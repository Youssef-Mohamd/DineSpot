package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.CreateTableRequest;
import com.restaurant.reservation.dto.response.TableResponse;
import com.restaurant.reservation.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping
    public ResponseEntity<TableResponse> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateTableRequest req) {
        return ResponseEntity.status(201)
                .body(tableService.create(restaurantId, req));
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> getAll(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(tableService.getByRestaurant(restaurantId));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        tableService.delete(tableId);
        return ResponseEntity.noContent().build();
    }
}