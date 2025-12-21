package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponseDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.security.JwtUtil;
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
    public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody CreateAppointmentRequestDto createAppointmentRequestDto,
                                                                    @RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);

        AppointmentResponseDto response = appointmentService.createAppointment(createAppointmentRequestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/available-times")
    public ResponseEntity<AvailableTimesResponse> getAvailableTimes(
            @RequestParam Long shopId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AvailableTimesResponse response = appointmentService.getAvailableTimes(shopId, serviceId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirmed/{shopId}")
    public ResponseEntity<List<AppointmentResponseDto>> getConfirmedAppointmentsByShopId(@PathVariable Long shopId,
                                                                                           @RequestHeader("Authorization") String authHeader,
                                                                                           @RequestParam(name = "type", required = false, defaultValue = "upcoming") String type,
                                                                                           @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);
        List<AppointmentResponseDto> response = appointmentService.getConfirmedAppointmentsByShopId(shopId, userId, type, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getUserAppointment(@RequestHeader("Authorization") String authHeader,
                                                                             @RequestParam(name = "type", required = false, defaultValue = "all") String type) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);
        List<AppointmentResponseDto> response = appointmentService.getUserAppointments(userId, type);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> deleteAppointment(@RequestHeader("Authorization") String authHeader,
                                                                    @PathVariable Long appointmentId){
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);
        AppointmentResponseDto deletedAppointment = appointmentService.deleteAppointment(userId, appointmentId);
        return ResponseEntity.ok(deletedAppointment); // Or ResponseEntity.noContent().build();
    }
}
