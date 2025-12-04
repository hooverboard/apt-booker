package com.aptBooker.backend.shop;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;

@Data
@Entity
@Table(name = "shops")
public class ShopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Long hostId;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

//    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ServiceEntity> services = new ArrayList<>();


}
