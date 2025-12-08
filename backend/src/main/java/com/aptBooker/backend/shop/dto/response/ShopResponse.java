package com.aptBooker.backend.shop.dto.response;

import com.aptBooker.backend.services.dto.response.ServiceResponse;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class ShopResponse {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String phoneNumber;
    private Long hostId;
    private String imageUrl;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private List<ServiceResponse> services;
}

