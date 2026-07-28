package com.caiocaminha.expensesmanager.core.application.gateway.api.openai;

import com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto.OpenAiRequestDto;
import com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto.OpenAiResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange(url = "", accept = MediaType.APPLICATION_JSON_VALUE)
public interface OpenAiHttpClient {

    @GetExchange("/chat/completions")
    Mono<OpenAiResponseDto> getCategories(
            @RequestBody OpenAiRequestDto openAiRequestDto,
            @RequestAttribute("openAiApiKey") String apiKey
    );

}
