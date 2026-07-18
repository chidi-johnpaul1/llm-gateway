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
public class AzureOpenAiProvider implements LlmProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final String endpoint;
    private final String deployment;
    private final String apiVersion;
    private final ObjectMapper mapper = new ObjectMapper();

    public AzureOpenAiProvider(WebClient.Builder webClientBuilder,
                               @Value("${llm.azure.api-key}") String apiKey,
                               @Value("${llm.azure.endpoint}") String endpoint,
                               @Value("${llm.azure.deployment}") String deployment,
                               @Value("${llm.azure.api-version}") String apiVersion) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.deployment = deployment;
        this.apiVersion = apiVersion;
        this.webClient = webClientBuilder.baseUrl(endpoint).build();
    }

    @Override
    public Mono<String> generateText(String prompt, Map<String, Object> options) {
        return callAzure(prompt, options);
    }

    @Override
    public Mono<String> generateChat(String conversationId, String userMessage, Map<String, Object> options) {
        return callAzure(userMessage, options);
    }

    private Mono<String> callAzure(String prompt, Map<String, Object> options) {
        Integer maxTokens = (Integer) options.getOrDefault("maxTokens", 500);
        Double temperature = (Double) options.getOrDefault("temperature", 0.7);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/openai/deployments/{deployment}/chat/completions")
                        .queryParam("api-version", apiVersion)
                        .build(deployment))
                .header("api-key", apiKey)
                .bodyValue(Map.of(
                        "messages", new Object[]{Map.of("role", "user", "content", prompt)},
                        "max_tokens", maxTokens,
                        "temperature", temperature
                ))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractContent)
                .doOnError(e -> log.error("Azure OpenAI call failed", e));
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Azure response", e);
        }
    }
}