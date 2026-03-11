package com.example.Java_2026FourthStep.controller;

import com.example.Java_2026FourthStep.dto.SessionDto;
import com.example.Java_2026FourthStep.dto.SessionSummaryDto;
import com.example.Java_2026FourthStep.service.SessionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{userId}")
    public List<SessionDto> getSessions(@PathVariable String userId) {
        return sessionService.extractSessions(userId);
    }

    @GetMapping("/ranking")
    public List<SessionSummaryDto> getRanking() {
        return sessionService.getRanking();
    }

    @GetMapping("/average")
    public long getAverage() {
        return sessionService.getAverage();
    }

    @GetMapping("/active")
    public List<String> getActiveUsers() {
        return sessionService.getActiveUsers();
    }
}