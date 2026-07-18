package com.ai.llmgateway.exception;


public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) { super(message); }
}