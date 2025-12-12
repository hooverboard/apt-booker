package com.aptBooker.backend.services;

import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.services.dto.request.UpdateServiceRequestDto;
import com.aptBooker.backend.services.dto.response.ServiceErrorResponse;
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
    public ResponseEntity<?> createService(@Valid @RequestBody CreateServiceDto createServiceDto,
                                           @RequestHeader("Authorization") String authHeader) {

        //extract userId and role from jwt in header
        String token = authHeader.replace("Bearer ", "");
        String userRole = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserid(token);

        //verify user is a host
        if(!"host".equals(userRole)){
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse();
            serviceErrorResponse.setErrorCode("FORBIDDEN");
            serviceErrorResponse.setErrorMessage("Only hosts can create services");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(serviceErrorResponse);
        }

        try {
            ServiceEntity service = serviceService.createService(createServiceDto, userId);

            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setId(service.getId());
            serviceResponse.setName(service.getName());
            serviceResponse.setDescription(service.getDescription());
            serviceResponse.setPrice(service.getPrice());
            serviceResponse.setDuration(service.getDuration());
            serviceResponse.setShopId(service.getShop().getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceResponse);
        } catch (Exception e) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse();
            serviceErrorResponse.setErrorMessage(e.getMessage());
            serviceErrorResponse.setErrorCode("CREATING SERVICE FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceErrorResponse);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateService(@Valid @RequestBody UpdateServiceRequestDto updateServiceRequestDto,
                                           @RequestHeader("Authorization") String authHeader){

        //extract info from jwt
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);

        try {
            ServiceEntity service = serviceService.updateService(updateServiceRequestDto, hostId);
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setName(service.getName());
            serviceResponse.setDescription(service.getDescription());
            serviceResponse.setPrice(service.getPrice());
            serviceResponse.setDuration(service.getDuration());
            serviceResponse.setId(service.getId());
            serviceResponse.setShopId(service.getShop().getId());

            return ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
        } catch (Exception e) {
            ServiceErrorResponse serviceErrorResponse = new ServiceErrorResponse();
            serviceErrorResponse.setErrorMessage(e.getMessage());
            serviceErrorResponse.setErrorCode("UPDATING SERVICE FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceErrorResponse);
        }

    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);
        try {
            serviceService.deleteService(serviceId, hostId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ServiceErrorResponse errorResponse = new ServiceErrorResponse();
            errorResponse.setErrorCode("DELETING SERVICE FAILED");
            errorResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
