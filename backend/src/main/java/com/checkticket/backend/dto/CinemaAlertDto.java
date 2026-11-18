package com.checkticket.backend.dto;

import java.math.BigDecimal;

public record CinemaAlertDto(
        Long id,
        Long userId,
        Long cinemaId,
        String movieTitle,
        String targetDay,
        BigDecimal maxPrice,
        boolean notifyEmail,
        boolean notifyTelegram,
        boolean active,
        String lastNotifiedHash) {
}
