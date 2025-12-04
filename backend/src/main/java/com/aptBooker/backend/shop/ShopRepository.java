package com.aptBooker.backend.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    //find all shops by host id
    // encontrar todos os shops por id do host
    List<ShopEntity> findByHostId(Long hostId);

    //find ship by name
    //encontrar shop por nome
    Optional<ShopEntity> findByNameIgnoreCase(String name);

    //check if a host has a shop
    //checar se um host tem shop
    boolean existsByHostId(Long hostId);
}
