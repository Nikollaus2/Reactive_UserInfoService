package com.userInfo.service;

import com.userInfo.domain.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
//@Slf4j
public class OrderSearchService {

    WebClient webClient;

    public OrderSearchService() {
        webClient = WebClient.create("http://localhost:8081");
    }

    public OrderSearchService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<OrderDTO> retrieveOrderByPhone(String phoneNumber){
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/orderSearchService/order/phone")
                        .queryParam("phoneNumber", "{phoneNumber}")
                        .build(phoneNumber))
                .retrieve()
                .bodyToFlux(OrderDTO.class)
             //   .doOnEach(orderDTO -> log.info(orderDTO.toString()))
             //   .doOnComplete(() -> {log.info("retrieveOrderByPhone COMPLETED");})
                .log();
    }
}
