package com.healthcare.dynamicMaster.v1.generator.config;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ValidationRule {
    private String annotation;
    private Map<String, Object> attributes = new HashMap<>(); // Initialize with empty map
    private String message;

    // Helper method to safely get attributes
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }
}