package com.aptBooker.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;
}
