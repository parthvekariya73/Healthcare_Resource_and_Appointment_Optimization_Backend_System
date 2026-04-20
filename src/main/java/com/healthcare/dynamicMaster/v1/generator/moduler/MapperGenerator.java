package com.healthcare.dynamicMaster.v1.generator.moduler;

import com.healthcare.dynamicMaster.v1.generator.BaseGenerator;
import com.healthcare.dynamicMaster.v1.generator.config.FieldConfig;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MapperGenerator extends BaseGenerator {

    public String generate(ModuleConfig config) {
        log.info("Generating Mapper for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        // Package declaration
        sb.append("package ").append(config.getPackageName()).append(".mapper;\n\n");

        // Imports
        sb.append("import ").append(config.getPackageName()).append(".dto.projection.").append(getEntityName(config)).append("Projection;\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.request.").append(getEntityName(config)).append("RequestDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.response.").append(getEntityName(config)).append("ResponseDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".entity.").append(getEntityName(config)).append(";\n");
        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import lombok.extern.slf4j.Slf4j;\n");
        sb.append("import org.springframework.stereotype.Component;\n");
        sb.append("\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.util.UUID;\n");
        sb.append("\n");

        // Class declaration
        sb.append("@Slf4j\n");
        sb.append("@Component\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(getEntityName(config)).append("Mapper {\n\n");

        // ToEntity method
        generateToEntity(sb, config);

        // UpdateEntity method
        generateUpdateEntity(sb, config);

        // ToResponseDTO method
        generateToResponseDTO(sb, config);

        // Helper methods
        generateHelperMethods(sb, config);

        sb.append("}\n");

        return sb.toString();
    }

    private void generateToEntity(StringBuilder sb, ModuleConfig config) {
        sb.append("    public ").append(getEntityName(config)).append(" toEntity(")
                .append(getEntityName(config)).append("RequestDTO dto) {\n");
        sb.append("        return ").append(getEntityName(config)).append(".builder()\n");

        for (FieldConfig field : config.getFields()) {
            sb.append("                .").append(field.getName()).append("(dto.get")
                    .append(Character.toUpperCase(field.getName().charAt(0)))
                    .append(field.getName().substring(1)).append("())\n");
        }

        sb.append("                .status((short) 1)\n");
        sb.append("                .build();\n");
        sb.append("    }\n\n");
    }

    private void generateUpdateEntity(StringBuilder sb, ModuleConfig config) {
        sb.append("    public void updateEntity(").append(getEntityName(config)).append(" entity, ")
                .append(getEntityName(config)).append("RequestDTO dto) {\n");

        for (FieldConfig field : config.getFields()) {
            sb.append("        if (dto.get")
                    .append(Character.toUpperCase(field.getName().charAt(0)))
                    .append(field.getName().substring(1)).append("() != null) {\n");
            sb.append("            entity.set").append(Character.toUpperCase(field.getName().charAt(0)))
                    .append(field.getName().substring(1)).append("(dto.get")
                    .append(Character.toUpperCase(field.getName().charAt(0)))
                    .append(field.getName().substring(1)).append("());\n");
            sb.append("        }\n");
        }

        sb.append("        entity.setUpdatedAt(LocalDateTime.now());\n");
        sb.append("    }\n\n");
    }

    private void generateToResponseDTO(StringBuilder sb, ModuleConfig config) {
        String entityName = getEntityName(config);
        String uuidField = config.getUuidField();
        String getterPrefix = "get" + Character.toUpperCase(uuidField.charAt(0)) + uuidField.substring(1);

        sb.append("    public ").append(entityName).append("ResponseDTO toResponseDTO(")
                .append(entityName).append("Projection projection) {\n");
        sb.append("        if (projection == null) return null;\n\n");
        sb.append("        return ").append(entityName).append("ResponseDTO.builder()\n");
        sb.append("                .").append(uuidField).append("(projection.").append(getterPrefix).append("())\n");

        for (FieldConfig field : config.getFields()) {
            String fieldName = field.getName();
            String fieldGetter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            sb.append("                .").append(fieldName)
                    .append("(projection.").append(fieldGetter).append("())\n");
        }

        sb.append("                .status(projection.getStatus())\n");
        sb.append("                .createdAt(projection.getCreatedAt())\n");
        sb.append("                .updatedAt(projection.getUpdatedAt())\n");
        sb.append("                .createdBy(projection.getCreatedBy())\n");
        sb.append("                .updatedBy(projection.getUpdatedBy())\n");
        sb.append("                .build();\n");
        sb.append("    }\n\n");
    }

    private void generateHelperMethods(StringBuilder sb, ModuleConfig config) {
        // Add helper methods for resolving IDs from UUIDs if needed
        sb.append("    // Helper methods for resolving IDs from UUIDs\n");
        sb.append("    // Implement based on your relationship mappings\n");
    }
}