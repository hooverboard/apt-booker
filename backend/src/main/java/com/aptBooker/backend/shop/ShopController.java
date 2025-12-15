package com.aptBooker.backend.shop;

import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import com.aptBooker.backend.shop.dto.request.UpdateShopRequestDto;
import com.aptBooker.backend.shop.dto.response.ShopErrorResponse;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.aptBooker.backend.security.JwtUtil;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createShop(@Valid @RequestBody CreateShopRequestDto createShopRequestDto,
                                        @RequestHeader("Authorization") String authHeader){

        //extract hostId from jwt in header
        String token = authHeader.replace("Bearer ", "");
        String userRole = jwtUtil.extractRole(token);
        Long hostId = jwtUtil.extractUserid(token);

        if(!"host".equals(userRole)){
            ShopErrorResponse shopErrorResponse = new ShopErrorResponse();
            shopErrorResponse.setErrorCode("FORBIDDEN");
            shopErrorResponse.setErrorMessage("Only hosts can create shops");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(shopErrorResponse);
        }

        try {


            ShopEntity shop = shopService.createShop(createShopRequestDto, hostId);

            ShopResponse shopResponse = new ShopResponse();
            shopResponse.setId(shop.getId());
            shopResponse.setName(shop.getName());
            shopResponse.setAddress(shop.getAddress());
            shopResponse.setDescription(shop.getDescription());
            shopResponse.setPhoneNumber(shop.getPhoneNumber());
            shopResponse.setOpeningTime(shop.getOpeningTime());
            shopResponse.setClosingTime(shop.getClosingTime());
            shopResponse.setHostId(hostId);
            shopResponse.setImageUrl(shop.getImageUrl());
            shopResponse.setServices(shop.getServices().stream()
                    .map(service -> {
                        ServiceResponse serviceResponse = new ServiceResponse();
                        serviceResponse.setId(service.getId());
                        serviceResponse.setName(service.getName());
                        serviceResponse.setPrice(service.getPrice());
                        serviceResponse.setDuration(service.getDuration());
                        serviceResponse.setDescription(service.getDescription());
                        return serviceResponse;
                    })
                    .toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(shopResponse);
        } catch (Exception e) {
            ShopErrorResponse shopErrorResponse = new ShopErrorResponse();
            shopErrorResponse.setErrorMessage(e.getMessage());
            shopErrorResponse.setErrorCode("CREATING SHOP FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(shopErrorResponse);
        }
    }

    //GET all shops
    //get todos os shops
    @GetMapping
    public ResponseEntity<?> getAllShops() {
        try {
            List<ShopEntity> shops = shopService.getAllShops();

            List<ShopResponse> shopResponses = shops.stream()
                    .map(shop -> {
                        ShopResponse response = new ShopResponse();
                        response.setId(shop.getId());
                        response.setName(shop.getName());
                        response.setAddress(shop.getAddress());
                        response.setDescription(shop.getDescription());
                        response.setPhoneNumber(shop.getPhoneNumber());
                        response.setOpeningTime(shop.getOpeningTime());
                        response.setClosingTime(shop.getClosingTime());
                        response.setHostId(shop.getHostId());
                        response.setImageUrl(shop.getImageUrl());
                        response.setServices(shop.getServices().stream()
                                .map(service -> {
                                    ServiceResponse serviceResponse = new ServiceResponse();
                                    serviceResponse.setId(service.getId());
                                    serviceResponse.setName(service.getName());
                                    serviceResponse.setPrice(service.getPrice());
                                    serviceResponse.setDuration(service.getDuration());
                                    serviceResponse.setDescription(service.getDescription());

                                    return serviceResponse;
                                })
                                .toList());
                        return response;
                    })
                    .toList();

            return ResponseEntity.ok(shopResponses);
        } catch (Exception e) {
            ShopErrorResponse error = new ShopErrorResponse();
            error.setErrorMessage(e.getMessage());
            error.setErrorCode("GET SHOPS REQUEST FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    //get shops by host
    //buscar shops do host
    @GetMapping("/my-shops")
    public ResponseEntity<?> getMyShops(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract hostId from JWT
            String token = authHeader.replace("Bearer ", "");
            Long hostId = jwtUtil.extractUserid(token);

            List<ShopEntity> shops = shopService.getShopsByHostId(hostId);

            List<ShopResponse> shopResponses = shops.stream()
                    .map(shop -> {
                        ShopResponse response = new ShopResponse();
                        response.setId(shop.getId());
                        response.setName(shop.getName());
                        response.setAddress(shop.getAddress());
                        response.setDescription(shop.getDescription());
                        response.setPhoneNumber(shop.getPhoneNumber());
                        response.setOpeningTime(shop.getOpeningTime());
                        response.setClosingTime(shop.getClosingTime());
                        response.setHostId(shop.getHostId());
                        response.setImageUrl(shop.getImageUrl());
                        response.setServices(shop.getServices().stream()
                                .map(service -> {
                                    ServiceResponse serviceResponse = new ServiceResponse();
                                    serviceResponse.setId(service.getId());
                                    serviceResponse.setName(service.getName());
                                    serviceResponse.setPrice(service.getPrice());
                                    serviceResponse.setDuration(service.getDuration());
                                    serviceResponse.setDescription(service.getDescription());
                                    return serviceResponse;
                                })
                                .toList());
                        return response;
                    })
                    .toList();

            return ResponseEntity.ok(shopResponses);
        } catch (Exception e) {
            ShopErrorResponse error = new ShopErrorResponse();
            error.setErrorMessage(e.getMessage());
            error.setErrorCode("GET MY SHOPS REQUEST FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    //UPDATE shop
    //atualizar shop
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShop(@PathVariable Long id,
                                        @Valid @RequestBody UpdateShopRequestDto updateShopRequestDto,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract hostId from JWT
            String token = authHeader.replace("Bearer ", "");
            Long hostId = jwtUtil.extractUserid(token);

            ShopEntity updatedShop = shopService.updateShop(id, updateShopRequestDto, hostId);

            ShopResponse response = new ShopResponse();
            response.setId(updatedShop.getId());
            response.setName(updatedShop.getName());
            response.setAddress(updatedShop.getAddress());
            response.setDescription(updatedShop.getDescription());
            response.setPhoneNumber(updatedShop.getPhoneNumber());
            response.setOpeningTime(updatedShop.getOpeningTime());
            response.setClosingTime(updatedShop.getClosingTime());
            response.setHostId(updatedShop.getHostId());
            response.setImageUrl(updatedShop.getImageUrl());
            response.setServices(updatedShop.getServices().stream()
                    .map(service -> {
                        ServiceResponse serviceResponse = new ServiceResponse();
                        serviceResponse.setId(service.getId());
                        serviceResponse.setName(service.getName());
                        serviceResponse.setPrice(service.getPrice());
                        serviceResponse.setDuration(service.getDuration());
                        serviceResponse.setDescription(service.getDescription());
                        return serviceResponse;
                    })
                    .toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ShopErrorResponse error = new ShopErrorResponse();
            error.setErrorMessage(e.getMessage());
            error.setErrorCode("UPDATE REQUEST FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    //GET shop by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getShopById(@PathVariable Long id) {
        try {
            ShopEntity shop = shopService.getShopById(id);
            ShopResponse response = new ShopResponse();
            response.setId(shop.getId());
            response.setName(shop.getName());
            response.setAddress(shop.getAddress());
            response.setDescription(shop.getDescription());
            response.setPhoneNumber(shop.getPhoneNumber());
            response.setOpeningTime(shop.getOpeningTime());
            response.setClosingTime(shop.getClosingTime());
            response.setHostId(shop.getHostId());
            response.setImageUrl(shop.getImageUrl());
            response.setServices(shop.getServices().stream()
                    .map(service -> {
                        ServiceResponse serviceResponse = new ServiceResponse();
                        serviceResponse.setId(service.getId());
                        serviceResponse.setName(service.getName());
                        serviceResponse.setPrice(service.getPrice());
                        serviceResponse.setDuration(service.getDuration());
                        serviceResponse.setDescription(service.getDescription());
                        return serviceResponse;
                    })
                    .toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ShopErrorResponse error = new ShopErrorResponse();
            error.setErrorMessage(e.getMessage());
            error.setErrorCode("GET SHOP BY ID FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShop(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable("id") Long shopId){
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);

        try{
            ShopEntity deleteShop = shopService.deleteShop(hostId, shopId);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ShopErrorResponse error = new ShopErrorResponse();
            error.setErrorMessage(e.getMessage());
            error.setErrorCode("GET SHOP BY ID FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
