package com.healthcare.dynamicMaster.v1.generator.moduler;

import com.healthcare.dynamicMaster.v1.generator.BaseGenerator;
import com.healthcare.dynamicMaster.v1.generator.config.FieldConfig;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EntityGenerator extends BaseGenerator {

    public String generate(ModuleConfig config) {
        log.info("Generating Entity for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        // Package declaration
        sb.append("package ").append(config.getPackageName()).append(".entity;\n\n");

        // ==================== IMPORTS ====================
        sb.append("import com.healthcare.common.apputil.utils.commonutil.CommonUtil;\n");
        sb.append("import com.healthcare.common.apputil.utils.commonutil.SecurityUtils;\n");
        sb.append("import jakarta.persistence.*;\n");
        sb.append("import jakarta.validation.constraints.*;\n");
        sb.append("import lombok.*;\n");
        sb.append("import org.hibernate.annotations.CreationTimestamp;\n");
        sb.append("import org.hibernate.annotations.UpdateTimestamp;\n");
        sb.append("import org.hibernate.annotations.Generated;\n");
        sb.append("import org.hibernate.generator.EventType;\n");
        sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.UUID;\n");

        if (hasLocalDateField(config)) {
            sb.append("import java.time.LocalDate;\n");
        }
        sb.append("\n");

        // ==================== ENTITY ANNOTATIONS ====================
        sb.append("@Entity\n");
        sb.append("@Table(name = \"").append(config.getTableName())
                .append("\", schema = \"").append(config.getSchemaName()).append("\")\n");
        sb.append("@Getter\n");
        sb.append("@Setter\n");
        sb.append("@NoArgsConstructor\n");
        sb.append("@AllArgsConstructor\n");
        sb.append("@Builder\n");
        sb.append("public class ").append(getEntityName(config)).append(" {\n\n");

        // ==================== PRIMARY KEY FIELD ====================
        generatePrimaryKeyField(sb, config);
        sb.append("\n");

        // ==================== UUID FIELD ====================
        generateUuidField(sb, config);
        sb.append("\n");

        // ==================== BUSINESS FIELDS ====================
        for (FieldConfig field : config.getFields()) {
            generateBusinessField(sb, field, config);
            sb.append("\n");
        }

        // ==================== STATUS FIELD ====================
        generateStatusField(sb);
        sb.append("\n");

        // ==================== AUDIT FIELDS ====================
        generateAuditFields(sb);
        sb.append("\n");

        // ==================== LIFECYCLE METHODS ====================
        generateLifecycleMethods(sb, config);

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * Generates the primary key field
     */
    private void generatePrimaryKeyField(StringBuilder sb, ModuleConfig config) {
        sb.append("    @Id\n");
        sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        sb.append("    @Column(name = \"").append(config.getPrimaryKey()).append("\")\n");
        sb.append("    private Long ").append(config.getPrimaryKey()).append(";\n");
    }

    /**
     * Generates the UUID field
     */
    private void generateUuidField(StringBuilder sb, ModuleConfig config) {
        sb.append("    @Column(name = \"").append(config.getUuidField())
                .append("\", nullable = false, updatable = false, unique = true, insertable = false, ")
                .append("columnDefinition = \"UUID DEFAULT gen_uuid_v7()\")\n");
        sb.append("    @Generated(event = EventType.INSERT)\n");
        sb.append("    private UUID ").append(config.getUuidField()).append(";\n");
    }

    /**
     * Generates business fields with proper annotations
     */
    private void generateBusinessField(StringBuilder sb, FieldConfig field, ModuleConfig config) {
        // Column annotation
        sb.append("    @Column(name = \"").append(field.getColumnName()).append("\"");
        if (!field.isNullable()) {
            sb.append(", nullable = false");
        }
        if (field.getLength() != null && "String".equals(field.getType())) {
            sb.append(", length = ").append(field.getLength());
        }
        if (field.getPrecision() != null) {
            sb.append(", precision = ").append(field.getPrecision());
        }
        if (field.getScale() != null) {
            sb.append(", scale = ").append(field.getScale());
        }
        if (field.getDefaultValue() != null) {
            sb.append(", columnDefinition = \"").append(field.getDefaultValue()).append("\"");
        }
        sb.append(")\n");

        // Validation annotations
        if (field.getValidations() != null && !field.getValidations().isEmpty()) {
            for (var validation : field.getValidations()) {
                sb.append("    @").append(validation.getAnnotation());

                Map<String, Object> attributes = validation.getAttributes();
                if (attributes != null && !attributes.isEmpty()) {
                    sb.append("(");
                    String attrs = attributes.entrySet().stream()
                            .map(e -> e.getKey() + " = " + formatValue(e.getValue()))
                            .collect(Collectors.joining(", "));
                    sb.append(attrs);
                    if (validation.getMessage() != null) {
                        sb.append(", message = \"").append(validation.getMessage()).append("\"");
                    }
                    sb.append(")");
                } else if (validation.getMessage() != null) {
                    sb.append("(message = \"").append(validation.getMessage()).append("\")");
                }
                sb.append("\n");
            }
        }

        // Field declaration
        sb.append("    private ").append(getJavaType(field.getType()));
        sb.append(" ").append(field.getName()).append(";\n");
    }

    /**
     * Generates the status field
     */
    private void generateStatusField(StringBuilder sb) {
        sb.append("    @Column(name = \"status\")\n");
        sb.append("    @Builder.Default\n");
        sb.append("    private Short status = 1;\n");
    }

    /**
     * Generates audit fields (created_at, updated_at, created_by, updated_by, etc.)
     */
    private void generateAuditFields(StringBuilder sb) {
        sb.append("    @CreationTimestamp\n");
        sb.append("    @Column(name = \"created_at\", updatable = false)\n");
        sb.append("    private LocalDateTime createdAt;\n\n");

        sb.append("    @UpdateTimestamp\n");
        sb.append("    @Column(name = \"updated_at\")\n");
        sb.append("    private LocalDateTime updatedAt;\n\n");

        sb.append("    @Column(name = \"deleted_at\")\n");
        sb.append("    private LocalDateTime deletedAt;\n\n");

        sb.append("    @Column(name = \"created_by\")\n");
        sb.append("    private Long createdBy;\n\n");

        sb.append("    @Column(name = \"updated_by\")\n");
        sb.append("    private Long updatedBy;\n\n");

        sb.append("    @Column(name = \"deleted_by\")\n");
        sb.append("    private Long deletedBy;\n\n");

        sb.append("    @Column(name = \"audit_tracker_id\", precision = 24)\n");
        sb.append("    private BigDecimal auditTrackerId;\n");
    }

    /**
     * Generates PrePersist and PreUpdate lifecycle methods
     */
    private void generateLifecycleMethods(StringBuilder sb, ModuleConfig config) {
        sb.append("    @PrePersist\n");
        sb.append("    protected void onCreate() {\n");
        sb.append("        if (this.status == null) {\n");
        sb.append("            this.status = 1;\n");
        sb.append("        }\n");
        sb.append("        this.createdAt = LocalDateTime.now();\n");
        sb.append("        this.updatedAt = LocalDateTime.now();\n");
        sb.append("        this.createdBy = SecurityUtils.getCurrentUserId();\n");
        sb.append("        this.updatedBy = SecurityUtils.getCurrentUserId();\n");
        sb.append("        this.auditTrackerId = CommonUtil.generateUniqueTxnNumber();\n");
        sb.append("    }\n\n");

        sb.append("    @PreUpdate\n");
        sb.append("    protected void onUpdate() {\n");
        sb.append("        this.updatedAt = LocalDateTime.now();\n");
        sb.append("        this.updatedBy = SecurityUtils.getCurrentUserId();\n");
        sb.append("    }\n");
    }

    /**
     * Gets entity name from module configuration
     * Converts snake_case to PascalCase
     * Example: product_category -> ProductCategory
     */
    public String getEntityName(ModuleConfig config) {
        String[] parts = config.getModuleName().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Converts type string to Java type
     */
    public String getJavaType(String type) {
        switch (type.toLowerCase()) {
            case "string": return "String";
            case "long": return "Long";
            case "integer": return "Integer";
            case "int": return "Integer";
            case "boolean": return "Boolean";
            case "localdate": return "LocalDate";
            case "localdatetime": return "LocalDateTime";
            case "bigdecimal": return "BigDecimal";
            case "uuid": return "UUID";
            case "double": return "Double";
            case "float": return "Float";
            default: return "String";
        }
    }

    /**
     * Formats value for annotation parameters
     */
    public String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        return value.toString();
    }

    /**
     * Checks if module has BigDecimal fields
     */
    public boolean hasBigDecimalField(ModuleConfig config) {
        return config.getFields().stream()
                .anyMatch(f -> "BigDecimal".equalsIgnoreCase(f.getType()));
    }

    /**
     * Checks if module has LocalDate fields
     */
    public boolean hasLocalDateField(ModuleConfig config) {
        return config.getFields().stream()
                .anyMatch(f -> "LocalDate".equalsIgnoreCase(f.getType()));
    }

    /**
     * Checks if module has LocalDateTime fields
     */
    public boolean hasLocalDateTimeField(ModuleConfig config) {
        return config.getFields().stream()
                .anyMatch(f -> "LocalDateTime".equalsIgnoreCase(f.getType()));
    }
}