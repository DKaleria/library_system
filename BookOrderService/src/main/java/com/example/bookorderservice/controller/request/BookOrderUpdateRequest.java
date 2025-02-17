package com.example.bookorderservice.controller.request;

import lombok.Builder;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record BookOrderUpdateRequest(
        Long userId,
        Long bookId,
        Integer quantity,
        String status,
        LocalDate orderDate
) {
}
