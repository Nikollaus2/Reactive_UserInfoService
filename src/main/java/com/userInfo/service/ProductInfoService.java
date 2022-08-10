package com.userInfo.service;

import com.userInfo.domain.OrderDTO;
import com.userInfo.domain.ProductDTO;
import com.userInfo.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.userInfo.controller.UserInfoRepositoryController.logOnNext;

@Component
@Slf4j
public class ProductInfoService {

    WebClient webClient;

    public ProductInfoService() {
        webClient = WebClient.create("http://localhost:8082");
    }

    public ProductInfoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<UserInfo> retrieveUserInfo(OrderDTO orderDTO){

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/productInfoService/product/names")
                        .queryParam("productCode", "{productCode}")
                        .build(orderDTO.getProductCode()))
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .doOnEach(logOnNext(productDTO -> log.info(productDTO.toString())))
                .map(productDTO -> new UserInfo(
                        orderDTO.getOrderNumber(),
                        productDTO.getProductId(),
                        orderDTO.getProductCode(),
                        orderDTO.getPhoneNumber()))
                .log();
    }
}
