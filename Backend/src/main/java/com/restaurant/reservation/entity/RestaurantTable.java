package com.restaurant.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Location location;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}
