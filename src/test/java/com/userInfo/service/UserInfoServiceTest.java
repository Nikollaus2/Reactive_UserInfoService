package com.userInfo.service;

import com.github.jenspiegsa.wiremockextension.ConfigureWireMock;
import com.github.jenspiegsa.wiremockextension.InjectServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.Options;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WireMockExtension.class)
public class UserInfoServiceTest {

    @InjectServer
    WireMockServer wireMockServer;

    @ConfigureWireMock
    Options options1 = wireMockConfig().
            port(8080)
            .notifier(new ConsoleNotifier(true));

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/")
            .build();

    OrderSearchService orderSearchService = new OrderSearchService(webClient);
    ProductInfoService productInfoService = new ProductInfoService(webClient);
    UserInfoService userInfoService = new UserInfoService(orderSearchService, productInfoService);

    @Test
    void successfulUserInfoRequest() {
        //given
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader("requestId", "888888")
                        .withBodyFile("order-info.json")));
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=2005"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("product-info.json")));

        //when
        var orderInfoFlux = userInfoService.getUserInfoByPhone("123")
                .contextWrite(Context.of("CONTEXT_KEY", "requestId"));

        //then
        StepVerifier.create(orderInfoFlux)
                .assertNext( userInfo -> {
                    assertEquals("2005", userInfo.getProductCode());
                    assertEquals("123", userInfo.getPhoneNumber());
                    assertEquals("1", userInfo.getOrderNumber());
                    assertEquals("111", userInfo.getProductId());
                })
                .assertNext( userInfo -> {
                    assertEquals("2005", userInfo.getProductCode());
                    assertEquals("123", userInfo.getPhoneNumber());
                    assertEquals("1", userInfo.getOrderNumber());
                    assertEquals("222", userInfo.getProductId());
                        }

                )
                .verifyComplete();
    }

    @Test
    void productInfoFail() {
        //given
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("order-info.json")));
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=2005"))
                .willReturn(aResponse().withStatus(400).withBody("product not found")));

        //when
        var orderInfoFlux = userInfoService.getUserInfoByPhone("123")
                .contextWrite(Context.of("CONTEXT_KEY", "requestId"));

        //then
        StepVerifier.create(orderInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void orderSearchFail() {
        //given
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=123"))
                .willReturn(aResponse().withStatus(400).withBody("product not found")));
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=2005"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("product-info.json")));

        //when
        var orderInfoFlux = userInfoService.getUserInfoByPhone("123")
                .contextWrite(Context.of("CONTEXT_KEY", "requestId"));

        //then
        StepVerifier.create(orderInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

}
