package com.checkticket.backend.dto;

public record LoginResponse(
        String token,
        Long userId,
        String email,
        String fullName,
        String role) {
}
