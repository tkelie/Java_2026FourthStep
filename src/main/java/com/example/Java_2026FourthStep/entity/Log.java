package com.example.Java_2026FourthStep.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private Action action;

    private LocalDateTime logTime;

    // JPA用コンストラクタ（必須）
    public Log() {
    }

    public Log(String userId, Action action, LocalDateTime logTime) {
        this.userId = userId;
        this.action = action;
        this.logTime = logTime;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Action getAction() {
        return action;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }
}