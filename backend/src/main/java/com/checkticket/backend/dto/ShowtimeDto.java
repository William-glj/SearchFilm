package com.checkticket.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ShowtimeDto(
        Long id,
        Long cinemaId,
        Long tmdbMovieId,
        String movieTitle,
        String dayOfWeek,
        LocalTime sessionTime,
        BigDecimal price,
        int seatsAvailable,
        LocalDateTime updatedAt) {
}
