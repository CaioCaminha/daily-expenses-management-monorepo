package com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompletionTokenDetails(
        @JsonProperty("reasoning_tokens")
        Number reasoningTokens,
        @JsonProperty("accepted_prediction_tokens")
        Number acceptedPredictionTokens,
        @JsonProperty("rejected_prediction_tokens")
        Number rejectedPredictionTokens
) {
}
