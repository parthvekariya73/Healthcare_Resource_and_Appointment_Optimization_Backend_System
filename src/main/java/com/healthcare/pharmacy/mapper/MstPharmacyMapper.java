package com.healthcare.pharmacy.mapper;

import com.healthcare.pharmacy.dto.projection.MstPharmacyProjection;
import com.healthcare.pharmacy.dto.request.MstPharmacyRequestDTO;
import com.healthcare.pharmacy.dto.response.MstPharmacyResponseDTO;
import com.healthcare.pharmacy.entity.MstPharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper for MstPharmacy — converts between entity and DTOs.
 * All update operations are null-safe (support partial updates).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MstPharmacyMapper {

    /**
     * Map RequestDTO → new Entity.
     * Status is initialized to 1 (active).
     */
    public MstPharmacy toEntity(MstPharmacyRequestDTO dto) {
        return MstPharmacy.builder()
                .pharmacyCode(dto.getPharmacyCode())
                .pharmacyName(dto.getPharmacyName())
                .licenseNumber(dto.getLicenseNumber())
                .ownerName(dto.getOwnerName())
                .ownerMobile(dto.getOwnerMobile())
                .ownerEmail(dto.getOwnerEmail())
                .pharmacyTypeId(dto.getPharmacyTypeId())
                .pharmacyCategoryId(dto.getPharmacyCategoryId())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .cityId(dto.getCityId())
                .stateId(dto.getStateId())
                .countryId(dto.getCountryId())
                .pincode(dto.getPincode())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .hasOwnDelivery(dto.getHasOwnDelivery())
                .isOperationActive(dto.getIsOperationActive())
                .creditLimit(dto.getCreditLimit())
                .creditDays(dto.getCreditDays())
                .remark(dto.getRemark())
                .profileFileMetaId(dto.getProfileFileMetaId())
                .status((short) 1)
                .build();
    }

    /**
     * Apply changes from RequestDTO to existing Entity.
     * Null fields in DTO are skipped — preserves current values.
     */
    public void updateEntity(MstPharmacy entity, MstPharmacyRequestDTO dto) {
        if (dto.getPharmacyCode() != null) {
            entity.setPharmacyCode(dto.getPharmacyCode());
        }
        if (dto.getPharmacyName() != null) {
            entity.setPharmacyName(dto.getPharmacyName());
        }
        if (dto.getLicenseNumber() != null) {
            entity.setLicenseNumber(dto.getLicenseNumber());
        }
        if (dto.getOwnerName() != null) {
            entity.setOwnerName(dto.getOwnerName());
        }
        if (dto.getOwnerMobile() != null) {
            entity.setOwnerMobile(dto.getOwnerMobile());
        }
        if (dto.getOwnerEmail() != null) {
            entity.setOwnerEmail(dto.getOwnerEmail());
        }
        entity.setPharmacyTypeId(dto.getPharmacyTypeId());
        entity.setPharmacyCategoryId(dto.getPharmacyCategoryId());
        if (dto.getAddressLine1() != null) {
            entity.setAddressLine1(dto.getAddressLine1());
        }
        if (dto.getAddressLine2() != null) {
            entity.setAddressLine2(dto.getAddressLine2());
        }
        entity.setCityId(dto.getCityId());
        entity.setStateId(dto.getStateId());
        entity.setCountryId(dto.getCountryId());
        if (dto.getPincode() != null) {
            entity.setPincode(dto.getPincode());
        }
        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }
        entity.setHasOwnDelivery(dto.getHasOwnDelivery());
        entity.setIsOperationActive(dto.getIsOperationActive());
        if (dto.getCreditLimit() != null) {
            entity.setCreditLimit(dto.getCreditLimit());
        }
        entity.setCreditDays(dto.getCreditDays());
        if (dto.getRemark() != null) {
            entity.setRemark(dto.getRemark());
        }
        entity.setProfileFileMetaId(dto.getProfileFileMetaId());
    }

    /**
     * Map Projection → ResponseDTO.
     * Returns null if projection is null.
     */
    public MstPharmacyResponseDTO toResponseDTO(MstPharmacyProjection projection) {
        if (projection == null) return null;

        return MstPharmacyResponseDTO.builder()
                .pharmacyUuid(projection.getPharmacyUuid())
                .pharmacyCode(projection.getPharmacyCode())
                .pharmacyName(projection.getPharmacyName())
                .licenseNumber(projection.getLicenseNumber())
                .ownerName(projection.getOwnerName())
                .ownerMobile(projection.getOwnerMobile())
                .ownerEmail(projection.getOwnerEmail())
                .pharmacyTypeId(projection.getPharmacyTypeId())
                .pharmacyCategoryId(projection.getPharmacyCategoryId())
                .addressLine1(projection.getAddressLine1())
                .addressLine2(projection.getAddressLine2())
                .cityId(projection.getCityId())
                .stateId(projection.getStateId())
                .countryId(projection.getCountryId())
                .pincode(projection.getPincode())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .hasOwnDelivery(projection.getHasOwnDelivery())
                .isOperationActive(projection.getIsOperationActive())
                .creditLimit(projection.getCreditLimit())
                .creditDays(projection.getCreditDays())
                .remark(projection.getRemark())
                .profileFileMetaId(projection.getProfileFileMetaId())
                .status(projection.getStatus())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .createdBy(projection.getCreatedBy())
                .updatedBy(projection.getUpdatedBy())
                .auditTrackerId(projection.getAuditTrackerId())
                .build();
    }
}