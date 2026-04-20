package com.healthcare.dynamicMaster.v1.generator.config;

import lombok.Data;

import java.util.List;

@Data
public class UniqueConstraint {
    private String name;
    private List<String> fields;
    private String message;
}