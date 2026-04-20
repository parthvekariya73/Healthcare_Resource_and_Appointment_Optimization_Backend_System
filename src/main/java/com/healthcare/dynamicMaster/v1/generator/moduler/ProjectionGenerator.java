package com.healthcare.dynamicMaster.v1.generator.moduler;

import com.healthcare.dynamicMaster.v1.generator.BaseGenerator;
import com.healthcare.dynamicMaster.v1.generator.config.FieldConfig;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectionGenerator extends BaseGenerator {

    public String generate(ModuleConfig config) {
        StringBuilder sb = new StringBuilder();
        String entityName = getEntityName(config);

        sb.append("package ").append(config.getPackageName()).append(".dto.projection;\n\n");
        sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.UUID;\n\n");

        sb.append("public interface ").append(entityName).append("Projection {\n\n");

        // ID field
        sb.append("    Long getId();\n");
        sb.append("    UUID get").append(Character.toUpperCase(config.getUuidField().charAt(0)))
                .append(config.getUuidField().substring(1)).append("();\n");

        // Business fields
        for (FieldConfig field : config.getFields()) {
            String getter = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            sb.append("    ").append(getJavaType(field.getType())).append(" ").append(getter).append("();\n");
        }

        sb.append("    String getStatus();\n");
        sb.append("    LocalDateTime getCreatedAt();\n");
        sb.append("    LocalDateTime getUpdatedAt();\n");
        sb.append("    String getCreatedBy();\n");
        sb.append("    String getUpdatedBy();\n");
        sb.append("    BigDecimal getAuditTrackerId();\n");

        sb.append("}\n");

        return sb.toString();
    }
}