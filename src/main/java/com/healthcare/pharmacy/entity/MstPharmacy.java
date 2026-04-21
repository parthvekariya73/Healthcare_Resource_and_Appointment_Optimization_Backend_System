package com.healthcare.pharmacy.entity;

import com.healthcare.common.apputil.utils.commonutil.CommonUtil;
import com.healthcare.common.apputil.utils.commonutil.SecurityUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;

@Entity
@Table(
    name = "mst_pharmacy",
    schema = "pharmacy",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_pharmacy_license", columnNames = {"licenseNumber"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MstPharmacy {

    // ── Primary Key ─────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_id")
    private Long pharmacy_id;

    // ── UUID (DB-generated UUID v7) ─────────────────────
    @Column(name = "pharmacyUuid",
            nullable = false, updatable = false, unique = true,
            insertable = false, columnDefinition = "UUID DEFAULT gen_uuid_v7()")
    @Generated(event = EventType.INSERT)
    private UUID pharmacyUuid;

    // ── Business Fields ─────────────────────────────────────
    @Column(name = "pharmacy_code", length = 30)
    private String pharmacyCode;

    @Column(name = "pharmacy_name", nullable = false, length = 200)
    @NotBlank(message = "Pharmacy name is required")
    private String pharmacyName;

    @Column(name = "license_number", nullable = false, length = 100)
    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @Column(name = "owner_mobile", length = 15)
    private String ownerMobile;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "pharmacy_type_id")
    private Long pharmacyTypeId;

    @Column(name = "pharmacy_category_id")
    private Long pharmacyCategoryId;

    @Column(name = "address_line1", nullable = false, length = 255)
    @NotBlank(message = "Address is required")
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city_id", nullable = false)
    @NotNull(message = "City is required")
    private Long cityId;

    @Column(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private Long stateId;

    @Column(name = "country_id", nullable = false)
    @NotNull(message = "Country is required")
    private Long countryId;

    @Column(name = "pincode", nullable = false, length = 10)
    @NotBlank(message = "Pincode is required")
    private String pincode;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "has_own_delivery", columnDefinition = "false")
    private Boolean hasOwnDelivery;

    @Column(name = "is_operation_active", columnDefinition = "false")
    private Boolean isOperationActive;

    @Column(name = "credit_limit", precision = 18, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "credit_days")
    private Short creditDays;

    @Column(name = "remark", length = 255)
    private String remark;

    @Column(name = "profile_file_meta_id")
    private Long profileFileMetaId;

    // ── Soft-Delete Status: 1=active, 0=inactive, 9=deleted ──
    @Column(name = "status")
    @Builder.Default
    private Short status = 1;

    // ── Audit Trail ─────────────────────────────────────────
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

    // ── JPA Lifecycle Callbacks ──────────────────────────────
    @PrePersist
    protected void onCreate() {
        if (this.status == null) this.status = 1;
        this.createdBy = SecurityUtils.getCurrentUserId();
        this.updatedBy = SecurityUtils.getCurrentUserId();
        this.auditTrackerId = CommonUtil.generateUniqueTxnNumber();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedBy = SecurityUtils.getCurrentUserId();
    }
}