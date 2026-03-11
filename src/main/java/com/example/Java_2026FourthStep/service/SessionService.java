package com.example.Java_2026FourthStep.service;

import com.example.Java_2026FourthStep.dto.SessionDto;
import com.example.Java_2026FourthStep.dto.SessionSummaryDto;
import com.example.Java_2026FourthStep.entity.Action;
import com.example.Java_2026FourthStep.repository.LogRepository;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final LogRepository logRepository;

    public SessionService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // ユーザーのセッション一覧
    public List<SessionDto> extractSessions(String userId) {
        var logs = logRepository.findByUserIdOrderByLogTimeAsc(userId);
        List<SessionDto> sessions = new ArrayList<>();

        for (int i = 0; i + 1 < logs.size(); i++) {
            var curr = logs.get(i);
            var next = logs.get(i + 1);

            if (curr.getAction() == Action.LOGIN &&
                    next.getAction() == Action.LOGOUT) {

                long minutes = Duration.between(curr.getLogTime(), next.getLogTime()).toMinutes();
                sessions.add(new SessionDto(userId, curr.getLogTime(), next.getLogTime(), minutes));
                i++;
            }
        }
        return sessions;
    }

    // 総滞在時間ランキング（降順）
    public List<SessionSummaryDto> getRanking() {
        var allUsers = logRepository.findAll().stream()
                .map(log -> log.getUserId())
                .distinct()
                .toList();

        return allUsers.stream()
                .map(userId -> {
                    long total = extractSessions(userId).stream()
                            .mapToLong(SessionDto::durationMinutes)
                            .sum();
                    return new SessionSummaryDto(userId, total);
                })
                .sorted(Comparator.comparingLong(SessionSummaryDto::totalMinutes).reversed())
                .collect(Collectors.toList());
    }

    // 平均セッション時間（分・切り捨て）
    public long getAverage() {
        var allUsers = logRepository.findAll().stream()
                .map(log -> log.getUserId())
                .distinct()
                .toList();

        var allSessions = allUsers.stream()
                .flatMap(userId -> extractSessions(userId).stream())
                .toList();

        if (allSessions.isEmpty())
            return 0;

        return (long) allSessions.stream()
                .mapToLong(SessionDto::durationMinutes)
                .average()
                .orElse(0);
    }

    // ログイン中ユーザー（LOGOUT なし）
    public List<String> getActiveUsers() {
        var allUsers = logRepository.findAll().stream()
                .map(log -> log.getUserId())
                .distinct()
                .toList();

        return allUsers.stream()
                .filter(userId -> {
                    var logs = logRepository.findByUserIdOrderByLogTimeAsc(userId);
                    if (logs.isEmpty())
                        return false;
                    var last = logs.get(logs.size() - 1);
                    return last.getAction() == Action.LOGIN;
                })
                .collect(Collectors.toList());
    }
}