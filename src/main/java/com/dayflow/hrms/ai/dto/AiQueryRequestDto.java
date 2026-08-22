package com.dayflow.hrms.ai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Placeholder DTO for AI Assistant user query requests.
 */
public class AiQueryRequestDto {

    @NotBlank(message = "Prompt/query cannot be blank")
    private String prompt;

    private String context;
    private Long employeeId;

    public AiQueryRequestDto() {
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
