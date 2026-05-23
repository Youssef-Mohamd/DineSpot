package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;

import java.util.List;

public interface TableAssignmentStrategy {
    RestaurantTable assign(List<RestaurantTable> availableTables, int guestCount);
}