package com.aptBooker.backend.appointment.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentResponseDto {
    private Long id;
    private Long userId;
    private Long serviceId;
    private Long shopId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

}

