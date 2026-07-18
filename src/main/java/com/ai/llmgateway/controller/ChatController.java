package com.ai.llmgateway.controller;

import com.ai.llmgateway.dto.ChatRequest;
import com.ai.llmgateway.dto.ChatResponse;
import com.ai.llmgateway.service.LlmGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final LlmGatewayService llmGatewayService;

    @PostMapping
    public Mono<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return llmGatewayService.chat(request);
    }
}