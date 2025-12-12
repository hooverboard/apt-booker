package com.aptBooker.backend.services;

import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.services.dto.request.UpdateServiceRequestDto;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import com.aptBooker.backend.user.UserEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ShopRepository shopRepository;

    public ServiceService(ServiceRepository serviceRepository, ShopRepository shopRepository){
        this.serviceRepository = serviceRepository;
        this.shopRepository = shopRepository;
    }

    public ServiceEntity createService(CreateServiceDto createServiceDto, Long userId){
        String name = createServiceDto.getName();
        String description = createServiceDto.getDescription();
        java.math.BigDecimal price = createServiceDto.getPrice();
        Integer duration = createServiceDto.getDuration();
        Long shopId = createServiceDto.getShopId();

        //verify shop exists
        ShopEntity shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) {
            throw new RuntimeException("Shop not found");
        }

        //verify user owns the shop
        if (!shop.getHostId().equals(userId)) {
            throw new RuntimeException("You are not authorized to add services to this shop");
        }

        ServiceEntity service = new ServiceEntity();
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDuration(duration);
        service.setShop(shop);

        return serviceRepository.save(service);
    }

    public ServiceEntity updateService(UpdateServiceRequestDto updateServiceRequestDto, Long hostId){
        //extract data from dto
        Long id = updateServiceRequestDto.getId();
        String name = updateServiceRequestDto.getName();
        String description = updateServiceRequestDto.getDescription();
        BigDecimal price = updateServiceRequestDto.getPrice();
        Integer duration = updateServiceRequestDto.getDuration();
        Long shopId = updateServiceRequestDto.getShopId();

        //edge cases

        //check if user owns the shop
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (!shop.getHostId().equals(hostId)){
            throw new RuntimeException("User does not own this shop");
        }

        //check if this service belong to the shop
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getShop().getId().equals(shopId)){
            throw new RuntimeException("Service does not belong to this shop");
        }

        //update the service
        service.setName(name);
        service.setDescription(description);
        service.setDuration(duration);
        service.setPrice(price);

        return serviceRepository.save(service);
    }
}
