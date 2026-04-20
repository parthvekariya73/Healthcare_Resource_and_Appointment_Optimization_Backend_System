package com.healthcare.dynamicMaster.v1.generator.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldConfig {
    private String name;                // Field name
    private String columnName;          // Database column name
    private String type;                // Java type: String, Long, Integer, Boolean, LocalDate, LocalDateTime, UUID
    private boolean nullable;           // Is nullable
    private boolean updatable;          // Can be updated
    private boolean insertable;         // Can be inserted
    private Integer length;             // For String fields
    private Integer precision;          // For BigDecimal
    private Integer scale;              // For BigDecimal
    private String defaultValue;        // Default value
    private List<ValidationRule> validations = new ArrayList<>();
    private boolean unique;             // Is unique
    private boolean searchable;         // Can be searched
    private boolean filterable;         // Can be filtered
    private boolean sortable;           // Can be sorted
}
