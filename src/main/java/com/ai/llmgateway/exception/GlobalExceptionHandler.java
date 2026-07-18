package com.ai.llmgateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LlmProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Mono<Map<String, String>> handleLlmProviderException(LlmProviderException ex) {
        return Mono.just(Map.of("error", "LLM provider error", "message", ex.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Mono<Map<String, String>> handleRateLimitExceeded(RateLimitExceededException ex) {
        return Mono.just(Map.of("error", "Rate limit exceeded", "message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Map<String, String>> handleGeneric(Exception ex) {
        return Mono.just(Map.of("error", "Internal server error", "message", ex.getMessage()));
    }
}




