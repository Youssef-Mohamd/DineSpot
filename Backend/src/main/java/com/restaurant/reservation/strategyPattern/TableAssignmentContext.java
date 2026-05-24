package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.Restaurant;
import com.restaurant.reservation.entity.RestaurantTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TableAssignmentContext {
    private final Map<String, TableAssignmentStrategy> strategies;
    
    public RestaurantTable assignTable(Restaurant restaurant, List<RestaurantTable> availableTables, int guestCount) {
        String strategyName = restaurant.getAssignmentStrategy();
        if (strategyName == null || strategyName.isBlank()) {
            strategyName = "bestFit";
        }
        TableAssignmentStrategy strategy = strategies.get(strategyName.toLowerCase());
        if (strategy == null) {
            strategy = strategies.get("bestFit"); // Safe global fallback
        }
        if (strategy == null) {
            throw new RuntimeException("No table assignment strategy found");
        }
        return strategy.assignTable(availableTables, guestCount);
    }
}
