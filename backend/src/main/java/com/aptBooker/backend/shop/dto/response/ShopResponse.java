package com.aptBooker.backend.shop.dto.response;

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
    private LocalTime openingTime;
    private LocalTime closingTime;
    private List<Long> serviceIds;
}

