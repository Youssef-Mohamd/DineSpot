package com.restaurant.reservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendResponse {
    
    private String message;
    private Integer totalSent;
    private Integer successCount;
    private Integer failureCount;
    private List<String> failedUserIds;
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED
    private Long timestamp;
    private String sendType; // SPECIFIC_USERS, BY_ROLE, BY_RESTAURANT, ALL_USERS
}

