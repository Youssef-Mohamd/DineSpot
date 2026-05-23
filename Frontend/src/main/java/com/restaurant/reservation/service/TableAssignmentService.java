package com.restaurant.reservation.service;

import com.restaurant.reservation.entity.RestaurantTable;
import com.restaurant.reservation.strategyPattern.TableAssignmentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TableAssignmentService {

    // get all sterategies
    private final Map<String, TableAssignmentStrategy> strategies;

    public RestaurantTable assignTable(String type,
                                       List<RestaurantTable> tables,
                                       int guestCount) {

        TableAssignmentStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new RuntimeException("Invalid strategy type: " + type);
        }

        return strategy.assign(tables, guestCount);
    }
}