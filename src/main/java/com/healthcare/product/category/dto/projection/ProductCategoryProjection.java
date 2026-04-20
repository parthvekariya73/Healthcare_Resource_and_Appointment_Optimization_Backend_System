package com.healthcare.product.category.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductCategoryProjection {

    Long getId();
    UUID getCategoryUuid();
    String getCategoryCode();
    String getCategoryName();
    String getDescription();
    Integer getSortOrder();
    String getStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
    String getUpdatedBy();
    BigDecimal getAuditTrackerId();
}
