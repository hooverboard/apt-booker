package com.aptBooker.backend.services.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Long shopId;
}

