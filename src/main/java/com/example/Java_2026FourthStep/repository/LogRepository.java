package com.example.Java_2026FourthStep.repository;

import org.springframework.stereotype.Repository;

import com.example.Java_2026FourthStep.entity.Log;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LogRepository {

    private final List<Log> logs = new ArrayList<>(List.of(
            new Log("alice", "LOGIN"),
            new Log("alice", "LOGOUT"),
            new Log("bob", "LOGIN"),
            new Log("charlie", "LOGIN"),
            new Log("charlie", "LOGOUT"),
            new Log("alice", "LOGIN"),
            new Log("charlie", "LOGIN")));

    public List<Log> findAll() {
        return logs;
    }

    public void save(Log log) {
        logs.add(log);
    }
}