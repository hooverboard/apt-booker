package com.aptBooker.backend.services;

import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.ServiceNotFoundInShopException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.services.dto.request.UpdateServiceRequestDto;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
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

    public ServiceResponse createService(CreateServiceDto createServiceDto, Long userId, String role){
        String name = createServiceDto.getName();
        String description = createServiceDto.getDescription();
        java.math.BigDecimal price = createServiceDto.getPrice();
        Integer duration = createServiceDto.getDuration();
        Long shopId = createServiceDto.getShopId();

        //make sure role is 'host'
        if(!"host".equals(role)){
            throw new UnauthorizedActionException("Only hosts can create services");
        }

        //verify shop exists
        ShopEntity shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) {
            throw new ResourceNotFoundException("Shop not found");
        }

        //verify user owns the shop
        if (!shop.getHostId().equals(userId)) {
            throw new UnauthorizedActionException("You are not authorized to add services to this shop");
        }

        ServiceEntity service = new ServiceEntity();
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDuration(duration);
        service.setShop(shop);

        ServiceEntity savedService = serviceRepository.save(service);

        //create dto
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setId(savedService.getId());
        serviceResponse.setName(savedService.getName());
        serviceResponse.setDescription(savedService.getDescription());
        serviceResponse.setPrice(savedService.getPrice());
        serviceResponse.setDuration(savedService.getDuration());
        serviceResponse.setShopId(savedService.getShop().getId());

        return serviceResponse;
    }

    public ServiceResponse updateService(UpdateServiceRequestDto updateServiceRequestDto, Long hostId){
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
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        if (!shop.getHostId().equals(hostId)){
            throw new UnauthorizedActionException("User does not own this shop");
        }

        //check if this service belong to the shop
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (!service.getShop().getId().equals(shopId)){
            throw new ServiceNotFoundInShopException("Service does not belong to this shop");
        }

        //update the service
        service.setName(name);
        service.setDescription(description);
        service.setDuration(duration);
        service.setPrice(price);

        ServiceEntity savedService = serviceRepository.save(service);

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setName(savedService.getName());
        serviceResponse.setDescription(savedService.getDescription());
        serviceResponse.setPrice(savedService.getPrice());
        serviceResponse.setDuration(savedService.getDuration());
        serviceResponse.setId(savedService.getId());
        serviceResponse.setShopId(savedService.getShop().getId());
        return serviceResponse;
    }

    public void deleteService(Long serviceId, Long hostId) {
        // get the service
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        // check is host owns the shop being deleted
        ShopEntity shop = service.getShop();
        if (!shop.getHostId().equals(hostId)) {
            throw new UnauthorizedActionException("User does not own this shop");
        }

        // delete service
        serviceRepository.delete(service);
    }
}
