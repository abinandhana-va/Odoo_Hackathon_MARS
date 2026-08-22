package com.dayflow.hrms.ai.dto;

import java.time.LocalDateTime;

/**
 * Placeholder DTO for AI Assistant responses.
 */
public class AiQueryResponseDto {

    private String response;
    private String modelName = "Dayflow-HR-AI-Placeholder";
    private LocalDateTime timestamp;

    public AiQueryResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public AiQueryResponseDto(String response) {
        this.response = response;
        this.modelName = "Dayflow-HR-AI-Placeholder";
        this.timestamp = LocalDateTime.now();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
