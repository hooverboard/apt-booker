package com.aptBooker.backend.services;

import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.ServiceNotFoundInShopException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.services.dto.request.CreateServiceDto;
import com.aptBooker.backend.services.dto.request.UpdateServiceRequestDto;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ServiceService serviceService;

    private ShopEntity shop;
    private ServiceEntity service;
    private CreateServiceDto createServiceDto;
    private UpdateServiceRequestDto updateServiceRequestDto;

    @BeforeEach
    void setUp() {
        shop = new ShopEntity();
        shop.setId(1L);
        shop.setHostId(10L);
        shop.setName("Test Shop");

        service = new ServiceEntity();
        service.setId(100L);
        service.setName("Test Service");
        service.setDescription("A great service");
        service.setPrice(BigDecimal.valueOf(50.00));
        service.setDuration(60);
        service.setShop(shop);

        createServiceDto = new CreateServiceDto();
        createServiceDto.setName("New Service");
        createServiceDto.setDescription("A new service description");
        createServiceDto.setPrice(BigDecimal.valueOf(75.00));
        createServiceDto.setDuration(45);
        createServiceDto.setShopId(1L);

        updateServiceRequestDto = new UpdateServiceRequestDto();
        updateServiceRequestDto.setId(100L);
        updateServiceRequestDto.setName("Updated Service");
        updateServiceRequestDto.setDescription("An updated service description");
        updateServiceRequestDto.setPrice(BigDecimal.valueOf(80.00));
        updateServiceRequestDto.setDuration(50);
        updateServiceRequestDto.setShopId(1L);
    }

    // createService tests
    @Test
    void createService_success_returnsServiceResponse() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(serviceRepository.save(Mockito.any(ServiceEntity.class))).thenAnswer(invocation -> {
            ServiceEntity savedService = invocation.getArgument(0);
            savedService.setId(101L);
            return savedService;
        });

        ServiceResponse result = serviceService.createService(createServiceDto, 10L, "host");

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("New Service", result.getName());
        assertEquals(BigDecimal.valueOf(75.00), result.getPrice());
        assertEquals(45, result.getDuration());
        assertEquals(1L, result.getShopId());
    }

    @Test
    void createService_whenRoleIsNotHost_throwsUnauthorizedActionException() {
        assertThrows(UnauthorizedActionException.class,
                () -> serviceService.createService(createServiceDto, 10L, "user"));
    }

    @Test
    void createService_whenShopNotFound_throwsResourceNotFoundException() {
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceService.createService(createServiceDto, 10L, "host"));
    }

    @Test
    void createService_whenUserDoesNotOwnShop_throwsUnauthorizedActionException() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        assertThrows(UnauthorizedActionException.class,
                () -> serviceService.createService(createServiceDto, 99L, "host")); // Different user ID
    }

    // updateService tests
    @Test
    void updateService_success_returnsUpdatedServiceResponse() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(serviceRepository.save(Mockito.any(ServiceEntity.class))).thenReturn(service);

        ServiceResponse result = serviceService.updateService(updateServiceRequestDto, 10L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Updated Service", result.getName());
        assertEquals(BigDecimal.valueOf(80.00), result.getPrice());
        assertEquals(50, result.getDuration());
    }

    @Test
    void updateService_whenShopNotFound_throwsResourceNotFoundException() {
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceService.updateService(updateServiceRequestDto, 10L));
    }

    @Test
    void updateService_whenUserDoesNotOwnShop_throwsUnauthorizedActionException() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        assertThrows(UnauthorizedActionException.class,
                () -> serviceService.updateService(updateServiceRequestDto, 99L)); // Different host ID
    }

    @Test
    void updateService_whenServiceNotFound_throwsResourceNotFoundException() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(serviceRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceService.updateService(updateServiceRequestDto, 10L));
    }

    @Test
    void updateService_whenServiceNotInShop_throwsServiceNotFoundInShopException() {
        ShopEntity anotherShop = new ShopEntity();
        anotherShop.setId(2L);
        service.setShop(anotherShop); // Service belongs to a different shop

        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));

        assertThrows(ServiceNotFoundInShopException.class,
                () -> serviceService.updateService(updateServiceRequestDto, 10L));
    }

    // deleteService tests
    @Test
    void deleteService_success_deletesService() {
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));

        serviceService.deleteService(100L, 10L);

        verify(serviceRepository).delete(service);
    }

    @Test
    void deleteService_whenServiceNotFound_throwsResourceNotFoundException() {
        when(serviceRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceService.deleteService(100L, 10L));
    }

    @Test
    void deleteService_whenUserDoesNotOwnShop_throwsUnauthorizedActionException() {
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));

        assertThrows(UnauthorizedActionException.class,
                () -> serviceService.deleteService(100L, 99L)); // Different host ID
    }
}
