package com.aptBooker.backend.shop;

import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.UnauthorizedActionException;
import com.aptBooker.backend.security.JwtUtil;
import com.aptBooker.backend.shop.dto.request.CreateShopRequestDto;
import com.aptBooker.backend.shop.dto.request.UpdateShopRequestDto;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShopService shopService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShop_success_returns201() throws Exception {
        CreateShopRequestDto request = new CreateShopRequestDto();
        request.setName("Test Shop");
        request.setAddress("Test Address");
        request.setPhoneNumber("1231231234");
        request.setDescription("Valid description with enough length");
        request.setImageUrl("imageUrl");
        request.setOpeningTime(LocalTime.of(9, 0));
        request.setClosingTime(LocalTime.of(20, 0));

        ShopResponse response = new ShopResponse();
        response.setId(1L);
        response.setName("Test Shop");

        when(jwtUtil.extractRole("fake-token")).thenReturn("host");
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(shopService.createShop(Mockito.any(CreateShopRequestDto.class), Mockito.eq(10L)))
                .thenReturn(response);

        mockMvc.perform(post("/api/shops")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Shop"));
    }

    @Test
    void createShop_whenNotHost_returns401() throws Exception {
        CreateShopRequestDto request = new CreateShopRequestDto();
        request.setName("Test Shop");
        request.setAddress("Test Address");
        request.setPhoneNumber("1231231234");
        request.setDescription("Valid description with enough length");
        request.setImageUrl("imageUrl");
        request.setOpeningTime(LocalTime.of(9, 0));
        request.setClosingTime(LocalTime.of(20, 0));

        when(jwtUtil.extractRole("fake-token")).thenReturn("user");

        mockMvc.perform(post("/api/shops")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllShops_success_returns200() throws Exception {
        ShopResponse shop = new ShopResponse();
        shop.setId(1L);
        shop.setName("Test Shop");

        when(shopService.getAllShops()).thenReturn(List.of(shop));

        mockMvc.perform(get("/api/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Shop"));
    }

    @Test
    void getShopById_success_returns200() throws Exception {
        ShopResponse shop = new ShopResponse();
        shop.setId(1L);
        shop.setName("Test Shop");

        when(shopService.getShopById(1L)).thenReturn(shop);

        mockMvc.perform(get("/api/shops/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Shop"));
    }

    @Test
    void getShopById_notFound_returns404() throws Exception {
        when(shopService.getShopById(1L))
                .thenThrow(new ResourceNotFoundException("Shop not found"));

        mockMvc.perform(get("/api/shops/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShop_success_returns200() throws Exception {
        UpdateShopRequestDto request = new UpdateShopRequestDto();
        request.setName("Updated Shop");
        request.setAddress("New Address");
        request.setPhoneNumber("1231231234");
        request.setDescription("Valid description with enough length");
        request.setImageUrl("newImageUrl");
        request.setOpeningTime(LocalTime.of(9, 0));
        request.setClosingTime(LocalTime.of(20, 0));

        ShopResponse response = new ShopResponse();
        response.setId(1L);
        response.setName("Updated Shop");

        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(shopService.updateShop(Mockito.eq(1L), Mockito.any(UpdateShopRequestDto.class), Mockito.eq(10L)))
                .thenReturn(response);

        mockMvc.perform(put("/api/shops/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Shop"));
    }

    @Test
    void updateShop_unauthorized_returns401() throws Exception {
        UpdateShopRequestDto request = new UpdateShopRequestDto();
        request.setName("Updated Shop");
        request.setAddress("New Address");
        request.setPhoneNumber("1231231234");
        request.setDescription("Valid description with enough length");
        request.setImageUrl("imageUrl");
        request.setOpeningTime(LocalTime.of(9, 0));
        request.setClosingTime(LocalTime.of(20, 0));

        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        when(shopService.updateShop(Mockito.eq(1L), Mockito.any(UpdateShopRequestDto.class), Mockito.eq(10L)))
                .thenThrow(new UnauthorizedActionException("Not allowed"));

        mockMvc.perform(put("/api/shops/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShop_success_returns204() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);

        mockMvc.perform(delete("/api/shops/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShop_unauthorized_returns401() throws Exception {
        when(jwtUtil.extractUserid("fake-token")).thenReturn(10L);
        Mockito.doThrow(new UnauthorizedActionException("Not allowed"))
                .when(shopService).deleteShop(10L, 1L);

        mockMvc.perform(delete("/api/shops/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isUnauthorized());
    }
}
