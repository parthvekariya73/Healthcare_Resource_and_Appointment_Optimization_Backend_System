package com.healthcare.dynamicMaster.v1.generator.moduler;

import com.healthcare.dynamicMaster.v1.generator.BaseGenerator;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import com.healthcare.dynamicMaster.v1.generator.config.UniqueConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class ServiceGenerator extends BaseGenerator {

    public String generateInterface(ModuleConfig config) {
        log.info("Generating Service Interface for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(config.getPackageName()).append(".service;\n\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.request.").append(getEntityName(config)).append("RequestDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.response.").append(getEntityName(config)).append("ResponseDTO;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("\n");
        sb.append("import java.util.UUID;\n");
        sb.append("\n");

        sb.append("public interface ").append(getEntityName(config)).append("Service {\n\n");

        // Create
        sb.append("    ").append(getEntityName(config)).append("ResponseDTO create(")
                .append(getEntityName(config)).append("RequestDTO requestDTO);\n\n");

        // Update
        sb.append("    ").append(getEntityName(config)).append("ResponseDTO update(UUID uuid, ")
                .append(getEntityName(config)).append("RequestDTO requestDTO);\n\n");

        // Get by UUID
        sb.append("    ").append(getEntityName(config)).append("ResponseDTO getByUuid(UUID uuid);\n\n");

        // Get all with pagination
        sb.append("    Page<").append(getEntityName(config)).append("ResponseDTO> getAll(int page, int size, String search);\n\n");

        // Delete
        sb.append("    void delete(UUID uuid);\n\n");

        // Update status
        sb.append("    ").append(getEntityName(config)).append("ResponseDTO updateStatus(UUID uuid, String status);\n");

        sb.append("}\n");

        return sb.toString();
    }

    public String generateImpl(ModuleConfig config) {
        log.info("Generating Service Implementation for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(config.getPackageName()).append(".service.impl;\n\n");

        // Imports
        sb.append("import ").append(config.getPackageName()).append(".dto.projection.").append(getEntityName(config)).append("Projection;\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.request.").append(getEntityName(config)).append("RequestDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.response.").append(getEntityName(config)).append("ResponseDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".entity.").append(getEntityName(config)).append(";\n");
        sb.append("import ").append(config.getPackageName()).append(".mapper.").append(getEntityName(config)).append("Mapper;\n");
        sb.append("import ").append(config.getPackageName()).append(".repository.").append(getEntityName(config)).append("Repository;\n");
        sb.append("import ").append(config.getPackageName()).append(".service.").append(getEntityName(config)).append("Service;\n");
        sb.append("import com.healthcare.common.apputil.exception.custom.BusinessException;\n");
        sb.append("import com.healthcare.common.apputil.response.ErrorCode;\n");
        sb.append("import com.healthcare.common.apputil.enums.StatusEnum;\n");
        sb.append("import com.healthcare.common.apputil.utils.commonutil.SecurityUtils;\n");
        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import lombok.extern.slf4j.Slf4j;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.PageRequest;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");
        sb.append("import org.springframework.data.domain.Sort;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import org.springframework.transaction.annotation.Transactional;\n");
        sb.append("\n");
        sb.append("import java.util.UUID;\n");
        sb.append("\n");

        // Class declaration
        sb.append("@Slf4j\n");
        sb.append("@Service\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("@Transactional(readOnly = true)\n");
        sb.append("public class ").append(getEntityName(config)).append("ServiceImpl implements ")
                .append(getEntityName(config)).append("Service {\n\n");

        sb.append("    private final ").append(getEntityName(config)).append("Repository repository;\n");
        sb.append("    private final ").append(getEntityName(config)).append("Mapper mapper;\n\n");

        // Create method
        generateCreateMethod(sb, config);

        // Update method
        generateUpdateMethod(sb, config);

        // Get by UUID method
        generateGetByUuidMethod(sb, config);

        // Get all method
        generateGetAllMethod(sb, config);

        // Delete method
        generateDeleteMethod(sb, config);

        // Update status method
        generateUpdateStatusMethod(sb, config);

        // Helper methods
        generateHelperMethods(sb, config);

        sb.append("}\n");

        return sb.toString();
    }

    private void generateCreateMethod(StringBuilder sb, ModuleConfig config) {
        String entityName = getEntityName(config);
        String uuidField = config.getUuidField();
        String getUuidMethod = "get" + Character.toUpperCase(uuidField.charAt(0)) + uuidField.substring(1);

        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public ").append(entityName).append("ResponseDTO create(")
                .append(entityName).append("RequestDTO requestDTO) {\n");
        sb.append("        log.info(\"Creating new ").append(config.getModuleName()).append("\");\n\n");

        // Unique constraint validations
        for (UniqueConstraint constraint : config.getUniqueConstraints()) {
            String methodName = "existsBy" +
                    constraint.getFields().stream()
                            .map(f -> Character.toUpperCase(f.charAt(0)) + f.substring(1))
                            .collect(Collectors.joining("And"));

            String params = constraint.getFields().stream()
                    .map(f -> "requestDTO.get" + Character.toUpperCase(f.charAt(0)) + f.substring(1) + "()")
                    .collect(Collectors.joining(", "));

            sb.append("        if (repository.").append(methodName).append("(").append(params).append(")) {\n");
            sb.append("            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);\n");
            sb.append("        }\n\n");
        }

        sb.append("        ").append(entityName).append(" entity = mapper.toEntity(requestDTO);\n");
        sb.append("        ").append(entityName).append(" saved = repository.save(entity);\n\n");
        sb.append("        log.info(\"Created ").append(config.getModuleName())
                .append(" with UUID: {}\", saved.").append(getUuidMethod).append("());\n");
        sb.append("        return getByUuid(saved.").append(getUuidMethod).append("());\n");
        sb.append("    }\n\n");
    }

    private void generateUpdateMethod(StringBuilder sb, ModuleConfig config) {
        String entityName = getEntityName(config);
        String primaryKey = config.getPrimaryKey();
        String getIdMethod = "get" + Character.toUpperCase(primaryKey.charAt(0)) + primaryKey.substring(1);

        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public ").append(entityName).append("ResponseDTO update(UUID uuid, ")
                .append(entityName).append("RequestDTO requestDTO) {\n");
        sb.append("        log.info(\"Updating ").append(config.getModuleName()).append(" with UUID: {}\", uuid);\n\n");
        sb.append("        ").append(entityName).append(" entity = findByUuid(uuid);\n\n");

        // Unique constraint validations for update (excluding current)
        for (UniqueConstraint constraint : config.getUniqueConstraints()) {
            sb.append("        if (repository.existsBy");
            for (String field : constraint.getFields()) {
                sb.append(Character.toUpperCase(field.charAt(0))).append(field.substring(1));
            }
            sb.append("AndNotId(entity.").append(getIdMethod).append("(), ");
            for (String field : constraint.getFields()) {
                sb.append("requestDTO.get").append(Character.toUpperCase(field.charAt(0)))
                        .append(field.substring(1)).append("()");
                if (!field.equals(constraint.getFields().get(constraint.getFields().size() - 1))) {
                    sb.append(", ");
                }
            }
            sb.append(")) {\n");
            sb.append("            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);\n");
            sb.append("        }\n\n");
        }

        sb.append("        mapper.updateEntity(entity, requestDTO);\n");
        sb.append("        ").append(entityName).append(" saved = repository.save(entity);\n\n");
        sb.append("        log.info(\"Updated ").append(config.getModuleName()).append(" with UUID: {}\", uuid);\n");

        String uuidField = config.getUuidField();
        String getUuidMethod = "get" + Character.toUpperCase(uuidField.charAt(0)) + uuidField.substring(1);
        sb.append("        return getByUuid(saved.").append(getUuidMethod).append("());\n");
        sb.append("    }\n\n");
    }

    private void generateGetByUuidMethod(StringBuilder sb, ModuleConfig config) {
        sb.append("    @Override\n");
        sb.append("    public ").append(getEntityName(config)).append("ResponseDTO getByUuid(UUID uuid) {\n");
        sb.append("        log.info(\"Fetching ").append(config.getModuleName()).append(" with UUID: {}\", uuid);\n");
        sb.append("        return repository.findProjectionByUuid(uuid)\n");
        sb.append("                .map(mapper::toResponseDTO)\n");
        sb.append("                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));\n");
        sb.append("    }\n\n");
    }

    private void generateGetAllMethod(StringBuilder sb, ModuleConfig config) {
        sb.append("    @Override\n");
        sb.append("    public Page<").append(getEntityName(config)).append("ResponseDTO> getAll(int page, int size, String search) {\n");
        sb.append("        log.info(\"Fetching ").append(config.getModuleName()).append("s with pagination - page: {}, size: {}\", page, size);\n\n");
        sb.append("        Pageable pageable = PageRequest.of(page, size, Sort.by(\"createdAt\").descending());\n");
        sb.append("        Page<").append(getEntityName(config)).append("Projection> projectionPage = repository.findAllWithFilters(search, pageable);\n");
        sb.append("        return projectionPage.map(mapper::toResponseDTO);\n");
        sb.append("    }\n\n");
    }

    private void generateDeleteMethod(StringBuilder sb, ModuleConfig config) {
        String entityName = getEntityName(config);
        String primaryKey = config.getPrimaryKey();
        String getIdMethod = "get" + Character.toUpperCase(primaryKey.charAt(0)) + primaryKey.substring(1);

        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public void delete(UUID uuid) {\n");
        sb.append("        log.info(\"Deleting ").append(config.getModuleName()).append(" with UUID: {}\", uuid);\n\n");
        sb.append("        ").append(entityName).append(" entity = findByUuid(uuid);\n");
        sb.append("        repository.softDeleteById(entity.").append(getIdMethod).append("(), SecurityUtils.getCurrentUserId());\n");
        sb.append("        log.info(\"Deleted ").append(config.getModuleName()).append(" with UUID: {}\", uuid);\n");
        sb.append("    }\n\n");
    }

    private void generateUpdateStatusMethod(StringBuilder sb, ModuleConfig config) {
        String entityName = getEntityName(config);
        String uuidField = config.getUuidField();
        String getUuidMethod = "get" + Character.toUpperCase(uuidField.charAt(0)) + uuidField.substring(1);

        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public ").append(entityName).append("ResponseDTO updateStatus(UUID uuid, String status) {\n");
        sb.append("        log.info(\"Updating status for ").append(config.getModuleName()).append(" with UUID: {} to: {}\", uuid, status);\n\n");
        sb.append("        ").append(entityName).append(" entity = findByUuid(uuid);\n");
        sb.append("        Short statusCode = StatusEnum.fromName(status).getCode();\n");
        sb.append("        entity.setStatus(statusCode);\n");
        sb.append("        ").append(entityName).append(" saved = repository.save(entity);\n");
        sb.append("        return getByUuid(saved.").append(getUuidMethod).append("());\n");
        sb.append("    }\n\n");
    }

    private void generateHelperMethods(StringBuilder sb, ModuleConfig config) {
        String entityName = getEntityName(config);
        String uuidField = config.getUuidField();
        String getUuidMethod = "get" + Character.toUpperCase(uuidField.charAt(0)) + uuidField.substring(1);

        sb.append("    private ").append(entityName).append(" findByUuid(UUID uuid) {\n");
        sb.append("        return repository.findByUuid(uuid)\n");
        sb.append("                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));\n");
        sb.append("    }\n");
    }
}