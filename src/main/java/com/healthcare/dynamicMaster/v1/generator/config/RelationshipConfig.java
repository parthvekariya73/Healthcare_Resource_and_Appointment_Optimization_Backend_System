package com.healthcare.dynamicMaster.v1.generator.config;

import jakarta.persistence.CascadeType;
import lombok.Data;

@Data
public class RelationshipConfig {
    private String name;
    private String type;                // OneToOne, OneToMany, ManyToOne, ManyToMany
    private String targetEntity;
    private String joinColumn;
    private String mappedBy;
    private boolean lazy;
    private CascadeType cascade;
}
