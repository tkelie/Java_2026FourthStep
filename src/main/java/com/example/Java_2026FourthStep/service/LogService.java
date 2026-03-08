package com.example.Java_2026FourthStep.service;

import org.springframework.stereotype.Service;

import com.example.Java_2026FourthStep.entity.Log;
import com.example.Java_2026FourthStep.exception.UserNotFoundException;
import com.example.Java_2026FourthStep.repository.LogRepository;

import java.util.List;
import java.util.Map;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<Log> findAll() {
        return logRepository.findAll();
    }

    public List<Log> findByUserId(String userId) {
        List<Log> result = logRepository.findAll().stream()
                .filter(log -> log.userId().equals(userId))
                .toList();

        if (result.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return result;
    }

    public Log save(Log log) {
        logRepository.save(log);
        return log;
    }

    public Map<String, Long> summarize() {
        return logRepository.findAll().stream()
                .filter(log -> log.operation().equals("LOGIN"))
                .collect(java.util.stream.Collectors.groupingBy(
                        Log::userId,
                        java.util.stream.Collectors.counting()));
    }

    public List<String> findDuplicates() {
        return summarize().entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
    }
}