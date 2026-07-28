package com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto;

import java.util.Set;

public record OpenAiRequestDto(
        Set<MessageDto> messages
) {
    public static String model = "gpt-4o-mini";
}
