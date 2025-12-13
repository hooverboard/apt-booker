package com.aptBooker.backend.appointment.dto.response;

import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
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
    private ServiceResponse service;
    private ShopResponse shop;
    private UserRegistrationDto user;

}

