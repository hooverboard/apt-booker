package com.aptBooker.backend.appointment;

import com.aptBooker.backend.appointment.dto.request.CreateAppointmentRequestDto;
import com.aptBooker.backend.appointment.dto.response.AvailableTimesResponse;
import com.aptBooker.backend.services.ServiceEntity;
import com.aptBooker.backend.services.ServiceRepository;
import com.aptBooker.backend.shop.ShopEntity;
import com.aptBooker.backend.shop.ShopRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final ShopRepository shopRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ServiceRepository serviceRepository,
                              ShopRepository shopRepository){
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.shopRepository = shopRepository;
    }

    public AppointmentEntity createAppointment(CreateAppointmentRequestDto createAppointmentRequestDto, Long userId){
        Long serviceId = createAppointmentRequestDto.getServiceId();
        Long shopId = createAppointmentRequestDto.getShopId();
        LocalDate appointmentDate = createAppointmentRequestDto.getAppointmentDate();
        LocalTime appointmentTime = createAppointmentRequestDto.getAppointmentTime();

        // check if shopId is equal to the service's shopID
        // verificar se o serviceID pertence ao shopId correto
        ServiceEntity service = serviceRepository.findById(serviceId) // grab service entity
                .orElseThrow(() -> new RuntimeException("Service not found"));

        //grab shop entity
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (!service.getShop().getId().equals(shopId)){
            throw new RuntimeException("Service does not belong to this ship");
        }

//         appointment time validation
        // verificar horario de agendamento
        LocalTime rightNow = LocalTime.now();
        LocalDate today =  LocalDate.now();

        if(appointmentDate.isBefore(today)){
            throw new RuntimeException("Cannot book appointments in the past");
        }

        if(appointmentDate.isEqual(today)){
            if(appointmentTime.isBefore(rightNow)){
                throw new RuntimeException("Cannot book appointments in the past");
            }
        }

        //validate within store hours
        Integer serviceDuration = service.getDuration();
        LocalTime openingTime = shop.getOpeningTime();
        LocalTime closingTime = shop.getClosingTime();
        LocalTime appointmentEndTime = appointmentTime.plusMinutes(serviceDuration);

        if (appointmentTime.isBefore(openingTime)){
            throw new RuntimeException("Appointment must be within store hours");
        }
        if (appointmentEndTime.isAfter(closingTime)){
            throw new RuntimeException("Appointment must end before closing hours");
        }

        //check if time overlaps existing appointments
        // verificar se ja nao existe um agendamento no mesmo horario
        List<AppointmentEntity> existingAppointments = appointmentRepository
                .findByShopIdAndAppointmentDateAndStatus(shopId, appointmentDate, "confirmed");

        for (AppointmentEntity existing : existingAppointments) {
            ServiceEntity existingService = serviceRepository.findById(existing.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Existing service not found"));

            Integer existingServiceDuration = existingService.getDuration();
            LocalTime existingAppointmentStarttime = existing.getAppointmentTime();
            LocalTime existingAppointmentEndTime = existingAppointmentStarttime.plusMinutes(existingServiceDuration);

            if (appointmentTime.isAfter(existingAppointmentStarttime) && appointmentTime.isBefore(existingAppointmentEndTime)){
                throw new RuntimeException("Appointment already exists at for this timeslot");
            }

            if (appointmentEndTime.isBefore(existingAppointmentEndTime) && appointmentEndTime.isAfter(existingAppointmentStarttime)){
                throw new RuntimeException("Appointment already exists at for this timeslot");
            }

            if (appointmentTime.isBefore(existingAppointmentStarttime) && appointmentEndTime.isAfter(existingAppointmentEndTime)){
                throw new RuntimeException("Appointment already exists at for this timeslot");
            }

            if (appointmentTime.equals(existingAppointmentStarttime) && appointmentEndTime.equals(existingAppointmentEndTime)){
                throw new RuntimeException("Appointment already exists at for this timeslot");
            }
        }



        //create response entity
        //criar resposta
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setUserId(userId);
        appointmentEntity.setServiceId(serviceId);
        appointmentEntity.setShopId(shopId);
        appointmentEntity.setAppointmentDate(appointmentDate);
        appointmentEntity.setAppointmentTime(appointmentTime);
        return appointmentRepository.save(appointmentEntity);
    }

    public AvailableTimesResponse getAvailableTimes(Long shopId, Long serviceId, LocalDate date) {
        // get the shop and service
        //buscar o shop e servico
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // verify service belongs to shop
        //verificar que o servico pertence ao shop
        if (!service.getShop().getId().equals(shopId)) {
            throw new RuntimeException("Service does not belong to this shop");
        }

        LocalTime openingTime = shop.getOpeningTime();
        LocalTime closingTime = shop.getClosingTime();
        Integer serviceDuration = service.getDuration();

        // generate all  time slots for every 30 minutes
        //gerar horarios disponiveis para cada 30 minutos
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime currentSlot = openingTime;
        
        while (currentSlot.plusMinutes(serviceDuration).isBefore(closingTime) || 
               currentSlot.plusMinutes(serviceDuration).equals(closingTime)) {
            allSlots.add(currentSlot);
            currentSlot = currentSlot.plusMinutes(30);
        }

        // filter out past times if the date is today
        // remover horarios no pasado
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (date.equals(today)) {
            allSlots.removeIf(slot -> slot.isBefore(now));
        }

        // get existing appointments for this shop and date
        // buscar agendamentos que ja existem desta data para o shop
        List<AppointmentEntity> existingAppointments = appointmentRepository
                .findByShopIdAndAppointmentDateAndStatus(shopId, date, "confirmed");

        // remove slots that overlap with existing appointments
        // remover horarios que ja estao agendados
        allSlots.removeIf(slot -> {
            LocalTime slotEndTime = slot.plusMinutes(serviceDuration);
            
            for (AppointmentEntity existing : existingAppointments) {
                ServiceEntity existingService = serviceRepository.findById(existing.getServiceId())
                        .orElse(null);
                if (existingService == null) continue;

                LocalTime existingStart = existing.getAppointmentTime();
                LocalTime existingEnd = existingStart.plusMinutes(existingService.getDuration());

                // Check for any overlap
                if ((slot.isBefore(existingEnd) && slotEndTime.isAfter(existingStart)) ||
                    slot.equals(existingStart)) {
                    return true; // remover se este horario ja for agendado
                }
            }
            return false;
        });

        AvailableTimesResponse response = new AvailableTimesResponse();
        response.setAvailableTimes(allSlots);
        return response;
    }
}
