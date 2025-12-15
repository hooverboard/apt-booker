package com.aptBooker.backend.appointment;

import com.aptBooker.backend.shop.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    //find all appointments for a specific shop
    //econtrar todos os agendamentos de uma loja
    List<AppointmentEntity> findByShop(ShopEntity shop);

    //find appointments by date
    //encontrar agedamentos por data
    List<AppointmentEntity> findByAppointmentDate(LocalDate date);

    //find appointments by shopId, Date and status
    //econtrar com shopId, data e status
    List<AppointmentEntity> findByShopAndAppointmentDateAndStatus(
            ShopEntity shop,
            LocalDate appointmentDate,
            String status
    );

    List<AppointmentEntity> findByShopAndStatus(ShopEntity shop, String Status);

    List<AppointmentEntity> findByUserId(Long userId);

    List<AppointmentEntity> findByShopAndAppointmentDate(ShopEntity shop, LocalDate appointmentDate);

}
