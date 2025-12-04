package com.aptBooker.backend.shop.dto.response;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ShopResponse {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String phoneNumber;
    private Long hostId;
    private LocalTime openingTime;
    private LocalTime closingTime;
}

