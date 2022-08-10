package com.userInfo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String orderNumber;
    private String productId;
    private String productCode;
    private String phoneNumber;

    public UserInfo() {
    }
}
