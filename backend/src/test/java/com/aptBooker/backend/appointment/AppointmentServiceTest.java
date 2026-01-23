package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.exceptions.ResourceNotFoundException;
import com.aptBooker.backend.exceptions.ServiceNotFoundInShopException;
import com.aptBooker.backend.exceptions.TimeMismatchException;
import com.aptBooker.backend.exceptions.TimeslotNotAvailableException;
import com.aptBooker.backend.services.ServiceEntity;
import com.aptBooker.backend.services.ServiceRepository;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import com.aptBooker.backend.user.UserEntity;
import com.aptBooker.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private UserEntity user;
    private ShopEntity shop;
    private ServiceEntity service;
    private CreateAppointmentRequestDto createDto;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        shop = new ShopEntity();
        shop.setId(10L);
        shop.setHostId(20L);
        shop.setOpeningTime(LocalTime.of(9, 0));
        shop.setClosingTime(LocalTime.of(18, 0));

        service = new ServiceEntity();
        service.setId(100L);
        service.setShop(shop);
        service.setDuration(60);

        createDto = new CreateAppointmentRequestDto();
        createDto.setUserId(1L);
        createDto.setShopId(10L);
        createDto.setServiceId(100L);
        createDto.setAppointmentDate(LocalDate.now().plusDays(1));
        createDto.setAppointmentTime(LocalTime.of(10, 0));
    }

    // createAppointment tests
    @Test
    void createAppointment_success_returnsDto() {
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatus(any(), any(), any())).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(AppointmentEntity.class))).thenAnswer(inv -> {
            AppointmentEntity app = inv.getArgument(0);
            app.setId(1000L);
            return app;
        });

        var result = appointmentService.createAppointment(createDto, 1L);

        assertNotNull(result);
        assertEquals(1000L, result.getId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void createAppointment_serviceNotFound_throwsResourceNotFoundException() {
        when(serviceRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.createAppointment(createDto, 1L));
    }

    @Test
    void createAppointment_inThePast_throwsTimeMismatchException() {
        createDto.setAppointmentDate(LocalDate.now().minusDays(1));
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(TimeMismatchException.class, () -> appointmentService.createAppointment(createDto, 1L));
    }

    @Test
    void createAppointment_outsideShopHours_throwsTimeMismatchException() {
        createDto.setAppointmentTime(LocalTime.of(20, 0)); // after closing
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(TimeMismatchException.class, () -> appointmentService.createAppointment(createDto, 1L));
    }

    @Test
    void createAppointment_conflictWithExisting_throwsTimeslotNotAvailableException() {
        AppointmentEntity existing = new AppointmentEntity();
        existing.setAppointmentTime(LocalTime.of(9, 30));
        existing.setService(service); // 60 min duration

        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatus(any(), any(), any())).thenReturn(List.of(existing));

        assertThrows(TimeslotNotAvailableException.class, () -> appointmentService.createAppointment(createDto, 1L));
    }

    @Test
    void createAppointment_serviceNotInShop_throwsServiceNotFoundInShopException() {
        ShopEntity anotherShop = new ShopEntity();
        anotherShop.setId(99L);
        service.setShop(anotherShop); // Service belongs to another shop

        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ServiceNotFoundInShopException.class, () -> appointmentService.createAppointment(createDto, 1L));
    }


    // getAvailableTimes tests
    @Test
    void getAvailableTimes_success_returnsAvailableTimesResponse() {
        shop.setOpeningTime(LocalTime.of(9, 0));
        shop.setClosingTime(LocalTime.of(11, 0));
        service.setDuration(30);

        AppointmentEntity existing = new AppointmentEntity();
        existing.setAppointmentTime(LocalTime.of(9, 30));
        ServiceEntity existingService = new ServiceEntity();
        existingService.setDuration(30);
        existing.setService(existingService);

        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatus(any(), any(), any())).thenReturn(List.of(existing));

        AvailableTimesResponse result = appointmentService.getAvailableTimes(10L, 100L, LocalDate.now().plusDays(1));

        assertNotNull(result);
        List<LocalTime> expectedTimes = List.of(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(10, 30)
        );
        assertEquals(expectedTimes.size(), result.getAvailableTimes().size());
        assertTrue(result.getAvailableTimes().containsAll(expectedTimes));
    }

    @Test
    void getAvailableTimes_serviceNotInShop_throwsServiceNotFoundInShopException() {
        ShopEntity anotherShop = new ShopEntity();
        anotherShop.setId(99L);
        service.setShop(anotherShop);

        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));
        when(serviceRepository.findById(100L)).thenReturn(Optional.of(service));

        assertThrows(ServiceNotFoundInShopException.class,
                () -> appointmentService.getAvailableTimes(10L, 100L, LocalDate.now()));
    }


    // deleteAppointment tests
    @Test
    void deleteAppointment_asBookingUser_deletesSuccessfully() {
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setId(1000L);
        appointment.setUserId(1L); // The user who booked
        appointment.setShop(shop);

        when(appointmentRepository.findById(1000L)).thenReturn(Optional.of(appointment));
        Mockito.lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));


        assertDoesNotThrow(() -> appointmentService.deleteAppointment(1L, 1000L));
    }

    @Test
    void deleteAppointment_asShopHost_deletesSuccessfully() {
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setId(1000L);
        appointment.setUserId(1L);
        appointment.setShop(shop); // Shop is hosted by user 20L
        UserEntity booker = new UserEntity();
        booker.setId(1L);

        when(appointmentRepository.findById(1000L)).thenReturn(Optional.of(appointment));
        Mockito.lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        assertDoesNotThrow(() -> appointmentService.deleteAppointment(20L, 1000L));
    }

    @Test
    void deleteAppointment_unauthorizedUser_throwsUnauthorizedActionException() {
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setId(1000L);
        appointment.setUserId(1L);
        appointment.setShop(shop);

        when(appointmentRepository.findById(1000L)).thenReturn(Optional.of(appointment));

        // User 99L is neither the booker (1L) nor the host (20L)
        assertThrows(Exception.class,
                () -> appointmentService.deleteAppointment(99L, 1000L));
    }

    @Test
    void deleteAppointment_notFound_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(1000L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.deleteAppointment(1L, 1000L));
    }
}
