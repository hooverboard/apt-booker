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

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/shop")
public class ShopController {

    private ShopService shopService;

    public ResponseEntity<?> createShop(@Valid @RequestBody CreateShopRequestDto createShopRequestDto){
        try {

            // extract host id from jwt and pass as second argument

            ShopEntity shop = shopService.createShop(createShopRequestDto);

            ShopResponse shopResponse = new ShopResponse();
            shopResponse.setId(shop.getId());
            shopResponse.setName(shop.getName());
            shopResponse.setAddress(shop.getAddress());
            shopResponse.setDescription(shop.getDescription());
            shopResponse.setPhoneNumber(shop.getPhoneNumber());
            shopResponse.setOpeningTime(shop.getOpeningTime());
            shopResponse.setClosingTime(shop.getClosingTime());

            return ResponseEntity.status(HttpStatus.CREATED).body(shop);
        } catch (Exception e) {
            ShopErrorResponse shopErrorResponse = new ShopErrorResponse();
            shopErrorResponse.setErrorMessage(e.getMessage());
            shopErrorResponse.setErrorCode("CREATING SHOP FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(shopErrorResponse);
        }
    }
}
