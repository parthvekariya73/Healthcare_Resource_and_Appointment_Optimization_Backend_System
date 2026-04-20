package com.healthcare.dynamicMaster.v1.generator.moduler;

import com.healthcare.dynamicMaster.v1.generator.BaseGenerator;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ControllerGenerator extends BaseGenerator {

    public String generate(ModuleConfig config) {
        log.info("Generating Controller for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        // Package declaration
        sb.append("package ").append(config.getPackageName()).append(".controller;\n\n");

        // Imports
        sb.append("import ").append(config.getPackageName()).append(".dto.request.").append(getEntityName(config)).append("RequestDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.response.").append(getEntityName(config)).append("ResponseDTO;\n");
        sb.append("import ").append(config.getPackageName()).append(".service.").append(getEntityName(config)).append("Service;\n");
        sb.append("import com.healthcare.common.apputil.response.ApiResponse;\n");
        sb.append("import com.healthcare.common.apputil.utils.commonutil.CommonUtil;\n");
        sb.append("import jakarta.servlet.http.HttpServletRequest;\n");
        sb.append("import jakarta.validation.Valid;\n");
        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import lombok.extern.slf4j.Slf4j;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.http.HttpStatus;\n");
        sb.append("import org.springframework.http.ResponseEntity;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("\n");
        sb.append("import java.util.UUID;\n");
        sb.append("\n");

        // Class declaration
        sb.append("@Slf4j\n");
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/api/v1/").append(config.getModuleName().replace("_", "-")).append("\")\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(getEntityName(config)).append("Controller {\n\n");

        sb.append("    private final ").append(getEntityName(config)).append("Service service;\n\n");

        // Create endpoint
        generateCreateEndpoint(sb, config);

        // Update endpoint
        generateUpdateEndpoint(sb, config);

        // Get by UUID endpoint
        generateGetByUuidEndpoint(sb, config);

        // Get all endpoint
        generateGetAllEndpoint(sb, config);

        // Delete endpoint
        generateDeleteEndpoint(sb, config);

        // Update status endpoint
        generateUpdateStatusEndpoint(sb, config);

        sb.append("}\n");

        return sb.toString();
    }

    private void generateCreateEndpoint(StringBuilder sb, ModuleConfig config) {
        sb.append("    @PostMapping\n");
        sb.append("    public ResponseEntity<ApiResponse<").append(getEntityName(config))
                .append("ResponseDTO>> create(\n");
        sb.append("            @Valid @RequestBody ").append(getEntityName(config))
                .append("RequestDTO requestDTO,\n");
        sb.append("            HttpServletRequest httpRequest) {\n\n");
        sb.append("        ").append(getEntityName(config)).append("ResponseDTO response = service.create(requestDTO);\n");
        sb.append("        return ResponseEntity.status(HttpStatus.CREATED)\n");
        sb.append("                .body(ApiResponse.success(\n");
        sb.append("                        \"").append(getEntityName(config)).append(" created successfully\",\n");
        sb.append("                        response,\n");
        sb.append("                        CommonUtil.getRequestId(httpRequest)));\n");
        sb.append("    }\n\n");
    }

    private void generateUpdateEndpoint(StringBuilder sb, ModuleConfig config) {
        sb.append("    @PutMapping(\"/{uuid}\")\n");
        sb.append("    public ResponseEntity<ApiResponse<").append(getEntityName(config))
                .append("ResponseDTO>> update(\n");
        sb.append("            @PathVariable UUID uuid,\n");
        sb.append("            @Valid @RequestBody ").append(getEntityName(config))
                .append("RequestDTO requestDTO,\n");
        sb.append("            HttpServletRequest httpRequest) {\n\n");
        sb.append("        ").append(getEntityName(config)).append("ResponseDTO response = service.update(uuid, requestDTO);\n");
        sb.append("        return ResponseEntity.ok(\n");
        sb.append("                ApiResponse.success(\n");
        sb.append("                        \"").append(getEntityName(config)).append(" updated successfully\",\n");
        sb.append("                        response,\n");
        sb.append("                        CommonUtil.getRequestId(httpRequest)));\n");
        sb.append("    }\n\n");
    }

    private void generateGetByUuidEndpoint(StringBuilder sb, ModuleConfig config) {
        sb.append("    @GetMapping(\"/{uuid}\")\n");
        sb.append("    public ResponseEntity<ApiResponse<").append(getEntityName(config))
                .append("ResponseDTO>> getByUuid(\n");
        sb.append("            @PathVariable UUID uuid,\n");
        sb.append("            HttpServletRequest httpRequest) {\n\n");
        sb.append("        ").append(getEntityName(config)).append("ResponseDTO response = service.getByUuid(uuid);\n");
        sb.append("        return ResponseEntity.ok(\n");
        sb.append("                ApiResponse.success(\n");
        sb.append("                        \"").append(getEntityName(config)).append(" retrieved successfully\",\n");
        sb.append("                        response,\n");
        sb.append("                        CommonUtil.getRequestId(httpRequest)));\n");
        sb.append("    }\n\n");
    }

    private void generateGetAllEndpoint(StringBuilder sb, ModuleConfig config) {
        sb.append("    @GetMapping\n");
        sb.append("    public ResponseEntity<ApiResponse<Page<").append(getEntityName(config))
                .append("ResponseDTO>>> getAll(\n");
        sb.append("            @RequestParam(defaultValue = \"1\") int page,\n");
        sb.append("            @RequestParam(defaultValue = \"10\") int size,\n");
        sb.append("            @RequestParam(required = false) String search,\n");
        sb.append("            HttpServletRequest httpRequest) {\n\n");
        sb.append("        Page<").append(getEntityName(config)).append("ResponseDTO> response = service.getAll(page - 1, size, search);\n");
        sb.append("        return ResponseEntity.ok(\n");
        sb.append("                ApiResponse.success(\n");
        sb.append("                        \"").append(getEntityName(config)).append("s retrieved successfully\",\n");
        sb.append("                        response,\n");
        sb.append("                        CommonUtil.getRequestId(httpRequest)));\n");
        sb.append("    }\n\n");
    }

    private void generateDeleteEndpoint(StringBuilder sb, ModuleConfig config) {
        sb.append("    @DeleteMapping(\"/{uuid}\")\n");
        sb.append("    public ResponseEntity<ApiResponse<Void>> delete(\n");
        sb.append("            @PathVariable UUID uuid,\n");
        sb.append("            HttpServletRequest httpRequest) {\n\n");
        sb.append("        service.delete(uuid);\n");
        sb.append("        return ResponseEntity.ok(\n");
        sb.append("                ApiResponse.success(\n");
        sb.append("                        \"").append(getEntityName(config)).append(" deleted successfully\",\n");
        sb.append("                        null,\n");
        sb.append("                        CommonUtil.getRequestId(httpRequest)));\n");
        sb.append("    }\n\n");
    }

    private void generateUpdateStatusEndpoint(StringBuilder sb, ModuleConfig config) {
        sb.append("    @PatchMapping(\"/{uuid}/status\")\n");
        sb.append("    public ResponseEntity<ApiResponse<").append(getEntityName(config))
                .append("ResponseDTO>> updateStatus(\n");
        sb.append("            @PathVariable UUID uuid,\n");
        sb.append("            @RequestParam String status,\n");
        sb.append("            HttpServletRequest httpRequest) {\n\n");
        sb.append("        ").append(getEntityName(config)).append("ResponseDTO response = service.updateStatus(uuid, status);\n");
        sb.append("        return ResponseEntity.ok(\n");
        sb.append("                ApiResponse.success(\n");
        sb.append("                        \"").append(getEntityName(config)).append(" status updated successfully\",\n");
        sb.append("                        response,\n");
        sb.append("                        CommonUtil.getRequestId(httpRequest)));\n");
        sb.append("    }\n\n");
    }
}