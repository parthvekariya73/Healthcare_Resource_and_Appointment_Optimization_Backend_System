package com.healthcare.pharmacy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

/**
 * Request DTO for MstPharmacy create/update operations.
 * All fields are validated via Jakarta Validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MstPharmacyRequestDTO {

    private String pharmacyCode;

    @NotBlank(message = "Pharmacy name is required")
    private String pharmacyName;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String ownerName;

    private String ownerMobile;

    private String ownerEmail;

    private Long pharmacyTypeId;

    private Long pharmacyCategoryId;

    @NotBlank(message = "Address is required")
    private String addressLine1;

    private String addressLine2;

    @NotNull(message = "City is required")
    private Long cityId;

    @NotNull(message = "State is required")
    private Long stateId;

    @NotNull(message = "Country is required")
    private Long countryId;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Boolean hasOwnDelivery;

    private Boolean isOperationActive;

    private BigDecimal creditLimit;

    private Short creditDays;

    private String remark;

    private Long profileFileMetaId;

}