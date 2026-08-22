package com.dayflow.hrms.ai.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * DTO for AI Assistant responses.
 */
public class AiQueryResponseDto {

    private String response;
    private String intent = "GENERAL";
    private String modelName = "Dayflow-HR-AI-Placeholder";
    private List<String> suggestedActions = Collections.emptyList();
    private LocalDateTime timestamp;

    public AiQueryResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public AiQueryResponseDto(String response) {
        this.response = response;
        this.intent = "GENERAL";
        this.modelName = "Dayflow-HR-AI-Placeholder";
        this.timestamp = LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String response;
        private String intent = "GENERAL";
        private String modelName = "Dayflow-HR-AI-Placeholder";
        private List<String> suggestedActions = Collections.emptyList();
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder response(String response) { this.response = response; return this; }
        public Builder intent(String intent) { this.intent = intent; return this; }
        public Builder modelName(String modelName) { this.modelName = modelName; return this; }
        public Builder suggestedActions(List<String> suggestedActions) { this.suggestedActions = suggestedActions; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AiQueryResponseDto build() {
            AiQueryResponseDto dto = new AiQueryResponseDto(response);
            if (intent != null) dto.setIntent(intent);
            if (modelName != null) dto.setModelName(modelName);
            if (suggestedActions != null) dto.setSuggestedActions(suggestedActions);
            if (timestamp != null) dto.setTimestamp(timestamp);
            return dto;
        }
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<String> getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(List<String> suggestedActions) {
        this.suggestedActions = suggestedActions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
