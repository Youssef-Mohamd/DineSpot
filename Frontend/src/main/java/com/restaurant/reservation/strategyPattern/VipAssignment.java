package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component("vip")
public class VipAssignment implements TableAssignmentStrategy {

    @Override
    public RestaurantTable assign(List<RestaurantTable> tables, int guests) {

        return tables.stream()
                .filter(t -> t.getCapacity() >= guests)
                .max(Comparator.comparingInt(RestaurantTable::getCapacity))
                .orElseThrow(() -> new RuntimeException("No suitable table"));
    }
}