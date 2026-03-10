package com.example.Java_2026FourthStep.repository;

import com.example.Java_2026FourthStep.entity.Log;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findByUserId(String userId);
}