package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponseDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final String fakeToken = "Bearer fake-token";
    private CreateAppointmentRequestDto createDto;
    private AppointmentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // The default ObjectMapper in Spring Boot test doesn't have the JavaTimeModule
        // registered by default, so we add it for proper serialization of LocalDate/Time.
        objectMapper.registerModule(new JavaTimeModule());

        createDto = new CreateAppointmentRequestDto();
        createDto.setShopId(1L);
        createDto.setServiceId(10L);
        createDto.setAppointmentDate(LocalDate.now().plusDays(1));
        createDto.setAppointmentTime(LocalTime.of(10, 0));

        responseDto = new AppointmentResponseDto();
        responseDto.setId(100L);
        responseDto.setUserId(1L);
    }

    // createAppointment tests
    @Test
    void createAppointment_success_returns201() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(1L);
        when(appointmentService.createAppointment(any(CreateAppointmentRequestDto.class), anyLong())).thenReturn(responseDto);

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void createAppointment_serviceNotFound_returns404() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(1L);
        when(appointmentService.createAppointment(any(CreateAppointmentRequestDto.class), anyLong()))
                .thenThrow(new ResourceNotFoundException("Service not found"));

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }

    // getAvailableTimes tests
    @Test
    void getAvailableTimes_success_returns200() throws Exception {
        AvailableTimesResponse availableTimes = new AvailableTimesResponse();
        availableTimes.setAvailableTimes(List.of(LocalTime.of(9, 0), LocalTime.of(10, 0)));
        when(appointmentService.getAvailableTimes(1L, 10L, createDto.getAppointmentDate())).thenReturn(availableTimes);

        mockMvc.perform(get("/api/appointments/available-times")
                        .param("shopId", "1")
                        .param("serviceId", "10")
                        .param("date", createDto.getAppointmentDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableTimes[0]").value("09:00:00"))
                .andExpect(jsonPath("$.availableTimes[1]").value("10:00:00"));
    }

    // getUserAppointment tests
    @Test
    void getUserAppointment_success_returns200() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(1L);
        when(appointmentService.getUserAppointments(1L, "all")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/appointments/my-appointments")
                        .header("Authorization", fakeToken)
                        .param("type", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L));
    }

    // deleteAppointment tests
    @Test
    void deleteAppointment_success_returns200() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(1L);
        when(appointmentService.deleteAppointment(1L, 100L)).thenReturn(responseDto);

        mockMvc.perform(delete("/api/appointments/100")
                        .header("Authorization", fakeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void deleteAppointment_unauthorized_returns401() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(2L); // Different user
        when(appointmentService.deleteAppointment(2L, 100L))
                .thenThrow(new UnauthorizedActionException("Not authorized"));

        mockMvc.perform(delete("/api/appointments/100")
                        .header("Authorization", fakeToken))
                .andExpect(status().isUnauthorized());
    }
}
