package com.dayflow.hrms.ai.service.impl;

import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import org.springframework.stereotype.Service;

/**
 * Placeholder implementation for AI Assistant Service.
 * LLM integration will be connected in future iterations.
 */
@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    @Override
    public AiQueryResponseDto processQuery(AiQueryRequestDto request) {
        String placeholderAnswer = "Dayflow AI Assistant module scaffold is active. LLM processing for query '" 
                + request.getPrompt() + "' will be integrated in subsequent phases.";
        return new AiQueryResponseDto(placeholderAnswer);
    }
}
