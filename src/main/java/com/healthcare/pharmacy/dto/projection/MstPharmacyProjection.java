package com.healthcare.pharmacy.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Spring Data JPA interface projection for MstPharmacy.
 * Used with native SQL queries — avoids full entity hydration.
 */
public interface MstPharmacyProjection {

    /** Internal primary key (not exposed in API responses) */
    Long getPharmacy_id();

    /** Public UUID identifier */
    UUID getPharmacyUuid();

    // ── Business Fields ──────────────────────────────────────
    String getPharmacyCode();
    String getPharmacyName();
    String getLicenseNumber();
    String getOwnerName();
    String getOwnerMobile();
    String getOwnerEmail();
    Long getPharmacyTypeId();
    Long getPharmacyCategoryId();
    String getAddressLine1();
    String getAddressLine2();
    Long getCityId();
    Long getStateId();
    Long getCountryId();
    String getPincode();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
    Boolean getHasOwnDelivery();
    Boolean getIsOperationActive();
    BigDecimal getCreditLimit();
    Short getCreditDays();
    String getRemark();
    Long getProfileFileMetaId();

    // ── System / Audit Fields ────────────────────────────────
    String getStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
    String getUpdatedBy();
    BigDecimal getAuditTrackerId();
}