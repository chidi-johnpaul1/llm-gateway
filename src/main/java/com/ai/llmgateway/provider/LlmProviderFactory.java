package com.ai.llmgateway.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LlmProviderFactory {

    @Value("${llm.provider}")
    private String activeProvider;

    //@Autowired
    private final OpenAiProvider openAiProvider;

    //@Autowired
    private final  AzureOpenAiProvider azureOpenAiProvider;

    //@Autowired
    private final  HuggingFaceProvider huggingFaceProvider;

    public LlmProvider getProvider() {
        return switch (activeProvider.toLowerCase()) {
            case "openai" -> openAiProvider;
            case "azure" -> azureOpenAiProvider;
            case "huggingface" -> huggingFaceProvider;
            default -> throw new IllegalArgumentException("Unsupported provider: " + activeProvider);
        };
    }
}