package com.ai.llmgateway.controller;

import com.ai.llmgateway.dto.GenerateRequest;
import com.ai.llmgateway.dto.GenerateResponse;
import com.ai.llmgateway.service.LlmGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/generate")
@RequiredArgsConstructor
public class TextGenerationController {

    private final LlmGatewayService llmGatewayService;

    @PostMapping
    public Mono<GenerateResponse> generate(@Valid @RequestBody GenerateRequest request) {
        return llmGatewayService.generateText(request);
    }

    @PostMapping("/cached")
    public Mono<GenerateResponse> generateWithCache(@Valid @RequestBody GenerateRequest request) {
        return llmGatewayService.generateTextCached(request);
    }
}