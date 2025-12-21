package com.aptBooker.backend.shop;

import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import com.aptBooker.backend.shop.dto.request.UpdateShopRequestDto;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ShopResponse> createShop(@Valid @RequestBody CreateShopRequestDto createShopRequestDto,
                                                 @RequestHeader("Authorization") String authHeader){

        String token = authHeader.replace("Bearer ", "");
        String userRole = jwtUtil.extractRole(token);
        Long hostId = jwtUtil.extractUserid(token);

        if(!"host".equals(userRole)){
            throw new UnauthorizedActionException("Only hosts can create shops");
        }

        ShopResponse shopResponse = shopService.createShop(createShopRequestDto, hostId);
        return ResponseEntity.status(HttpStatus.CREATED).body(shopResponse);
    }

    @GetMapping
    public ResponseEntity<List<ShopResponse>> getAllShops() {
        List<ShopResponse> shopResponses = shopService.getAllShops();
        return ResponseEntity.ok(shopResponses);
    }

    @GetMapping("/my-shops")
    public ResponseEntity<List<ShopResponse>> getMyShops(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);
        List<ShopResponse> shopResponses = shopService.getShopsByHostId(hostId);
        return ResponseEntity.ok(shopResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopResponse> updateShop(@PathVariable Long id,
                                     @Valid @RequestBody UpdateShopRequestDto updateShopRequestDto,
                                     @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);
        ShopResponse updatedShop = shopService.updateShop(id, updateShopRequestDto, hostId);
        return ResponseEntity.ok(updatedShop);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShopById(@PathVariable Long id) {
        ShopResponse shopResponse = shopService.getShopById(id);
        return ResponseEntity.ok(shopResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@RequestHeader("Authorization") String authHeader,
                                     @PathVariable("id") Long shopId){
        String token = authHeader.replace("Bearer ", "");
        Long hostId = jwtUtil.extractUserid(token);
        shopService.deleteShop(hostId, shopId);
        return ResponseEntity.noContent().build();
    }
}
