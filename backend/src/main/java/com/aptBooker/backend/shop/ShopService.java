package com.aptBooker.backend.shop;

import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

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
        LocalTime openingTime = createShopRequestDto.getOpeningTime();
        LocalTime closingTime = createShopRequestDto.getClosingTime();

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
        newShop.setOpeningTime(openingTime);
        newShop.setClosingTime(closingTime);
        newShop.setHostId(hostId);
        shopRepository.save(newShop);

        return newShop;
    }
}
