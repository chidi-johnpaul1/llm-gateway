package com.ai.llmgateway.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatMemoryService {

    private final Map<String, List<Map<String, String>>> conversations = new ConcurrentHashMap<>();

    public Mono<String> createConversation() {
        String convId = UUID.randomUUID().toString();
        conversations.put(convId, new ArrayList<>());
        return Mono.just(convId);
    }

    public Mono<List<Map<String, String>>> getHistory(String conversationId) {
        return Mono.justOrEmpty(conversations.getOrDefault(conversationId, List.of()));
    }

    public Mono<Void> addMessage(String conversationId, String role, String content) {
        conversations.computeIfAbsent(conversationId, k -> new ArrayList<>())
                .add(Map.of("role", role, "content", content));
        return Mono.empty();
    }

    public Mono<String> buildContextPrompt(String conversationId, String newUserMessage) {
        return getHistory(conversationId)
                .map(history -> {
                    StringBuilder sb = new StringBuilder();
                    for (Map<String, String> msg : history) {
                        sb.append(msg.get("role")).append(": ").append(msg.get("content")).append("\n");
                    }
                    sb.append("user: ").append(newUserMessage);
                    return sb.toString();
                });
    }
}