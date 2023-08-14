package com.dateBuzz.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String method;
    private Long executionTime;
    private Long userId;
    private String requestBody;
    private String responseBody;
    private String userAgent;


    // getters and setters

    @Builder
    public AccessLog(String url, String method, Long executionTime, String requestBody, String responseBody, Long userId, String userAgent) {
        this.url = url;
        this.method = method;
        this.executionTime = executionTime;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.userId = userId;
        this.userAgent = userAgent;
    }

    public static AccessLog logging(String url, String method, Long executionTime, String requestBody, String responseBody, Long userId, String userAgent) {
        return AccessLog.builder()
                .url(url)
                .executionTime(executionTime)
                .method(method)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .userId(userId)
                .userAgent(userAgent)
                .build();
    }
}

