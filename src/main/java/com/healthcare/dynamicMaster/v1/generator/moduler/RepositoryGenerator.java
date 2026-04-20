package com.healthcare.dynamicMaster.v1.generator.moduler;

import com.healthcare.dynamicMaster.v1.generator.BaseGenerator;
import com.healthcare.dynamicMaster.v1.generator.config.FieldConfig;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import com.healthcare.dynamicMaster.v1.generator.config.UniqueConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class RepositoryGenerator extends BaseGenerator {

    public String generate(ModuleConfig config) {
        log.info("Generating Repository for module: {}", config.getModuleName());

        StringBuilder sb = new StringBuilder();

        // Package declaration
        sb.append("package ").append(config.getPackageName()).append(".repository;\n\n");

        // Imports
        sb.append("import ").append(config.getPackageName()).append(".entity.").append(getEntityName(config)).append(";\n");
        sb.append("import ").append(config.getPackageName()).append(".dto.projection.").append(getEntityName(config)).append("Projection;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");
        sb.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        sb.append("import org.springframework.data.jpa.repository.Modifying;\n");
        sb.append("import org.springframework.data.jpa.repository.Query;\n");
        sb.append("import org.springframework.data.repository.query.Param;\n");
        sb.append("import org.springframework.stereotype.Repository;\n");
        sb.append("\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import java.util.UUID;\n");
        sb.append("\n");

        // Repository interface
        sb.append("@Repository\n");
        sb.append("public interface ").append(getEntityName(config)).append("Repository extends JpaRepository<")
                .append(getEntityName(config)).append(", Long> {\n\n");

        // ==================== BASIC FIND METHODS ====================

        // Find by UUID
        sb.append("    @Query(\"SELECT e FROM ").append(getEntityName(config))
                .append(" e WHERE e.").append(config.getUuidField()).append(" = :uuid AND e.status != 9\")\n");
        sb.append("    Optional<").append(getEntityName(config)).append("> findByUuid(@Param(\"uuid\") UUID uuid);\n\n");

        // Find ID by UUID
        sb.append("    @Query(\"SELECT e.").append(config.getPrimaryKey()).append(" FROM ")
                .append(getEntityName(config)).append(" e WHERE e.").append(config.getUuidField())
                .append(" = :uuid AND e.status != 9\")\n");
        sb.append("    Optional<Long> findIdByUuid(@Param(\"uuid\") UUID uuid);\n\n");

        // ==================== UNIQUE CONSTRAINT METHODS ====================

        // Generate exists methods for unique constraints
        for (UniqueConstraint constraint : config.getUniqueConstraints()) {
            generateExistsMethods(sb, constraint, config);
        }

        // ==================== SOFT DELETE ====================

        sb.append("    @Modifying\n");
        sb.append("    @Query(\"UPDATE ").append(getEntityName(config))
                .append(" e SET e.status = 9, e.deletedAt = CURRENT_TIMESTAMP, e.deletedBy = :deletedBy WHERE e.")
                .append(config.getPrimaryKey()).append(" = :id\")\n");
        sb.append("    void softDeleteById(@Param(\"id\") Long id, @Param(\"deletedBy\") Long deletedBy);\n\n");

        // ==================== PAGINATED FIND WITH FILTERS ====================

        generateFindAllWithFilters(sb, config);

        // ==================== FIND PROJECTION BY UUID ====================

        generateFindProjectionByUuid(sb, config);

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * Generates exists methods for unique constraints
     * Creates both regular and "AndNotId" versions
     */
    private void generateExistsMethods(StringBuilder sb, UniqueConstraint constraint, ModuleConfig config) {
        String methodBaseName = "existsBy" +
                constraint.getFields().stream()
                        .map(f -> Character.toUpperCase(f.charAt(0)) + f.substring(1))
                        .collect(Collectors.joining("And"));

        String conditions = constraint.getFields().stream()
                .map(f -> "LOWER(e." + f + ") = LOWER(:" + f + ")")
                .collect(Collectors.joining(" AND "));

        String params = constraint.getFields().stream()
                .map(f -> "@Param(\"" + f + "\") String " + f)
                .collect(Collectors.joining(", "));

        // Regular exists method (without exclude ID)
        sb.append("    @Query(\"SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ")
                .append(getEntityName(config)).append(" e WHERE ");
        sb.append(conditions);
        sb.append(" AND e.status != 9\")\n");
        sb.append("    boolean ").append(methodBaseName).append("(").append(params).append(");\n\n");

        // Exists method with exclude ID (for updates)
        sb.append("    @Query(\"SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ")
                .append(getEntityName(config)).append(" e WHERE ");
        sb.append(conditions);
        sb.append(" AND e.status != 9 AND e.").append(config.getPrimaryKey()).append(" != :excludeId\")\n");
        sb.append("    boolean ").append(methodBaseName).append("AndNotId(\n");
        sb.append("            @Param(\"excludeId\") Long excludeId,\n");
        sb.append("            ").append(params).append("\n");
        sb.append("    );\n\n");
    }

    /**
     * Generates paginated find method with search filters
     */
    private void generateFindAllWithFilters(StringBuilder sb, ModuleConfig config) {
        sb.append("    @Query(value = \"\"\"\n");
        sb.append("        SELECT \n");
        sb.append("            e.").append(config.getPrimaryKey()).append(" as id,\n");
        sb.append("            e.").append(config.getUuidField()).append(" as uuid,\n");

        // Add business fields
        for (FieldConfig field : config.getFields()) {
            sb.append("            e.").append(field.getColumnName()).append(" as ").append(field.getName()).append(",\n");
        }

        sb.append("            CASE \n");
        sb.append("                WHEN e.status = 1 THEN 'active'\n");
        sb.append("                WHEN e.status = 0 THEN 'inactive'\n");
        sb.append("                WHEN e.status = 9 THEN 'deleted'\n");
        sb.append("                ELSE 'unknown'\n");
        sb.append("            END as status,\n");
        sb.append("            e.created_at as createdAt,\n");
        sb.append("            e.updated_at as updatedAt,\n");
        sb.append("            mu1.full_name as createdBy,\n");
        sb.append("            mu2.full_name as updatedBy\n");
        sb.append("        FROM ").append(config.getSchemaName()).append(".").append(config.getTableName()).append(" e\n");
        sb.append("        LEFT JOIN master.mst_users mu1 ON e.created_by = mu1.user_id\n");
        sb.append("        LEFT JOIN master.mst_users mu2 ON e.updated_by = mu2.user_id\n");
        sb.append("        WHERE e.status != 9\n");

        // Search condition
        sb.append("          AND (CAST(:search AS text) IS NULL OR \n");

        boolean first = true;
        for (FieldConfig field : config.getFields()) {
            if (field.isSearchable()) {
                if (!first) {
                    sb.append("               OR \n");
                }
                sb.append("               LOWER(e.").append(field.getColumnName()).append(") LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))\n");
                first = false;
            }
        }

        sb.append("              )\n");
        sb.append("        ORDER BY e.created_at DESC\n");
        sb.append("        \"\"\",\n");
        sb.append("        countQuery = \"\"\"\n");
        sb.append("        SELECT COUNT(1)\n");
        sb.append("        FROM ").append(config.getSchemaName()).append(".").append(config.getTableName()).append(" e\n");
        sb.append("        WHERE e.status != 9\n");
        sb.append("        \"\"\",\n");
        sb.append("        nativeQuery = true)\n");
        sb.append("    Page<").append(getEntityName(config)).append("Projection> findAllWithFilters(\n");
        sb.append("            @Param(\"search\") String search,\n");
        sb.append("            Pageable pageable);\n\n");
    }

    /**
     * Generates method to find projection by UUID
     */
    private void generateFindProjectionByUuid(StringBuilder sb, ModuleConfig config) {
        sb.append("    @Query(value = \"\"\"\n");
        sb.append("        SELECT \n");
        sb.append("            e.").append(config.getPrimaryKey()).append(" as id,\n");
        sb.append("            e.").append(config.getUuidField()).append(" as uuid,\n");

        for (FieldConfig field : config.getFields()) {
            sb.append("            e.").append(field.getColumnName()).append(" as ").append(field.getName()).append(",\n");
        }

        sb.append("            CASE \n");
        sb.append("                WHEN e.status = 1 THEN 'active'\n");
        sb.append("                WHEN e.status = 0 THEN 'inactive'\n");
        sb.append("                WHEN e.status = 9 THEN 'deleted'\n");
        sb.append("                ELSE 'unknown'\n");
        sb.append("            END as status,\n");
        sb.append("            e.created_at as createdAt,\n");
        sb.append("            e.updated_at as updatedAt,\n");
        sb.append("            mu1.full_name as createdBy,\n");
        sb.append("            mu2.full_name as updatedBy\n");
        sb.append("        FROM ").append(config.getSchemaName()).append(".").append(config.getTableName()).append(" e\n");
        sb.append("        LEFT JOIN master.mst_users mu1 ON e.created_by = mu1.user_id\n");
        sb.append("        LEFT JOIN master.mst_users mu2 ON e.updated_by = mu2.user_id\n");
        sb.append("        WHERE e.").append(config.getUuidField()).append(" = CAST(:uuid AS uuid) AND e.status != 9\n");
        sb.append("        \"\"\", nativeQuery = true)\n");
        sb.append("    Optional<").append(getEntityName(config)).append("Projection> findProjectionByUuid(@Param(\"uuid\") UUID uuid);\n\n");
    }
}