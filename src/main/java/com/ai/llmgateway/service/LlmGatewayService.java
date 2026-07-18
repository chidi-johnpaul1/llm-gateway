package com.ai.llmgateway.service;

import com.ai.llmgateway.dto.ChatRequest;
import com.ai.llmgateway.dto.ChatResponse;
import com.ai.llmgateway.dto.GenerateRequest;
import com.ai.llmgateway.dto.GenerateResponse;
import com.ai.llmgateway.exception.RateLimitExceededException;
import com.ai.llmgateway.provider.LlmProvider;
import com.ai.llmgateway.provider.LlmProviderFactory;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmGatewayService {

    private final LlmProviderFactory providerFactory;
    private final ChatMemoryService chatMemoryService;
    private final PromptTemplateService promptTemplateService;

    @Retry(name = "llm-gateway")
    @RateLimiter(name = "llm-gateway", fallbackMethod = "rateLimitFallback")
    public Mono<GenerateResponse> generateText(GenerateRequest request) {
        LlmProvider provider = providerFactory.getProvider();
        Map<String, Object> options = new HashMap<>();
        options.put("maxTokens", request.getMaxTokens());
        options.put("temperature", request.getTemperature());

        return provider.generateText(request.getPrompt(), options)
                .map(GenerateResponse::new);
    }

    @Retry(name = "llm-gateway")
    @RateLimiter(name = "llm-gateway")
    public Mono<ChatResponse> chat(ChatRequest request) {
        LlmProvider provider = providerFactory.getProvider();
        String convId = request.getConversationId();

        Mono<String> convIdMono = (convId == null || convId.isBlank())
                ? chatMemoryService.createConversation()
                : Mono.just(convId);

        return convIdMono.flatMap(cid ->
                chatMemoryService.addMessage(cid, "user", request.getMessage())
                        .then(chatMemoryService.buildContextPrompt(cid, request.getMessage()))
                        .flatMap(contextPrompt -> {
                            Map<String, Object> options = new HashMap<>();
                            options.put("maxTokens", 500);
                            options.put("temperature", 0.7);
                            return provider.generateChat(cid, contextPrompt, options);
                        })
                        .flatMap(response -> chatMemoryService.addMessage(convIdMono.block(), "assistant", response)
                                .thenReturn(new ChatResponse(convIdMono.block(), response)))
        );
    }

    @Cacheable(value = "llmResponses", key = "#request.prompt")
    public Mono<GenerateResponse> generateTextCached(GenerateRequest request) {
        return generateText(request);
    }

    private Mono<GenerateResponse> rateLimitFallback(GenerateRequest request, Exception ex) {
        log.warn("Rate limit exceeded for request: {}", request);
        return Mono.error(new RateLimitExceededException("Too many requests, please try later"));
    }
}