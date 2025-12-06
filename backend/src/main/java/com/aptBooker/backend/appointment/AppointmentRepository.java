package com.aptBooker.backend.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    //find all appointments for a specific shop
    //econtrar todos os agendamentos de uma loja
    List<AppointmentEntity> findByShopId(Long shopId);

    //find appointments by date
    //encontrar agedamentos por data
    List<AppointmentEntity> findByAppointmentDate(LocalDate date);


}
