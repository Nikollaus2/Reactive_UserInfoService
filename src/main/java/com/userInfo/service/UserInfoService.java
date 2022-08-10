package com.userInfo.service;

import com.userInfo.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static com.userInfo.controller.UserInfoRepositoryController.logOnNext;

@Component
@Slf4j
public class UserInfoService {

    OrderSearchService orderSearchService;
    ProductInfoService productInfoService;

    public UserInfoService(OrderSearchService orderSearchService, ProductInfoService productInfoService) {
        this.orderSearchService = orderSearchService;
        this.productInfoService = productInfoService;
    }

    public Flux<UserInfo> getUserInfoByPhone(String phoneNumber) {

        return orderSearchService.retrieveOrderByPhone(phoneNumber)
                .flatMap(productInfoService::retrieveUserInfo)
                .publishOn(Schedulers.parallel())
                .doOnEach(logOnNext(userInfo -> log.info(userInfo.toString())))
                .onErrorReturn(new UserInfo());
    }

}
