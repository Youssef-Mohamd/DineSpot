package com.restaurant.reservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatisticsResponse {
    private Long totalUsers;
    private Long totalRestaurants;
    private Long totalReservations;
    private Long activeReservations;
    private Long cancelledReservations;
    private Long totalTables;
    private Long totalNotifications;
    private Long unreadNotifications;
}
