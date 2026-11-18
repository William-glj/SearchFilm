package com.checkticket.backend.dto;

public record CinemaDto(
        Long id,
        String externalOsmId,
        String name,
        String city,
        String zone,
        Double latitude,
        Double longitude) {
}
