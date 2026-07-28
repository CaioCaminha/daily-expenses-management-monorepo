package com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UsageDto(
        @JsonProperty("prompt_tokens")
        Number promptTokens,
        @JsonProperty("completion_tokens")
        Number completionTokens,
        @JsonProperty("total_tokens")
        Number totalTokens,
        @JsonProperty("completion_tokens_details")
        CompletionTokenDetails completionTokenDetails
) {
}
