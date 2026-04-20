package com.healthcare.dynamicMaster.v1.generator;

import com.healthcare.dynamicMaster.v1.generator.config.FieldConfig;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseGenerator {

    /**
     * Get the entity name from module configuration
     * Converts snake_case to PascalCase
     * Example: product_category -> ProductCategory
     */
    protected String getEntityName(ModuleConfig config) {
        String[] parts = config.getModuleName().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Convert field name to getter method name
     * Example: categoryName -> getCategoryName
     */
    protected String toGetterName(String fieldName) {
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * Convert field name to setter method name
     * Example: categoryName -> setCategoryName
     */
    protected String toSetterName(String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * Convert Java type to database column type
     */
    protected String toDbType(String javaType) {
        switch (javaType.toLowerCase()) {
            case "string": return "VARCHAR";
            case "long": return "BIGINT";
            case "integer": return "INTEGER";
            case "boolean": return "BOOLEAN";
            case "localdate": return "DATE";
            case "localdatetime": return "TIMESTAMP";
            case "bigdecimal": return "DECIMAL";
            case "uuid": return "UUID";
            default: return "VARCHAR";
        }
    }

    /**
     * Format value for annotation parameters
     */
    protected String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        return value.toString();
    }

    /**
     * Check if module has BigDecimal fields
     */
    protected boolean hasBigDecimalField(ModuleConfig config) {
        return config.getFields().stream()
                .anyMatch(f -> "BigDecimal".equals(f.getType()));
    }

    /**
     * Check if module has LocalDate fields
     */
    protected boolean hasLocalDateField(ModuleConfig config) {
        return config.getFields().stream()
                .anyMatch(f -> "LocalDate".equals(f.getType()));
    }

    /**
     * Check if module has LocalDateTime fields
     */
    protected boolean hasLocalDateTimeField(ModuleConfig config) {
        return config.getFields().stream()
                .anyMatch(f -> "LocalDateTime".equals(f.getType()));
    }

    /**
     * Get Java type from string
     */
    protected String getJavaType(String type) {
        switch (type.toLowerCase()) {
            case "string": return "String";
            case "long": return "Long";
            case "integer": return "Integer";
            case "int": return "int";
            case "boolean": return "Boolean";
            case "localdate": return "LocalDate";
            case "localdatetime": return "LocalDateTime";
            case "bigdecimal": return "BigDecimal";
            case "uuid": return "UUID";
            case "byte": return "Byte";
            case "short": return "Short";
            case "float": return "Float";
            case "double": return "Double";
            default: return "String";
        }
    }

    /**
     * Get default value for type
     */
    protected String getDefaultValueForType(String type) {
        switch (type.toLowerCase()) {
            case "string": return "null";
            case "long": return "0L";
            case "integer": return "0";
            case "boolean": return "false";
            case "bigdecimal": return "BigDecimal.ZERO";
            default: return "null";
        }
    }

    /**
     * Format field name for display
     */
    protected String formatFieldName(String fieldName) {
        // Convert camelCase to Title Case with spaces
        // Example: categoryName -> "Category Name"
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (i == 0) {
                result.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                result.append(" ").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Generate import statements for required types
     */
    protected String generateImports(ModuleConfig config) {
        StringBuilder sb = new StringBuilder();

        // Base imports
        sb.append("import jakarta.persistence.*;\n");
        sb.append("import lombok.*;\n");
        sb.append("import org.hibernate.annotations.CreationTimestamp;\n");
        sb.append("import org.hibernate.annotations.UpdateTimestamp;\n");
        sb.append("import org.hibernate.annotations.GenericGenerator;\n");

        // Date/time imports
        if (hasLocalDateTimeField(config) || config.getAuditFields() != null) {
            sb.append("import java.time.LocalDateTime;\n");
        }
        if (hasLocalDateField(config)) {
            sb.append("import java.time.LocalDate;\n");
        }

        // BigDecimal import
        if (hasBigDecimalField(config)) {
            sb.append("import java.math.BigDecimal;\n");
        }

        sb.append("import java.util.UUID;\n");

        return sb.toString();
    }

    /**
     * Generate package declaration
     */
    protected String generatePackageDeclaration(String packageName) {
        return "package " + packageName + ";\n\n";
    }

    /**
     * Generate validation annotations for a field
     */
    protected String generateValidationAnnotations(FieldConfig field) {
        StringBuilder sb = new StringBuilder();
        for (var validation : field.getValidations()) {
            sb.append("    @").append(validation.getAnnotation());
            if (!validation.getAttributes().isEmpty()) {
                sb.append("(");
                String attrs = validation.getAttributes().entrySet().stream()
                        .map(e -> e.getKey() + " = " + formatValue(e.getValue()))
                        .collect(Collectors.joining(", "));
                sb.append(attrs);
                if (validation.getMessage() != null) {
                    sb.append(", message = \"").append(validation.getMessage()).append("\"");
                }
                sb.append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Generate JSON format annotation for date fields
     */
    protected String generateJsonFormatAnnotation(FieldConfig field) {
        if ("LocalDate".equals(field.getType())) {
            return "    @JsonFormat(pattern = \"yyyy-MM-dd\")\n";
        } else if ("LocalDateTime".equals(field.getType())) {
            return "    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n";
        }
        return "";
    }

    /**
     * Convert package name to file path
     */
    protected String packageToPath(String packageName) {
        return packageName.replace('.', '/');
    }

    /**
     * Generate a random UUID placeholder
     */
    protected String generateUuidDefault() {
        return "UUID.randomUUID()";
    }

    /**
     * Generate current timestamp
     */
    protected String generateCurrentTimestamp() {
        return "LocalDateTime.now()";
    }

    /**
     * Generate SQL parameter placeholder
     */
    protected String sqlParam(String paramName) {
        return ":" + paramName;
    }

    /**
     * Generate named parameter for native query
     */
    protected String nativeParam(String paramName) {
        return "CAST(:" + paramName + " AS text)";
    }

    /**
     * Generate UUID cast for native query
     */
    protected String uuidCast(String paramName) {
        return "CAST(:" + paramName + " AS uuid)";
    }

    /**
     * Generate LIKE condition for search
     */
    protected String likeCondition(String columnName, String paramName) {
        return "LOWER(" + columnName + ") LIKE LOWER(CONCAT('%', " + nativeParam(paramName) + ", '%'))";
    }

    /**
     * Generate equality condition
     */
    protected String eqCondition(String columnName, String paramName, String type) {
        if ("UUID".equals(type)) {
            return columnName + " = " + uuidCast(paramName);
        }
        return columnName + " = " + nativeParam(paramName);
    }

    /**
     * Generate NULL check condition
     */
    protected String nullCheck(String paramName) {
        return nativeParam(paramName) + " IS NULL";
    }

    /**
     * Generate pagination clause
     */
    protected String paginationClause() {
        return "LIMIT :limit OFFSET :offset";
    }

    /**
     * Generate ORDER BY clause
     */
    protected String orderByClause(String columnName, String direction) {
        return "ORDER BY " + columnName + " " + direction;
    }

    /**
     * Get current user ID (placeholder - implement based on your security)
     */
    protected String getCurrentUserIdMethod() {
        return "SecurityUtils.getCurrentUserId()";
    }

    /**
     * Get current timestamp method
     */
    protected String getCurrentTimestampMethod() {
        return "LocalDateTime.now()";
    }
}