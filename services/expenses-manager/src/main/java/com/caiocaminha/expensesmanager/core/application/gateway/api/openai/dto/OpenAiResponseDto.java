package com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAiResponseDto(
        String id,
        String object,
        String model,
        Set<ChoiceDto> choices,
        UsageDto usage
) {
}
