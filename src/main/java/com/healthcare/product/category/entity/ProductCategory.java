package com.healthcare.product.category.entity;

import com.healthcare.common.apputil.utils.commonutil.CommonUtil;
import com.healthcare.common.apputil.utils.commonutil.SecurityUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mst_product_category", schema = "master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Long categoryId;

    @Column(name = "categoryUuid", nullable = false, updatable = false, unique = true, insertable = false, columnDefinition = "UUID DEFAULT gen_uuid_v7()")
    @Generated(event = EventType.INSERT)
    private UUID categoryUuid;

    @Column(name = "category_code", nullable = false, length = 50)
    @NotBlank(message = "Category code is required")
    @Size(min = 2, max = 50, message = "Category code must be between 2 and 50 characters")
    private String categoryCode;

    @Column(name = "category_name", nullable = false, length = 100)
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String categoryName;

    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Column(name = "sort_order", columnDefinition = "0")
    private Integer sortOrder;

    @Column(name = "status")
    @Builder.Default
    private Short status = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "audit_tracker_id", precision = 24)
    private BigDecimal auditTrackerId;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = 1;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.createdBy = SecurityUtils.getCurrentUserId();
        this.updatedBy = SecurityUtils.getCurrentUserId();
        this.auditTrackerId = CommonUtil.generateUniqueTxnNumber();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = SecurityUtils.getCurrentUserId();
    }
}
