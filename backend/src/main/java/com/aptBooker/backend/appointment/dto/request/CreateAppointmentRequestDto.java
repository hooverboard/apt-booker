package com.aptBooker.backend.appointment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateAppointmentRequestDto {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;
}

