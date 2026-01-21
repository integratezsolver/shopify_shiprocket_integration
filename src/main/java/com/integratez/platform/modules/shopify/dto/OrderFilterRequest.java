package com.integratez.platform.modules.shopify.dto;

import lombok.Data;

@Data
public class OrderFilterRequest {
    private String startDate;
    private String endDate;
    private Integer limit = 20;
    private String cursor; // optional for pagination
}

