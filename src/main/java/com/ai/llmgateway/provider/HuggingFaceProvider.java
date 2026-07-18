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
public class HuggingFaceProvider implements LlmProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();

    public HuggingFaceProvider(WebClient.Builder webClientBuilder,
                               @Value("${llm.huggingface.api-key}") String apiKey,
                               @Value("${llm.huggingface.model}") String model,
                               @Value("${llm.huggingface.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = webClientBuilder.baseUrl(baseUrl + "/" + model).build();
    }

    @Override
    public Mono<String> generateText(String prompt, Map<String, Object> options) {
        return callHuggingFace(prompt, options);
    }

    @Override
    public Mono<String> generateChat(String conversationId, String userMessage, Map<String, Object> options) {
        return callHuggingFace(userMessage, options);
    }

    private Mono<String> callHuggingFace(String input, Map<String, Object> options) {
        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(Map.of("inputs", input))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractText)
                .doOnError(e -> log.error("HuggingFace call failed", e));
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            if (root.isArray() && root.size() > 0) {
                return root.get(0).path("generated_text").asText();
            }
            return root.path("generated_text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse HuggingFace response", e);
        }
    }
}