package com.aptBooker.backend.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    //find shop's services with shopID
    //econtrar servicos de um shop com o ShopID
    List<ServiceEntity> findByShopId(Long shopId);

    //check if service exists via id and shopId
    //verificar ser service exite com id e shopid
    Boolean existsByIdAndShopId(Long id, Long shopId);
}
