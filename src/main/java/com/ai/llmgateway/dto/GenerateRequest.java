package com.ai.llmgateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateRequest {
    @NotBlank
    private String prompt;
    private Integer maxTokens;
    private Double temperature;
}



