package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("bestFit")
@Primary
public class BestFitAssignmentStrategy implements TableAssignmentStrategy {
    @Override
    public RestaurantTable assignTable(List<RestaurantTable> availableTables, int guestCount) {
        return availableTables.stream()
                .filter(t -> t.getCapacity() >= guestCount)
                .min(java.util.Comparator.comparingInt(RestaurantTable::getCapacity))
                .orElseThrow(() -> new RuntimeException("No suitable table available (Best Fit)"));
    }
}
