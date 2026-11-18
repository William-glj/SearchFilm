package com.checkticket.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record AlertRequest(
        @NotBlank String movieTitle,
        String targetDay,
        BigDecimal maxPrice,
        Long cinemaId,
        Boolean notifyEmail,
        Boolean notifyTelegram) {
}
