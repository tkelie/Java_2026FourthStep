package com.example.Java_2026FourthStep.repository;

import com.example.Java_2026FourthStep.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findByUserId(String userId);

    List<Log> findByUserIdOrderByLogTimeAsc(String userId);
}