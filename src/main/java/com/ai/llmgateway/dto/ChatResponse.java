package com.ai.llmgateway.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String conversationId;
    private String response;
}