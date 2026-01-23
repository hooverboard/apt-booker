package com.aptBooker.backend.services;

import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.ServiceNotFoundInShopException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.services.dto.request.UpdateServiceRequestDto;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceService serviceService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateServiceDto createServiceDto;
    private UpdateServiceRequestDto updateServiceRequestDto;
    private ServiceResponse serviceResponse;
    private final String fakeToken = "Bearer fake-token";

    @BeforeEach
    void setUp() {
        createServiceDto = new CreateServiceDto();
        createServiceDto.setName("Test Service");
        createServiceDto.setDescription("A valid description for testing");
        createServiceDto.setPrice(BigDecimal.valueOf(100));
        createServiceDto.setDuration(60);
        createServiceDto.setShopId(1L);

        updateServiceRequestDto = new UpdateServiceRequestDto();
        updateServiceRequestDto.setId(1L);
        updateServiceRequestDto.setName("Updated Service");
        updateServiceRequestDto.setDescription("An updated valid description");
        updateServiceRequestDto.setPrice(BigDecimal.valueOf(120));
        updateServiceRequestDto.setDuration(75);
        updateServiceRequestDto.setShopId(1L);

        serviceResponse = new ServiceResponse();
        serviceResponse.setId(1L);
        serviceResponse.setName("Test Service");
        serviceResponse.setPrice(BigDecimal.valueOf(100));
        serviceResponse.setDuration(60);
        serviceResponse.setShopId(1L);
    }

    // createService tests
    @Test
    void createService_success_returns201() throws Exception {
        when(jwtUtil.extractRole("fake-token")).thenReturn("host");
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(serviceService.createService(any(CreateServiceDto.class), eq(10L), eq("host")))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/api/services")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Service"));
    }

    @Test
    void createService_whenNotHost_returns401() throws Exception {
        when(jwtUtil.extractRole("fake-token")).thenReturn("user");
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(serviceService.createService(any(CreateServiceDto.class), eq(10L), eq("user")))
                .thenThrow(new UnauthorizedActionException("Only hosts can create services"));

        mockMvc.perform(post("/api/services")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceDto)))
                .andExpect(status().isUnauthorized());
    }

    // updateService tests
    @Test
    void updateService_success_returns200() throws Exception {
        serviceResponse.setName("Updated Service");
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(serviceService.updateService(any(UpdateServiceRequestDto.class), eq(10L)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/api/services")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Service"));
    }

    @Test
    void updateService_whenServiceNotFound_returns404() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(serviceService.updateService(any(UpdateServiceRequestDto.class), eq(10L)))
                .thenThrow(new ResourceNotFoundException("Service not found"));

        mockMvc.perform(put("/api/services")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateService_whenServiceNotInShop_returns400() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(serviceService.updateService(any(UpdateServiceRequestDto.class), eq(10L)))
                .thenThrow(new ServiceNotFoundInShopException("Service does not belong to this shop"));

        mockMvc.perform(put("/api/services")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRequestDto)))
                .andExpect(status().isBadRequest());
    }

    // deleteService tests
    @Test
    void deleteService_success_returns204() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        Mockito.doNothing().when(serviceService).deleteService(1L, 10L);

        mockMvc.perform(delete("/api/services/1")
                        .header("Authorization", fakeToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteService_whenUserNotOwner_returns401() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        doThrow(new UnauthorizedActionException("User does not own this shop"))
                .when(serviceService).deleteService(1L, 10L);

        mockMvc.perform(delete("/api/services/1")
                        .header("Authorization", fakeToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteService_whenServiceNotFound_returns404() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        doThrow(new ResourceNotFoundException("Service not found"))
                .when(serviceService).deleteService(1L, 10L);

        mockMvc.perform(delete("/api/services/1")
                        .header("Authorization", fakeToken))
                .andExpect(status().isNotFound());
    }
}
