package com.ai.llmgateway;

import com.ai.llmgateway.dto.GenerateRequest;
import com.ai.llmgateway.provider.LlmProvider;
import com.ai.llmgateway.provider.LlmProviderFactory;
import com.ai.llmgateway.service.LlmGatewayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmGatewayServiceTest {

    @Mock
    private LlmProviderFactory providerFactory;

    @Mock
    private LlmProvider provider;

    @InjectMocks
    private LlmGatewayService service;

    @Test
    void generateText_shouldReturnResponse() {
        GenerateRequest request = new GenerateRequest();
        request.setPrompt("Hello");
        when(providerFactory.getProvider()).thenReturn(provider);
        when(provider.generateText(any(), anyMap())).thenReturn(Mono.just("World"));

        StepVerifier.create(service.generateText(request))
                .expectNextMatches(resp -> resp.getText().equals("World"))
                .verifyComplete();
    }
}