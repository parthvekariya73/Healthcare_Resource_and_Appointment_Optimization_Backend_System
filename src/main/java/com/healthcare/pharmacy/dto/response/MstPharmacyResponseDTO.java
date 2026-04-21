package com.healthcare.pharmacy.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/**
 * Response DTO for MstPharmacy.
 * Returned by all API endpoints. Includes audit metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MstPharmacyResponseDTO {

    /** Public identifier — use this in all client-side references */
    private UUID pharmacyUuid;

    private String pharmacyCode;
    private String pharmacyName;
    private String licenseNumber;
    private String ownerName;
    private String ownerMobile;
    private String ownerEmail;
    private Long pharmacyTypeId;
    private Long pharmacyCategoryId;
    private String addressLine1;
    private String addressLine2;
    private Long cityId;
    private Long stateId;
    private Long countryId;
    private String pincode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean hasOwnDelivery;
    private Boolean isOperationActive;
    private BigDecimal creditLimit;
    private Short creditDays;
    private String remark;
    private Long profileFileMetaId;

    /** Current status: 'active' | 'inactive' | 'deleted' */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    private BigDecimal auditTrackerId;
}