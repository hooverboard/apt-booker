package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AppointmentErrorResponse;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponse;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponseDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.services.ServiceEntity;
import com.aptBooker.backend.services.ServiceRepository;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import com.aptBooker.backend.user.UserEntity;
import com.aptBooker.backend.user.UserRepository;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
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

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

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
            response.setServiceId(appointment.getService().getId());
            response.setShopId(appointment.getShop().getId());
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
                                                             @RequestHeader("Authorization") String authHeader,
                                                              @RequestParam(name = "type", required = false, defaultValue = "upcoming") String type,
                                                              @RequestParam(name = "date", required = false) LocalDate date) {
            ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);
        try {
            List<AppointmentEntity> appointments = appointmentService.getConfirmedAppointmentsByShopId(shopId, userId, type, date);
            List<AppointmentResponseDto> response = appointments.stream().map(appointment -> {
                AppointmentResponseDto dto = new AppointmentResponseDto();
                dto.setId(appointment.getId());
                dto.setUserId(appointment.getUserId());
                dto.setAppointmentDate(appointment.getAppointmentDate());
                dto.setAppointmentTime(appointment.getAppointmentTime());
                dto.setStatus(appointment.getStatus());

                ServiceEntity serviceItem = appointment.getService();
                if (serviceItem != null) {
                    ServiceResponse serviceResponse = new ServiceResponse();
                    serviceResponse.setId(serviceItem.getId());
                    serviceResponse.setName(serviceItem.getName());
                    serviceResponse.setDescription(serviceItem.getDescription());
                    serviceResponse.setPrice(serviceItem.getPrice());
                    serviceResponse.setDuration(serviceItem.getDuration());
                    dto.setService(serviceResponse);
                }

                ShopResponse shopResponse = new ShopResponse();
                shopResponse.setName(shop.getName());
                shopResponse.setPhoneNumber(shop.getPhoneNumber());
                shopResponse.setAddress(shop.getAddress());
                dto.setShop(shopResponse);


                userRepository.findById(appointment.getUserId()).ifPresent(userItem -> {
                    UserRegistrationDto userResponse = new UserRegistrationDto();
                    userResponse.setName(userItem.getName());
                    userResponse.setEmail(userItem.getEmail());
                    dto.setUser(userResponse);
                });

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






    @GetMapping("/my-appointments")
    public ResponseEntity<?> getUserAppointment(@RequestHeader("Authorization") String authHeader,
                                                @RequestParam(name = "type", required = false, defaultValue = "all") String type) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserid(token);

        try {
            List<AppointmentEntity> appointments = appointmentService.getUserAppointments(userId, type);
            List<AppointmentResponseDto> response = appointments.stream().map(appointment -> {
                AppointmentResponseDto dto = new AppointmentResponseDto();
                dto.setId(appointment.getId());
                dto.setUserId(appointment.getUserId());
                dto.setAppointmentDate(appointment.getAppointmentDate());
                dto.setAppointmentTime(appointment.getAppointmentTime());
                dto.setStatus(appointment.getStatus());

                ServiceEntity service = appointment.getService();
                if (service != null) {
                    ServiceResponse serviceResponse = new ServiceResponse();
                    serviceResponse.setId(service.getId());
                    serviceResponse.setName(service.getName());
                    serviceResponse.setDescription(service.getDescription());
                    serviceResponse.setPrice(service.getPrice());
                    serviceResponse.setDuration(service.getDuration());
                    dto.setService(serviceResponse);
                }

                ShopEntity shop = appointment.getShop();
                if (shop != null) {
                    ShopResponse shopResponse = new ShopResponse();
                    shopResponse.setName(shop.getName());
                    shopResponse.setPhoneNumber(shop.getPhoneNumber());
                    shopResponse.setAddress(shop.getAddress());
                    dto.setShop(shopResponse);
                }

                return dto;
            }).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AppointmentErrorResponse error = new AppointmentErrorResponse();
            error.setErrorCode("FETCH_USER_APPOINTMENTS_FAILED");
            error.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
