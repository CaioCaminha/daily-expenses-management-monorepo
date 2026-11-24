package com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChoiceDto(
        Integer index,
        MessageDto message,
        @JsonProperty("finish_reason")
        String finishReason
) {
}
