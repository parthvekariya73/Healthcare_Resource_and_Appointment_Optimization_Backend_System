package com.healthcare.dynamicMaster.v1.generator.config;

import lombok.Data;

import java.util.List;

@Data
public class ApiEndpoint {
    private String path;
    private String method;              // GET, POST, PUT, DELETE, PATCH
    private String operation;
    private boolean paginated;
    private boolean filtered;
    private List<String> requiredPermissions;
}