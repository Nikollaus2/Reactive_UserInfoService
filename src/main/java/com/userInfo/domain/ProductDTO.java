package com.userInfo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private String productId;
    private String productCode;
    private String productName;
    private double score;

    public ProductDTO() {
    }
}
