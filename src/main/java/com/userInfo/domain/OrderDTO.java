package com.userInfo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDTO {
   private String phoneNumber;
   private String orderNumber;
   private String productCode;

   public OrderDTO() {
   }
}
