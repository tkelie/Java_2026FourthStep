package com.example.Java_2026FourthStep.dto;

import com.example.Java_2026FourthStep.entity.Action;
import java.time.LocalDateTime;

public record LogRequest(String userId, Action action, LocalDateTime logTime) {
}