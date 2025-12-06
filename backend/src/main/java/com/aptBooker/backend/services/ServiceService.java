package com.aptBooker.backend.services;

import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import org.springframework.stereotype.Service;

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
}
