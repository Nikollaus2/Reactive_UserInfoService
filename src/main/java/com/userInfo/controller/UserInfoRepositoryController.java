package com.userInfo.controller;

import com.userInfo.domain.UserInfo;
import com.userInfo.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Consumer;


@RestController
@RequestMapping("/orderSearchService")
@RequiredArgsConstructor
@Slf4j
public class UserInfoRepositoryController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    UserInfoService orderSearchService;



    @GetMapping(value = "/order/phone", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserInfo> getUserInfoByPhone(@RequestHeader(value = "requestId", required = false) String requestId,@RequestParam String phoneNumber) {
        Flux<UserInfo> result = orderSearchService.getUserInfoByPhone(phoneNumber)
                .doOnEach(logOnNext(r ->log.info("Found User Info {}", r.toString())))
                .contextWrite(Context.of("CONTEXT_KEY", requestId));
        return result;
    }

    public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
        return signal -> {
            if (!signal.isOnNext()) return;
            String contextValue = signal.getContextView().get("CONTEXT_KEY");
            try (MDC.MDCCloseable cMdc = MDC.putCloseable("MDC_KEY", contextValue)) {
                logStatement.accept(signal.get());
            }
        };
    }
}
