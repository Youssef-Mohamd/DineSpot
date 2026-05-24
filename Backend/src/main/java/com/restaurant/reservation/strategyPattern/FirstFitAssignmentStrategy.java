package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("firstFit")
public class FirstFitAssignmentStrategy implements TableAssignmentStrategy {
    @Override
    public RestaurantTable assignTable(List<RestaurantTable> availableTables, int guestCount) {
        return availableTables.stream()
                .filter(t -> t.getCapacity() >= guestCount)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No suitable table available (First Fit)"));
    }
}
