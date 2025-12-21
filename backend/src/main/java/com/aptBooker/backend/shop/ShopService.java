package com.aptBooker.backend.shop;

import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.TimeMismatchException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import com.aptBooker.backend.shop.dto.request.UpdateShopRequestDto;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public ShopResponse createShop(CreateShopRequestDto createShopRequestDto, Long hostId){
        if(hostId == null){
            throw new UnauthorizedActionException("Host ID cannot be null");
        }

        if(createShopRequestDto.getClosingTime().isBefore(createShopRequestDto.getOpeningTime())){
            throw new TimeMismatchException("Closing time must be after opening time");
        }

        ShopEntity newShop = new ShopEntity();
        newShop.setName(createShopRequestDto.getName());
        newShop.setAddress(createShopRequestDto.getAddress());
        newShop.setPhoneNumber(createShopRequestDto.getPhoneNumber());
        newShop.setDescription(createShopRequestDto.getDescription());
        newShop.setImageUrl(createShopRequestDto.getImageUrl());
        newShop.setOpeningTime(createShopRequestDto.getOpeningTime());
        newShop.setClosingTime(createShopRequestDto.getClosingTime());
        newShop.setHostId(hostId);
        ShopEntity savedShop = shopRepository.save(newShop);

        ShopResponse shopResponse = new ShopResponse();
        shopResponse.setId(savedShop.getId());
        shopResponse.setName(savedShop.getName());
        shopResponse.setAddress(savedShop.getAddress());
        shopResponse.setDescription(savedShop.getDescription());
        shopResponse.setPhoneNumber(savedShop.getPhoneNumber());
        shopResponse.setOpeningTime(savedShop.getOpeningTime());
        shopResponse.setClosingTime(savedShop.getClosingTime());
        shopResponse.setHostId(savedShop.getHostId());
        shopResponse.setImageUrl(savedShop.getImageUrl());
        shopResponse.setServices(savedShop.getServices().stream()
                .map(service -> {
                    ServiceResponse serviceResponse = new ServiceResponse();
                    serviceResponse.setId(service.getId());
                    serviceResponse.setName(service.getName());
                    serviceResponse.setPrice(service.getPrice());
                    serviceResponse.setDuration(service.getDuration());
                    serviceResponse.setDescription(service.getDescription());
                    return serviceResponse;
                })
                .collect(Collectors.toList()));
        return shopResponse;
    }

    public List<ShopResponse> getAllShops(){
        List<ShopEntity> shops = shopRepository.findAll();
        return shops.stream()
                .map(shop -> {
                    ShopResponse shopResponse = new ShopResponse();
                    shopResponse.setId(shop.getId());
                    shopResponse.setName(shop.getName());
                    shopResponse.setAddress(shop.getAddress());
                    shopResponse.setDescription(shop.getDescription());
                    shopResponse.setPhoneNumber(shop.getPhoneNumber());
                    shopResponse.setOpeningTime(shop.getOpeningTime());
                    shopResponse.setClosingTime(shop.getClosingTime());
                    shopResponse.setHostId(shop.getHostId());
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
                            .collect(Collectors.toList()));
                    return shopResponse;
                })
                .collect(Collectors.toList());
    }

    public List<ShopResponse> getShopsByHostId(Long hostId) {
        List<ShopEntity> shops = shopRepository.findByHostId(hostId);
        return shops.stream()
                .map(shop -> {
                    ShopResponse shopResponse = new ShopResponse();
                    shopResponse.setId(shop.getId());
                    shopResponse.setName(shop.getName());
                    shopResponse.setAddress(shop.getAddress());
                    shopResponse.setDescription(shop.getDescription());
                    shopResponse.setPhoneNumber(shop.getPhoneNumber());
                    shopResponse.setOpeningTime(shop.getOpeningTime());
                    shopResponse.setClosingTime(shop.getClosingTime());
                    shopResponse.setHostId(shop.getHostId());
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
                            .collect(Collectors.toList()));
                    return shopResponse;
                })
                .collect(Collectors.toList());
    }

    public ShopResponse getShopById(Long shopId) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID: " + shopId + " not found"));
        ShopResponse shopResponse = new ShopResponse();
        shopResponse.setId(shop.getId());
        shopResponse.setName(shop.getName());
        shopResponse.setAddress(shop.getAddress());
        shopResponse.setDescription(shop.getDescription());
        shopResponse.setPhoneNumber(shop.getPhoneNumber());
        shopResponse.setOpeningTime(shop.getOpeningTime());
        shopResponse.setClosingTime(shop.getClosingTime());
        shopResponse.setHostId(shop.getHostId());
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
                .collect(Collectors.toList()));
        return shopResponse;
    }

    public ShopResponse updateShop(Long shopId, UpdateShopRequestDto updateShopRequestDto, Long hostId) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID: " + shopId + " not found"));

        if (!shop.getHostId().equals(hostId)) {
            throw new UnauthorizedActionException("You are not authorized to update this shop");
        }

        LocalTime openingTime = updateShopRequestDto.getOpeningTime();
        LocalTime closingTime = updateShopRequestDto.getClosingTime();

        if (closingTime.isBefore(openingTime)) {
            throw new TimeMismatchException("Closing time must be after opening time");
        }

        shop.setName(updateShopRequestDto.getName());
        shop.setAddress(updateShopRequestDto.getAddress());
        shop.setDescription(updateShopRequestDto.getDescription());
        shop.setPhoneNumber(updateShopRequestDto.getPhoneNumber());
        shop.setImageUrl(updateShopRequestDto.getImageUrl());
        shop.setOpeningTime(openingTime);
        shop.setClosingTime(closingTime);
        ShopEntity updatedShop = shopRepository.save(shop);

        ShopResponse shopResponse = new ShopResponse();
        shopResponse.setId(updatedShop.getId());
        shopResponse.setName(updatedShop.getName());
        shopResponse.setAddress(updatedShop.getAddress());
        shopResponse.setDescription(updatedShop.getDescription());
        shopResponse.setPhoneNumber(updatedShop.getPhoneNumber());
        shopResponse.setOpeningTime(updatedShop.getOpeningTime());
        shopResponse.setClosingTime(updatedShop.getClosingTime());
        shopResponse.setHostId(updatedShop.getHostId());
        shopResponse.setImageUrl(updatedShop.getImageUrl());
        shopResponse.setServices(updatedShop.getServices().stream()
                .map(service -> {
                    ServiceResponse serviceResponse = new ServiceResponse();
                    serviceResponse.setId(service.getId());
                    serviceResponse.setName(service.getName());
                    serviceResponse.setPrice(service.getPrice());
                    serviceResponse.setDuration(service.getDuration());
                    serviceResponse.setDescription(service.getDescription());
                    return serviceResponse;
                })
                .collect(Collectors.toList()));
        return shopResponse;
    }

    public void deleteShop(Long hostId, Long shopId){
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID: " + shopId + " not found"));

        if (!hostId.equals(shop.getHostId())){
            throw new UnauthorizedActionException("User does not own the shop");
        }

        shopRepository.delete(shop);
    }
}
