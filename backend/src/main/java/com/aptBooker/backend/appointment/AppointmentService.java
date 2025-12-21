package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AppointmentResponseDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.exceptions.*;
import com.aptBooker.backend.services.ServiceEntity;
import com.aptBooker.backend.services.ServiceRepository;
import com.aptBooker.backend.services.dto.response.ServiceResponse;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import com.aptBooker.backend.shop.dto.response.ShopResponse;
import com.aptBooker.backend.user.UserEntity;
import com.aptBooker.backend.user.UserRepository;
import com.aptBooker.backend.user.dto.request.UserRegistrationDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ServiceRepository serviceRepository,
                              ShopRepository shopRepository,
                              UserRepository userRepository){
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    public AppointmentResponseDto createAppointment(CreateAppointmentRequestDto createAppointmentRequestDto, Long userId){
        Long serviceId = createAppointmentRequestDto.getServiceId();
        Long shopId = createAppointmentRequestDto.getShopId();
        LocalDate appointmentDate = createAppointmentRequestDto.getAppointmentDate();
        LocalTime appointmentTime = createAppointmentRequestDto.getAppointmentTime();

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!service.getShop().getId().equals(shopId)){
            throw new ServiceNotFoundInShopException("Service does not belong to this shop");
        }

        LocalTime rightNow = LocalTime.now();
        LocalDate today =  LocalDate.now();

        if(appointmentDate.isBefore(today) || (appointmentDate.isEqual(today) && appointmentTime.isBefore(rightNow))){
            throw new TimeMismatchException("Cannot book appointments in the past");
        }

        Integer serviceDuration = service.getDuration();
        LocalTime openingTime = shop.getOpeningTime();
        LocalTime closingTime = shop.getClosingTime();
        LocalTime appointmentEndTime = appointmentTime.plusMinutes(serviceDuration);

        if (appointmentTime.isBefore(openingTime) || appointmentEndTime.isAfter(closingTime)){
            throw new TimeMismatchException("Appointment must be within store hours");
        }

        List<AppointmentEntity> existingAppointments = appointmentRepository
                .findByShopAndAppointmentDateAndStatus(shop, appointmentDate, "confirmed");

        for (AppointmentEntity existing : existingAppointments) {
            LocalTime existingStart = existing.getAppointmentTime();
            LocalTime existingEnd = existingStart.plusMinutes(existing.getService().getDuration());

            if (appointmentTime.isBefore(existingEnd) && appointmentEndTime.isAfter(existingStart)) {
                throw new TimeslotNotAvailableException("Appointment conflicts with an existing appointment");
            }
        }

        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setUserId(userId);
        appointmentEntity.setService(service);
        appointmentEntity.setShop(shop);
        appointmentEntity.setAppointmentDate(appointmentDate);
        appointmentEntity.setAppointmentTime(appointmentTime);
        AppointmentEntity savedAppointment = appointmentRepository.save(appointmentEntity);

        AppointmentResponseDto responseDto = new AppointmentResponseDto();
        responseDto.setId(savedAppointment.getId());
        responseDto.setUserId(savedAppointment.getUserId());
        responseDto.setAppointmentDate(savedAppointment.getAppointmentDate());
        responseDto.setAppointmentTime(savedAppointment.getAppointmentTime());
        responseDto.setStatus(savedAppointment.getStatus());

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setId(savedAppointment.getService().getId());
        serviceResponse.setName(savedAppointment.getService().getName());
        serviceResponse.setDescription(savedAppointment.getService().getDescription());
        serviceResponse.setPrice(savedAppointment.getService().getPrice());
        serviceResponse.setDuration(savedAppointment.getService().getDuration());
        responseDto.setService(serviceResponse);

        ShopResponse shopResponse = new ShopResponse();
        shopResponse.setId(savedAppointment.getShop().getId());
        shopResponse.setName(savedAppointment.getShop().getName());
        shopResponse.setAddress(savedAppointment.getShop().getAddress());
        shopResponse.setPhoneNumber(savedAppointment.getShop().getPhoneNumber());
        responseDto.setShop(shopResponse);

        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        responseDto.setUser(userDto);

        return responseDto;
    }

    public AvailableTimesResponse getAvailableTimes(Long shopId, Long serviceId, LocalDate date) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (!service.getShop().getId().equals(shopId)) {
            throw new ServiceNotFoundInShopException("Service does not belong to this shop");
        }

        LocalTime openingTime = shop.getOpeningTime();
        LocalTime closingTime = shop.getClosingTime();
        Integer serviceDuration = service.getDuration();

        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime currentSlot = openingTime;
        
        while (currentSlot.plusMinutes(serviceDuration).isBefore(closingTime) ||
               currentSlot.plusMinutes(serviceDuration).equals(closingTime)) {
            allSlots.add(currentSlot);
            currentSlot = currentSlot.plusMinutes(30);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (date.equals(today)) {
            allSlots.removeIf(slot -> slot.isBefore(now));
        }

        List<AppointmentEntity> existingAppointments = appointmentRepository
                .findByShopAndAppointmentDateAndStatus(shop, date, "confirmed");

        allSlots.removeIf(slot -> {
            LocalTime slotEndTime = slot.plusMinutes(serviceDuration);
            for (AppointmentEntity existing : existingAppointments) {
                LocalTime existingStart = existing.getAppointmentTime();
                LocalTime existingEnd = existingStart.plusMinutes(existing.getService().getDuration());
                if (slot.isBefore(existingEnd) && slotEndTime.isAfter(existingStart)) {
                    return true;
                }
            }
            return false;
        });

        AvailableTimesResponse response = new AvailableTimesResponse();
        response.setAvailableTimes(allSlots);
        return response;
    }

    public List<AppointmentResponseDto> getConfirmedAppointmentsByShopId(Long shopId, Long userId, String type, LocalDate date) {
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        if (!userId.equals(shop.getHostId())){
            throw new UnauthorizedActionException("User does not own the shop");
        }

        List<AppointmentEntity> appointments;
        if (date == null ) {
            appointments = appointmentRepository.findByShop(shop);
        } else {
            appointments = appointmentRepository.findByShopAndAppointmentDate(shop, date);
        }
        
        LocalDate today = LocalDate.now();
        LocalTime rightNow = LocalTime.now();

        Stream<AppointmentEntity> filteredStream = appointments.stream();

        if("upcoming".equalsIgnoreCase(type)){
            filteredStream = filteredStream.filter(a -> a.getAppointmentDate().isAfter(today) || (a.getAppointmentDate().isEqual(today) && a.getAppointmentTime().isAfter(rightNow)));
        } else if ("past".equalsIgnoreCase(type)) {
            filteredStream = filteredStream.filter(a -> a.getAppointmentDate().isBefore(today) || (a.getAppointmentDate().isEqual(today) && a.getAppointmentTime().isBefore(rightNow)));
        }

        return filteredStream.map(appointment -> {
            AppointmentResponseDto responseDto = new AppointmentResponseDto();
            responseDto.setId(appointment.getId());
            responseDto.setUserId(appointment.getUserId());
            responseDto.setAppointmentDate(appointment.getAppointmentDate());
            responseDto.setAppointmentTime(appointment.getAppointmentTime());
            responseDto.setStatus(appointment.getStatus());

            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setId(appointment.getService().getId());
            serviceResponse.setName(appointment.getService().getName());
            serviceResponse.setDescription(appointment.getService().getDescription());
            serviceResponse.setPrice(appointment.getService().getPrice());
            serviceResponse.setDuration(appointment.getService().getDuration());
            responseDto.setService(serviceResponse);

            ShopResponse shopResponse = new ShopResponse();
            shopResponse.setId(appointment.getShop().getId());
            shopResponse.setName(appointment.getShop().getName());
            shopResponse.setAddress(appointment.getShop().getAddress());
            shopResponse.setPhoneNumber(appointment.getShop().getPhoneNumber());
            responseDto.setShop(shopResponse);

            userRepository.findById(appointment.getUserId()).ifPresent(user -> {
                UserRegistrationDto userDto = new UserRegistrationDto();
                userDto.setName(user.getName());
                userDto.setEmail(user.getEmail());
                responseDto.setUser(userDto);
            });
            
            return responseDto;
        }).collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getUserAppointments(Long userId, String type) {
        List<AppointmentEntity> all = appointmentRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();
        LocalTime rightNow = LocalTime.now();

        Stream<AppointmentEntity> filteredStream = all.stream();

        if ("upcoming".equalsIgnoreCase(type)) {
            filteredStream = filteredStream.filter(a -> a.getAppointmentDate().isAfter(today) || (a.getAppointmentDate().isEqual(today) && a.getAppointmentTime().isAfter(rightNow)));
        } else if ("past".equalsIgnoreCase(type)) {
            filteredStream = filteredStream.filter(a -> a.getAppointmentDate().isBefore(today) || (a.getAppointmentDate().isEqual(today) && a.getAppointmentTime().isBefore(rightNow)));
        }

        return filteredStream.map(appointment -> {
            AppointmentResponseDto responseDto = new AppointmentResponseDto();
            responseDto.setId(appointment.getId());
            responseDto.setUserId(appointment.getUserId());
            responseDto.setAppointmentDate(appointment.getAppointmentDate());
            responseDto.setAppointmentTime(appointment.getAppointmentTime());
            responseDto.setStatus(appointment.getStatus());

            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setId(appointment.getService().getId());
            serviceResponse.setName(appointment.getService().getName());
            serviceResponse.setDescription(appointment.getService().getDescription());
            serviceResponse.setPrice(appointment.getService().getPrice());
            serviceResponse.setDuration(appointment.getService().getDuration());
            responseDto.setService(serviceResponse);

            ShopResponse shopResponse = new ShopResponse();
            shopResponse.setId(appointment.getShop().getId());
            shopResponse.setName(appointment.getShop().getName());
            shopResponse.setAddress(appointment.getShop().getAddress());
            shopResponse.setPhoneNumber(appointment.getShop().getPhoneNumber());
            responseDto.setShop(shopResponse);
            
            return responseDto;
        }).collect(Collectors.toList());
    }

    public AppointmentResponseDto deleteAppointment(Long userId, Long appointmentId){
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        ShopEntity shop = appointment.getShop();

        if (!userId.equals(shop.getHostId()) && !userId.equals(appointment.getUserId())){
            throw new UnauthorizedActionException("You are not authorized to delete this appointment");
        }

        AppointmentResponseDto responseDto = new AppointmentResponseDto();
        responseDto.setId(appointment.getId());
        responseDto.setUserId(appointment.getUserId());
        responseDto.setAppointmentDate(appointment.getAppointmentDate());
        responseDto.setAppointmentTime(appointment.getAppointmentTime());
        responseDto.setStatus(appointment.getStatus());

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setId(appointment.getService().getId());
        serviceResponse.setName(appointment.getService().getName());
        serviceResponse.setDescription(appointment.getService().getDescription());
        serviceResponse.setPrice(appointment.getService().getPrice());
        serviceResponse.setDuration(appointment.getService().getDuration());
        responseDto.setService(serviceResponse);

        ShopResponse shopResponse = new ShopResponse();
        shopResponse.setId(appointment.getShop().getId());
        shopResponse.setName(appointment.getShop().getName());
        shopResponse.setAddress(appointment.getShop().getAddress());
        shopResponse.setPhoneNumber(appointment.getShop().getPhoneNumber());
        responseDto.setShop(shopResponse);

        userRepository.findById(appointment.getUserId()).ifPresent(user -> {
            UserRegistrationDto userDto = new UserRegistrationDto();
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            responseDto.setUser(userDto);
        });
        
        appointmentRepository.delete(appointment);
        return responseDto;
    }
}
