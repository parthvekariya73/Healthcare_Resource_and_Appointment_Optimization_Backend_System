package com.healthcare.dynamicMaster.v1.generator.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleConfig {
    private String moduleName;          // e.g., "product", "category"
    private String packageName;         // e.g., "com.topia.ecommerce.product"
    private String tableName;           // e.g., "mst_product"
    private String schemaName;          // e.g., "master"
    private String primaryKey;          // e.g., "product_id"
    private String primaryKeyType;      // e.g., "Long", "UUID"
    private String primaryKeySequence;  // Optional sequence name
    private String uuidField;           // e.g., "product_uuid"
    private String softDeleteField;     // e.g., "status"
    private String auditFields;         // Enable audit fields
    private List<FieldConfig> fields = new ArrayList<>(); // Initialize
    private List<UniqueConstraint> uniqueConstraints = new ArrayList<>(); // Initialize
    private List<ApiEndpoint> customEndpoints = new ArrayList<>(); // Initialize
    private List<RelationshipConfig> relationships = new ArrayList<>(); // Initialize
}
