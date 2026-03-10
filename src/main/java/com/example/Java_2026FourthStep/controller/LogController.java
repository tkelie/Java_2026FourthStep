package com.example.Java_2026FourthStep.controller;

import org.springframework.web.bind.annotation.*;

import com.example.Java_2026FourthStep.entity.Log;
import com.example.Java_2026FourthStep.service.LogService;
import com.example.Java_2026FourthStep.dto.LogRequest;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public List<Log> getLogs() {
        return logService.findAll();
    }

    @GetMapping("/{userId}")
    public List<Log> getLogsByUser(@PathVariable String userId) {
        return logService.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Log> addLog(@RequestBody LogRequest request) {
        Log saved = logService.save(request);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/summary")
    public Map<String, Long> getSummary() {
        return logService.summarize();
    }

    @GetMapping("/duplicate")
    public List<String> getDuplicates() {
        return logService.findDuplicates();
    }
}