package com.aptBooker.backend.shop;

import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import com.aptBooker.backend.shop.dto.request.UpdateShopRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    //create shop method
    //criar shop
    public ShopEntity createShop(CreateShopRequestDto createShopRequestDto, Long hostId){
        //extract shop details from dto
        //extrair detalhes do shop do dto

        String name = createShopRequestDto.getName();
        String address = createShopRequestDto.getAddress();
        String phoneNumber = createShopRequestDto.getPhoneNumber();
        String description = createShopRequestDto.getDescription();
        String imageUrl = createShopRequestDto.getImageUrl();
        LocalTime openingTime = createShopRequestDto.getOpeningTime();
        LocalTime closingTime = createShopRequestDto.getClosingTime();

        // Debug log to check what imageUrl value is being received
        System.out.println("DEBUG - Received imageUrl: '" + imageUrl + "' (is null: " + (imageUrl == null) + ", is empty: " + (imageUrl != null && imageUrl.isEmpty()) + ")");

        //validation
        //validacao
        if(hostId == null){
            throw new RuntimeException("Host ID is required");
        }

        if(closingTime.isBefore(openingTime)){
            throw new RuntimeException("Closing time must be after opening time");
        }

        //create new shop and save
        //criar shop novo e salvar
        ShopEntity newShop = new ShopEntity();
        newShop.setName(name);
        newShop.setAddress(address);
        newShop.setPhoneNumber(phoneNumber);
        newShop.setDescription(description);
        newShop.setImageUrl(imageUrl);
        newShop.setOpeningTime(openingTime);
        newShop.setClosingTime(closingTime);
        newShop.setHostId(hostId);
        shopRepository.save(newShop);

        return newShop;
    }

    // get all shops
    // get request para todos os shops

    public List<ShopEntity> getAllShops(){
        return shopRepository.findAll();
    }

    // get shops by host id
    // buscar shops do host por id
    public List<ShopEntity> getShopsByHostId(Long hostId) {
        return shopRepository.findByHostId(hostId);
    }

    // get shop by id
    public ShopEntity getShopById(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
    }

    // update shop
    // atualizar shop
    public ShopEntity updateShop(Long shopId, UpdateShopRequestDto updateShopRequestDto, Long hostId) {
        // Find the shop
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // Verify that the shop belongs to the host
        if (!shop.getHostId().equals(hostId)) {
            throw new RuntimeException("You are not authorized to update this shop");
        }

        // Validate times
        LocalTime openingTime = updateShopRequestDto.getOpeningTime();
        LocalTime closingTime = updateShopRequestDto.getClosingTime();

        if (closingTime.isBefore(openingTime)) {
            throw new RuntimeException("Closing time must be after opening time");
        }

        // Update shop fields
        shop.setName(updateShopRequestDto.getName());
        shop.setAddress(updateShopRequestDto.getAddress());
        shop.setDescription(updateShopRequestDto.getDescription());
        shop.setPhoneNumber(updateShopRequestDto.getPhoneNumber());
        shop.setImageUrl(updateShopRequestDto.getImageUrl());
        shop.setOpeningTime(openingTime);
        shop.setClosingTime(closingTime);

        return shopRepository.save(shop);
    }
}
