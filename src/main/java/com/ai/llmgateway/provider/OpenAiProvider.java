package com.ai.llmgateway.provider;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
public class OpenAiProvider implements LlmProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAiProvider(WebClient.Builder webClientBuilder,
                          @Value("${llm.openai.api-key}") String apiKey,
                          @Value("${llm.openai.model}") String model,
                          @Value("${llm.openai.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<String> generateText(String prompt, Map<String, Object> options) {
        log.debug("Calling OpenAI text generation with prompt: {}", prompt);
        return callOpenAi(prompt, options);
    }

    @Override
    public Mono<String> generateChat(String conversationId, String userMessage, Map<String, Object> options) {
        log.debug("Calling OpenAI chat for convId: {}", conversationId);
        // For simplicity, we just send user message; memory is handled by service layer.
        return callOpenAi(userMessage, options);
    }

    private Mono<String> callOpenAi(String prompt, Map<String, Object> options) {
        Integer maxTokens = (Integer) options.getOrDefault("maxTokens", 500);
        Double temperature = (Double) options.getOrDefault("temperature", 0.7);

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(Map.of(
                        "model", model,
                        "messages", new Object[]{Map.of("role", "user", "content", prompt)},
                        "max_tokens", maxTokens,
                        "temperature", temperature
                ))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> extractContent(response))
                .doOnError(e -> log.error("OpenAI call failed", e));
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }
}