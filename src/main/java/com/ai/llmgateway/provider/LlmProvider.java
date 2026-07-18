package com.ai.llmgateway.provider;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface LlmProvider {
    Mono<String> generateText(String prompt, Map<String, Object> options);
    Mono<String> generateChat(String conversationId, String userMessage, Map<String, Object> options);
}