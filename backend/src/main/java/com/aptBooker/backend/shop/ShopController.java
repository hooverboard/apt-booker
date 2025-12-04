package com.aptBooker.backend.shop;

import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
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

            return ResponseEntity.status(HttpStatus.CREATED).body(shopResponse);
        } catch (Exception e) {
            ShopErrorResponse shopErrorResponse = new ShopErrorResponse();
            shopErrorResponse.setErrorMessage(e.getMessage());
            shopErrorResponse.setErrorCode("CREATING SHOP FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(shopErrorResponse);
        }
    }
}
