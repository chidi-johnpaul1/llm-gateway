/*
package com.ai.llmgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@AutoConfigureWebTestClient
class ChatControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void chatEndpoint_shouldReturnOk() {
        var request = Map.of("message", "Hello");
        webTestClient.post()
                .uri("/api/v1/chat")
                .header("X-API-Key", "mysecretapikey")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.conversationId").exists()
                .jsonPath("$.response").exists();
    }
}*/
