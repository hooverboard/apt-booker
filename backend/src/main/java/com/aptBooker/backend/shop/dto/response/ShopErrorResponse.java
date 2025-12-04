package com.aptBooker.backend.shop.dto.response;

import lombok.Data;

@Data
public class ShopErrorResponse {

    private String errorMessage;

    private String errorCode;
}

