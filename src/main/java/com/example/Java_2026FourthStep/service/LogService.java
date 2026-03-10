package com.example.Java_2026FourthStep.service;

import org.springframework.stereotype.Service;

import com.example.Java_2026FourthStep.dto.LogRequest;
import com.example.Java_2026FourthStep.entity.Action;
import com.example.Java_2026FourthStep.entity.Log;
import com.example.Java_2026FourthStep.exception.UserNotFoundException;
import com.example.Java_2026FourthStep.repository.LogRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Log> result = logRepository.findByUserId(userId);

        if (result.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return result;
    }

    public Log save(LogRequest request) {
        Log log = new Log(request.userId(), request.action(), request.logTime());
        return logRepository.save(log);
    }

    public Map<String, Long> summarize() {
        return logRepository.findAll().stream()
                .filter(log -> log.getAction() == Action.LOGIN)
                .collect(Collectors.groupingBy(
                        Log::getUserId,
                        Collectors.counting()));
    }

    public List<String> findDuplicates() {
        return summarize().entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
    }
}