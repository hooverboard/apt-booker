package com.aptBooker.backend.appointment.dto.response;

import lombok.Data;

@Data
public class AppointmentErrorResponse {

    private String errorCode;
    private String errorMessage;
}

