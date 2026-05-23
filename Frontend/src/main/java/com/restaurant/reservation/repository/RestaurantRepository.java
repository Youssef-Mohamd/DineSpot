package com.restaurant.reservation.repository;

import com.restaurant.reservation.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByCity(String city);
    List<Restaurant> findByCuisineType(String cuisineType);
    List<Restaurant> findByCityAndCuisineType(String city, String cuisineType);
    List<Restaurant> findByIsActiveTrue();
}