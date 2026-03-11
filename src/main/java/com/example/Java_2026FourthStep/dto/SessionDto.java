package com.example.Java_2026FourthStep.dto;

import java.time.LocalDateTime;

public record SessionDto(
        String userId,
        LocalDateTime loginTime,
        LocalDateTime logoutTime,
        long durationMinutes) {
}