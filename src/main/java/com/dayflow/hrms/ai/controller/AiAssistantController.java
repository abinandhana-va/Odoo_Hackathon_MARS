package com.dayflow.hrms.ai.controller;

import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import com.dayflow.hrms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for AI Assistant API endpoints.
 */
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Assistant (Placeholder)", description = "Endpoints for conversational HR AI Assistant and query answering")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/query")
    @Operation(summary = "Query AI Assistant", description = "Sends query prompt to AI assistant placeholder")
    public ResponseEntity<ApiResponse<AiQueryResponseDto>> queryAssistant(@Valid @RequestBody AiQueryRequestDto request) {
        AiQueryResponseDto response = aiAssistantService.processQuery(request);
        return ResponseEntity.ok(ApiResponse.ok("AI Assistant placeholder response", response));
    }
}
