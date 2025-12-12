package com.aptBooker.backend.services.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateServiceRequestDto {
    @NotNull
    private Long id;

    @NotNull
    private Long shopId;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 5, max = 500)
    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer duration;

}
