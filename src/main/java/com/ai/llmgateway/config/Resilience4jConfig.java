package com.ai.llmgateway.config;

import org.springframework.context.annotation.Configuration;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;

@Configuration
public class Resilience4jConfig {
    // Custom configuration if needed; defaults from application.yml are auto-configured.

}
