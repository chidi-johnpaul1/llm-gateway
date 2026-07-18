package com.ai.llmgateway.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    private String conversationId;  // optional, if not provided create new
    @NotBlank
    private String message;
}
