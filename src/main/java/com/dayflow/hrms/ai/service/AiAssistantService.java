package com.dayflow.hrms.ai.service;

import com.dayflow.hrms.ai.dto.AiHrInsightsDto;
import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;

public interface AiAssistantService {
    AiQueryResponseDto processQuery(AiQueryRequestDto request);
    AiHrInsightsDto getHrInsights();
}
