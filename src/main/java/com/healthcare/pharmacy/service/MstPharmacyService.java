package com.healthcare.pharmacy.service;

import com.healthcare.pharmacy.dto.request.MstPharmacyRequestDTO;
import com.healthcare.pharmacy.dto.response.MstPharmacyResponseDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

/**
 * Service contract for MstPharmacy operations.
 */
public interface MstPharmacyService {

    /** Create a new MstPharmacy record */
    MstPharmacyResponseDTO create(MstPharmacyRequestDTO requestDTO);

    /** Update an existing MstPharmacy by UUID */
    MstPharmacyResponseDTO update(UUID uuid, MstPharmacyRequestDTO requestDTO);

    /** Fetch a single MstPharmacy by UUID */
    MstPharmacyResponseDTO getByUuid(UUID uuid);

    /** Paginated list with optional search and filters */
    Page<MstPharmacyResponseDTO> getAll(int page, int size, String search, String pharmacyTypeId, String pharmacyCategoryId, String cityId, String stateId, String countryId, String hasOwnDelivery, String isOperationActive);

    /** Soft-delete a MstPharmacy by UUID */
    void delete(UUID uuid);

    /** Toggle active/inactive status */
    MstPharmacyResponseDTO updateStatus(UUID uuid, String status);

    /** Count all non-deleted MstPharmacy records */
    long countActive();

}