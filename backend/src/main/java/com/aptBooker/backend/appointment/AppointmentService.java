package com.aptBooker.backend.appointment;

import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

    public AppointmentEntity createAppointment(CreateAppointmentRequestDto createAppointmentRequestDto){

    }
}
