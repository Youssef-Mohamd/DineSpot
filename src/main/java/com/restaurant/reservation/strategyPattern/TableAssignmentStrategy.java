package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;

import java.util.List;

@FunctionalInterface
public interface TableAssignmentStrategy {
    RestaurantTable assignTable(List<RestaurantTable> availableTables, int guestCount);
}