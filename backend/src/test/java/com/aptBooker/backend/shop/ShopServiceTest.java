package com.aptBooker.backend.shop;

import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.TimeMismatchException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.services.ServiceEntity;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import com.aptBooker.backend.shop.dto.request.UpdateShopRequestDto;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import com.aptBooker.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    ShopRepository shopRepository;

    @InjectMocks
    ShopService shopService;

    //create shop tests
    @Test
    void createShop_success_returnsShopResponse() {
        CreateShopRequestDto createShopRequestDto = new CreateShopRequestDto();
        createShopRequestDto.setName("test shop");
        createShopRequestDto.setAddress("test address");
        createShopRequestDto.setDescription("shop description");
        createShopRequestDto.setPhoneNumber("1231231234");
        createShopRequestDto.setImageUrl("imageUrl");
        createShopRequestDto.setOpeningTime(LocalTime.parse("09:00"));
        createShopRequestDto.setClosingTime(LocalTime.parse("20:00"));

        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setName("test shop");
        shop.setAddress("test address");
        shop.setDescription("shop description");
        shop.setPhoneNumber("1231231234");
        shop.setHostId(2L);
        shop.setImageUrl("imageUrl");
        shop.setOpeningTime(LocalTime.parse("09:00"));
        shop.setClosingTime(LocalTime.parse("20:00"));

        when(shopRepository.save(Mockito.any(ShopEntity.class)))
                .thenReturn(shop);

        var result = shopService.createShop(createShopRequestDto, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test shop", result.getName());
        assertEquals("test address", result.getAddress());
        assertEquals("shop description", result.getDescription());
        assertEquals("1231231234", result.getPhoneNumber());
        assertEquals(2L, result.getHostId());
        assertEquals("imageUrl", result.getImageUrl());
        assertEquals(LocalTime.parse("09:00"), result.getOpeningTime());
        assertEquals(LocalTime.parse("20:00"), result.getClosingTime());

    }

    @Test
    void createShop_nullHostId_throwsUnauthorizedActionException(){
        CreateShopRequestDto createShopRequestDto = new CreateShopRequestDto();
        createShopRequestDto.setName("test shop");
        createShopRequestDto.setAddress("test address");
        createShopRequestDto.setDescription("shop description");
        createShopRequestDto.setPhoneNumber("1231231234");
        createShopRequestDto.setImageUrl("imageUrl");
        createShopRequestDto.setOpeningTime(LocalTime.parse("09:00"));
        createShopRequestDto.setClosingTime(LocalTime.parse("20:00"));

        assertThrows(UnauthorizedActionException.class,
                () -> shopService.createShop(createShopRequestDto, null));
    }

    @Test
    void createShop_whenTimeMismatch_throwsTimeMismatchException(){
        CreateShopRequestDto createShopRequestDto = new CreateShopRequestDto();
        createShopRequestDto.setName("test shop");
        createShopRequestDto.setAddress("test address");
        createShopRequestDto.setDescription("shop description");
        createShopRequestDto.setPhoneNumber("1231231234");
        createShopRequestDto.setImageUrl("imageUrl");
        createShopRequestDto.setOpeningTime(LocalTime.parse("20:00"));
        createShopRequestDto.setClosingTime(LocalTime.parse("09:00"));

        assertThrows(TimeMismatchException.class,
                () -> shopService.createShop(createShopRequestDto, 2L));
    }


    //get all shops tests
    @Test
    void getAllShops_success_returnsShopResponseList() {
        ServiceEntity service = new ServiceEntity();
        service.setName("haircut");
        service.setDescription("mens haircut");
        service.setId(10L);
        service.setDuration(30);
        service.setPrice(BigDecimal.valueOf(50));

        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setName("test shop");
        shop.setAddress("test address");
        shop.setDescription("shop description");
        shop.setPhoneNumber("1231231234");
        shop.setHostId(2L);
        shop.setImageUrl("imageUrl");
        shop.setOpeningTime(LocalTime.parse("09:00"));
        shop.setClosingTime(LocalTime.parse("20:00"));
        shop.setServices(List.of(service));

        when(shopRepository.findAll())
                .thenReturn(List.of(shop));

        List<ShopResponse> result = shopService.getAllShops();

        assertEquals(1, result.size());

        ShopResponse response = result.get(0);
        assertEquals(1L, response.getId());
        assertEquals("test shop", response.getName());
        assertEquals("test address", response.getAddress());
        assertEquals("shop description", response.getDescription());
        assertEquals("1231231234", response.getPhoneNumber());
        assertEquals(2L, response.getHostId());
        assertEquals("imageUrl", response.getImageUrl());
        assertEquals(LocalTime.of(9, 0), response.getOpeningTime());
        assertEquals(LocalTime.of(20, 0), response.getClosingTime());

        assertEquals(1, response.getServices().size());
        ServiceResponse serviceResponse = response.getServices().get(0);
        assertEquals(10L, serviceResponse.getId());
        assertEquals("haircut", serviceResponse.getName());
        assertEquals(BigDecimal.valueOf(50), serviceResponse.getPrice());
        assertEquals(30, serviceResponse.getDuration());
        assertEquals("mens haircut", serviceResponse.getDescription());

    }

    @Test
    void getShopsByHostId_success_returnsShopResponseList() {
        ServiceEntity service = new ServiceEntity();
        service.setName("haircut");
        service.setDescription("mens haircut");
        service.setId(10L);
        service.setDuration(30);
        service.setPrice(BigDecimal.valueOf(50));

        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setName("test shop");
        shop.setAddress("test address");
        shop.setDescription("shop description");
        shop.setPhoneNumber("1231231234");
        shop.setHostId(2L);
        shop.setImageUrl("imageUrl");
        shop.setOpeningTime(LocalTime.parse("09:00"));
        shop.setClosingTime(LocalTime.parse("20:00"));
        shop.setServices(List.of(service));

        when(shopRepository.findByHostId(1L))
                .thenReturn(List.of(shop));

        List<ShopResponse> result = shopService.getShopsByHostId(1L);

        assertEquals(1, result.size());

        ShopResponse response = result.get(0);
        assertEquals(1L, response.getId());
        assertEquals("test shop", response.getName());
        assertEquals("test address", response.getAddress());
        assertEquals("shop description", response.getDescription());
        assertEquals("1231231234", response.getPhoneNumber());
        assertEquals(2L, response.getHostId());
        assertEquals("imageUrl", response.getImageUrl());
        assertEquals(LocalTime.of(9, 0), response.getOpeningTime());
        assertEquals(LocalTime.of(20, 0), response.getClosingTime());

        assertEquals(1, response.getServices().size());
        ServiceResponse serviceResponse = response.getServices().get(0);
        assertEquals(10L, serviceResponse.getId());
        assertEquals("haircut", serviceResponse.getName());
        assertEquals(BigDecimal.valueOf(50), serviceResponse.getPrice());
        assertEquals(30, serviceResponse.getDuration());
        assertEquals("mens haircut", serviceResponse.getDescription());

    }


    //get shop by ID tests
    @Test
    void getShopById() {
        ServiceEntity service = new ServiceEntity();
        service.setName("haircut");
        service.setDescription("mens haircut");
        service.setId(10L);
        service.setDuration(30);
        service.setPrice(BigDecimal.valueOf(50));

        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setName("test shop");
        shop.setAddress("test address");
        shop.setDescription("shop description");
        shop.setPhoneNumber("1231231234");
        shop.setHostId(2L);
        shop.setImageUrl("imageUrl");
        shop.setOpeningTime(LocalTime.parse("09:00"));
        shop.setClosingTime(LocalTime.parse("20:00"));
        shop.setServices(List.of(service));

        when(shopRepository.findById(1L))
                .thenReturn(Optional.of(shop));

        ShopResponse response = shopService.getShopById(1L);

        assertEquals(1L, response.getId());
        assertEquals("test shop", response.getName());
        assertEquals("test address", response.getAddress());
        assertEquals("shop description", response.getDescription());
        assertEquals("1231231234", response.getPhoneNumber());
        assertEquals(2L, response.getHostId());
        assertEquals("imageUrl", response.getImageUrl());
        assertEquals(LocalTime.of(9, 0), response.getOpeningTime());
        assertEquals(LocalTime.of(20, 0), response.getClosingTime());

        assertEquals(1, response.getServices().size());
        ServiceResponse serviceResponse = response.getServices().get(0);
        assertEquals(10L, serviceResponse.getId());
        assertEquals("haircut", serviceResponse.getName());
        assertEquals(BigDecimal.valueOf(50), serviceResponse.getPrice());
        assertEquals(30, serviceResponse.getDuration());
        assertEquals("mens haircut", serviceResponse.getDescription());
    }

    @Test
    void updateShop() {
        ServiceEntity service = new ServiceEntity();
        service.setName("haircut");
        service.setDescription("mens haircut");
        service.setId(10L);
        service.setDuration(30);
        service.setPrice(BigDecimal.valueOf(50));

        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setName("test shop");
        shop.setAddress("test address");
        shop.setDescription("shop description");
        shop.setPhoneNumber("1231231234");
        shop.setHostId(2L);
        shop.setImageUrl("imageUrl");
        shop.setOpeningTime(LocalTime.parse("09:00"));
        shop.setClosingTime(LocalTime.parse("20:00"));
        shop.setServices(List.of(service));

        UpdateShopRequestDto request = new UpdateShopRequestDto();
        request.setName("test shop");
        request.setAddress("test address");
        request.setDescription("shop description");
        request.setPhoneNumber("1231231234");
        request.setImageUrl("imageUrl");
        request.setOpeningTime(LocalTime.parse("09:00"));
        request.setClosingTime(LocalTime.parse("20:00"));

        when(shopRepository.findById(1L))
                .thenReturn(Optional.of(shop));

        when(shopRepository.save(Mockito.any(ShopEntity.class)))
                .thenReturn(shop);



        var result = shopService.updateShop(1L, request, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test shop", result.getName());
        assertEquals("test address", result.getAddress());
        assertEquals("shop description", result.getDescription());
        assertEquals("1231231234", result.getPhoneNumber());
        assertEquals(2L, result.getHostId());
        assertEquals("imageUrl", result.getImageUrl());
        assertEquals(LocalTime.of(9, 0), result.getOpeningTime());
        assertEquals(LocalTime.of(20, 0), result.getClosingTime());

        assertEquals(1, result.getServices().size());
        ServiceResponse serviceResponse = result.getServices().get(0);
        assertEquals(10L, serviceResponse.getId());
        assertEquals("haircut", serviceResponse.getName());
        assertEquals(BigDecimal.valueOf(50), serviceResponse.getPrice());
        assertEquals(30, serviceResponse.getDuration());
        assertEquals("mens haircut", serviceResponse.getDescription());
    }

    @Test
    void updateShop_whenShopDoesNotExist_throwsResourceNotFoundException() {
        UpdateShopRequestDto request = new UpdateShopRequestDto();
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shopService.updateShop(1L, request, 2L));
    }

    @Test
    void updateShopwhenHostIdMismatch_throwsUnauthorizedActionException_() {
        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setHostId(2L);

        UpdateShopRequestDto request = new UpdateShopRequestDto();
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        assertThrows(UnauthorizedActionException.class,
                () -> shopService.updateShop(1L, request, 10L));
    }

    @Test
    void updateShopwhenClosingBeforeOpening_throwsTimeMismatchException_() {
        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setHostId(2L);

        UpdateShopRequestDto request = new UpdateShopRequestDto();
        request.setOpeningTime(LocalTime.of(10, 0));
        request.setClosingTime(LocalTime.of(9, 0));

        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        assertThrows(TimeMismatchException.class,
                () -> shopService.updateShop(1L, request, 2L));
    }



    @Test
    void deleteShop_success() {
        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setHostId(2L);

        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        shopService.deleteShop(2L, 1L);
    }

    @Test
    void deleteShop_throwsResourceNotFoundException() {
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shopService.deleteShop(2L, 1L));
    }

    @Test
    void deleteShop_throwsUnauthorizedActionException() {
        ShopEntity shop = new ShopEntity();
        shop.setId(1L);
        shop.setHostId(2L);

        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        assertThrows(UnauthorizedActionException.class,
                () -> shopService.deleteShop(10L, 1L));
    }

}