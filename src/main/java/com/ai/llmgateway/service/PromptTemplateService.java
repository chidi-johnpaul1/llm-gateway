package com.ai.llmgateway.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PromptTemplateService {

    public String applyTemplate(String templateName, Map<String, Object> variables) {
        // Simple implementation; could be extended with external template engine.
        if ("summarize".equals(templateName)) {
            return "Summarize the following text in a few sentences:\n\n" + variables.get("text");
        }
        return variables.get("prompt").toString();
    }
}
