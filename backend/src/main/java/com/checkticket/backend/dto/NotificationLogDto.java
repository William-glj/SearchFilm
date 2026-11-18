package com.checkticket.backend.dto;

import java.time.LocalDateTime;

public record NotificationLogDto(
        Long id,
        Long alertId,
        String channel,
        String messageSummary,
        LocalDateTime sentAt) {
}
