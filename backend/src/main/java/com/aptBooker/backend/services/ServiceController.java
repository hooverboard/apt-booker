package com.aptBooker.backend.services;

import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.services.dto.request.UpdateServiceRequestDto;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody CreateServiceDto createServiceDto,
                                           @RequestHeader("Authorization") String authHeader) {
        //extract jwt info
        String token = authHeader.replace("Bearer ", "");
        String userRole = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserid(token);

        ServiceResponse service = serviceService.createService(createServiceDto, userId, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PutMapping
    public ResponseEntity<ServiceResponse> updateService(@Valid @RequestBody UpdateServiceRequestDto updateServiceRequestDto,
                                           @RequestHeader("Authorization") String authHeader){
    // extract jwt info
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);

        ServiceResponse service = serviceService.updateService(updateServiceRequestDto, hostId);
        return ResponseEntity.status(HttpStatus.OK).body(service);
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);
        serviceService.deleteService(serviceId, hostId);
        return ResponseEntity.noContent().build();
    }
}
