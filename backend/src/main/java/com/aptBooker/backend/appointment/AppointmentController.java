package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AppointmentErrorResponse;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponse;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponseDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentRequestDto createAppointmentRequestDto,
                                               @RequestHeader("Authorization") String authHeader){
        //extract userId from jwt in header
        //extrair userID do jwt token
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);

        try {
            AppointmentEntity appointment = appointmentService.createAppointment(createAppointmentRequestDto, userId);

            AppointmentResponse response = new AppointmentResponse();
            response.setId(appointment.getId());
            response.setUserId(appointment.getUserId());
            response.setServiceId(appointment.getServiceId());
            response.setShopId(appointment.getShopId());
            response.setAppointmentDate(appointment.getAppointmentDate());
            response.setAppointmentTime(appointment.getAppointmentTime());
            response.setStatus(appointment.getStatus());
            response.setCreatedAt(appointment.getCreatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            AppointmentErrorResponse appointmentErrorResponse = new AppointmentErrorResponse();
            appointmentErrorResponse.setErrorCode("CREATING APPOINTMENT FAILED");
            appointmentErrorResponse.setErrorMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentErrorResponse);
        }
    }

    @GetMapping("/available-times")
    public ResponseEntity<?> getAvailableTimes(
            @RequestParam Long shopId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat LocalDate date) {
        try {
            AvailableTimesResponse response = appointmentService.getAvailableTimes(shopId, serviceId, date);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AppointmentErrorResponse appointmentErrorResponse = new AppointmentErrorResponse();
            appointmentErrorResponse.setErrorCode("FETCH_AVAILABLE_TIMES_FAILED");
            appointmentErrorResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentErrorResponse);
        }
    }

    @GetMapping("/confirmed/{shopId}")
    public ResponseEntity<?> getConfirmedAppointmentsByShopId(@PathVariable Long shopId,
                                                             @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);
        try {
            List<AppointmentEntity> appointments = appointmentService.getConfirmedAppointmentsByShopId(shopId, userId);
            List<AppointmentResponseDto> response = appointments.stream().map(appointment -> {
                AppointmentResponseDto dto = new AppointmentResponseDto();
                dto.setId(appointment.getId());
                dto.setUserId(appointment.getUserId());
                dto.setServiceId(appointment.getServiceId());
                dto.setShopId(appointment.getShopId());
                dto.setAppointmentDate(appointment.getAppointmentDate());
                dto.setAppointmentTime(appointment.getAppointmentTime());
                dto.setStatus(appointment.getStatus());
                return dto;
            }).toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AppointmentErrorResponse error = new AppointmentErrorResponse();
            error.setErrorCode("FETCH_CONFIRMED_APPOINTMENTS_FAILED");
            error.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
