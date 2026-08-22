package com.dayflow.hrms.ai.controller;

import com.dayflow.hrms.ai.dto.AiHrInsightsDto;
import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import com.dayflow.hrms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "HRMS AI Assistant & Insights", description = "Endpoints for employee AI HR query processing and HR/Admin organizational insights")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/query")
    @Operation(summary = "Process natural language HR query for employee")
    public ResponseEntity<ApiResponse<AiQueryResponseDto>> processQuery(@Valid @RequestBody AiQueryRequestDto request) {
        AiQueryResponseDto response = aiAssistantService.processQuery(request);
        return ResponseEntity.ok(ApiResponse.ok("Query processed successfully", response));
    }

    @GetMapping("/hr-insights")
    @Operation(summary = "HR/Admin: Get AI-generated organizational insights")
    public ResponseEntity<ApiResponse<AiHrInsightsDto>> getHrInsights() {
        AiHrInsightsDto insights = aiAssistantService.getHrInsights();
        return ResponseEntity.ok(ApiResponse.ok("HR insights generated successfully", insights));
    }
}
