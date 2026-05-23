package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("firstFit")
public class FirstFitAssignment implements TableAssignmentStrategy {

    @Override
    public RestaurantTable assign(List<RestaurantTable> tables, int guests) {

        return tables.stream()
                .filter(t -> t.getCapacity() >= guests)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No suitable table"));
    }
}