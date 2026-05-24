package com.restaurant.reservation.repository;

import com.restaurant.reservation.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByRestaurantId(Long restaurantId);
    List<RestaurantTable> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);
    List<RestaurantTable> findByRestaurantIdAndCapacityGreaterThanEqual(Long restaurantId, int capacity); // using sterategyPattern

    @Query("SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :restaurantId " +
           "AND t.id NOT IN (SELECT r.table.id FROM Reservation r " +
           "WHERE r.reservationDate = :date AND r.timeSlot.id = :slotId AND r.status != 'CANCELLED')")
    List<RestaurantTable> findAvailableTables(@Param("restaurantId") Long restaurantId, 
                                              @Param("date") java.time.LocalDate date, 
                                              @Param("slotId") Long slotId);
}