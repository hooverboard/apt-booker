package com.aptBooker.backend.services.dto.response;

import lombok.Data;

@Data
public class ServiceErrorResponse {

    private String errorMessage;

    private String errorCode;
}

