package com.example.Java_2026FourthStep;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 全ログ取得_200が返る() throws Exception {
        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("alice"))
                .andExpect(jsonPath("$[0].operation").value("LOGIN"));
    }

    @Test
    void ユーザー別ログ取得_aliceのログが返る() throws Exception {
        mockMvc.perform(get("/api/logs/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("alice"));
    }

    @Test
    void 存在しないユーザー_404が返る() throws Exception {
        mockMvc.perform(get("/api/logs/dave"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("dave not found"));
    }
}