package com.aptBooker.backend.user.dto.response;

import lombok.Data;

@Data
public class UserErrorDto {

    private String errorMessage;

    private String errorCode;
}
