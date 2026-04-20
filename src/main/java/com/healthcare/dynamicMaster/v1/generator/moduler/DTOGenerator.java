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
public class DTOGenerator extends BaseGenerator {

    public String generateRequestDTO(ModuleConfig config) {
        log.info("Generating Request DTO for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        // Package declaration
        sb.append("package ").append(config.getPackageName()).append(".dto.request;\n\n");

        // Imports
        sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
        sb.append("import jakarta.validation.constraints.*;\n");
        sb.append("import lombok.*;\n");
        sb.append("\n");

        if (hasLocalDateField(config)) sb.append("import java.time.LocalDate;\n");
        if (hasLocalDateTimeField(config)) sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.UUID;\n");
        sb.append("\n");

        // Class declaration
        sb.append("@Data\n");
        sb.append("@Builder\n");
        sb.append("@NoArgsConstructor\n");
        sb.append("@AllArgsConstructor\n");
        sb.append("public class ").append(getEntityName(config)).append("RequestDTO {\n\n");

        // Fields with validation
        for (FieldConfig field : config.getFields()) {
            generateRequestField(sb, field);
            sb.append("\n");
        }

        sb.append("}\n");

        return sb.toString();
    }

    public String generateResponseDTO(ModuleConfig config) {
        StringBuilder sb = new StringBuilder();
        String entityName = getEntityName(config);

        sb.append("package ").append(config.getPackageName()).append(".dto.response;\n\n");
        sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
        sb.append("import lombok.*;\n");
        sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.UUID;\n\n");

        sb.append("@Data\n");
        sb.append("@Builder\n");
        sb.append("@NoArgsConstructor\n");
        sb.append("@AllArgsConstructor\n");
        sb.append("public class ").append(entityName).append("ResponseDTO {\n\n");

        // UUID field - use the actual field name from config
        sb.append("    private UUID ").append(config.getUuidField()).append(";\n\n");

        // Business fields
        for (FieldConfig field : config.getFields()) {
            sb.append("    private ").append(getJavaType(field.getType())).append(" ").append(field.getName()).append(";\n");
        }

        sb.append("\n    private String status;\n\n");
        sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
        sb.append("    private LocalDateTime createdAt;\n\n");
        sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
        sb.append("    private LocalDateTime updatedAt;\n\n");
        sb.append("    private String createdBy;\n\n");
        sb.append("    private String updatedBy;\n\n");
        sb.append("    private BigDecimal auditTrackerId;\n");

        sb.append("}\n");

        return sb.toString();
    }

    private void generateRequestField(StringBuilder sb, FieldConfig field) {
        // Validation annotations - FIXED null check
        if (field.getValidations() != null) {
            for (var validation : field.getValidations()) {
                sb.append("    @").append(validation.getAnnotation());

                // Safely get attributes
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
                    // Handle case where there are no attributes but has message
                    sb.append("(message = \"").append(validation.getMessage()).append("\")");
                }
                sb.append("\n");
            }
        }

        // JsonFormat for dates
        if ("LocalDate".equals(field.getType())) {
            sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd\")\n");
        } else if ("LocalDateTime".equals(field.getType())) {
            sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
        }

        // Field declaration
        sb.append("    private ").append(field.getType()).append(" ").append(field.getName()).append(";\n");
    }

    private void generateResponseField(StringBuilder sb, FieldConfig field) {
        sb.append("    private ").append(field.getType()).append(" ").append(field.getName()).append(";\n");
    }
}