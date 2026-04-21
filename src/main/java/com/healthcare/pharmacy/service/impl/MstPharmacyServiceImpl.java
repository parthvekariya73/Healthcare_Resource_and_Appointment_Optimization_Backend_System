package com.healthcare.pharmacy.service.impl;

import com.healthcare.common.apputil.enums.StatusEnum;
import com.healthcare.common.apputil.exception.custom.BusinessException;
import com.healthcare.common.apputil.response.ErrorCode;
import com.healthcare.common.apputil.utils.commonutil.SecurityUtils;
import com.healthcare.pharmacy.dto.projection.MstPharmacyProjection;
import com.healthcare.pharmacy.dto.request.MstPharmacyRequestDTO;
import com.healthcare.pharmacy.dto.response.MstPharmacyResponseDTO;
import com.healthcare.pharmacy.entity.MstPharmacy;
import com.healthcare.pharmacy.mapper.MstPharmacyMapper;
import com.healthcare.pharmacy.repository.MstPharmacyRepository;
import com.healthcare.pharmacy.service.MstPharmacyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for MstPharmacy.
 * Read operations use @Transactional(readOnly=true) for performance.
 * Write operations are individually annotated @Transactional.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MstPharmacyServiceImpl implements MstPharmacyService {

    private final MstPharmacyRepository repository;
    private final MstPharmacyMapper mapper;

    @Override
    @Transactional
    public MstPharmacyResponseDTO create(MstPharmacyRequestDTO requestDTO) {
        log.info("Creating new mst_pharmacy");

        if (repository.existsByLicenseNumber(requestDTO.getLicenseNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        MstPharmacy entity = mapper.toEntity(requestDTO);
        MstPharmacy saved = repository.save(entity);

        log.info("Created mst_pharmacy with UUID: {}", saved.getPharmacyUuid());
        return getByUuid(saved.getPharmacyUuid());
    }

    @Override
    @Transactional
    public MstPharmacyResponseDTO update(UUID uuid, MstPharmacyRequestDTO requestDTO) {
        log.info("Updating mst_pharmacy with UUID: {}", uuid);

        MstPharmacy entity = findByUuid(uuid);

        if (repository.existsByLicenseNumberAndNotId(entity.getPharmacy_id(), requestDTO.getLicenseNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        mapper.updateEntity(entity, requestDTO);
        MstPharmacy saved = repository.save(entity);

        log.info("Updated mst_pharmacy with UUID: {}", uuid);
        return getByUuid(saved.getPharmacyUuid());
    }

    @Override
    public MstPharmacyResponseDTO getByUuid(UUID uuid) {
        log.debug("Fetching mst_pharmacy uuid={}", uuid);
        return repository.findProjectionByUuid(uuid)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Page<MstPharmacyResponseDTO> getAll(int page, int size, String search, String pharmacyTypeId, String pharmacyCategoryId, String cityId, String stateId, String countryId, String hasOwnDelivery, String isOperationActive) {
        log.debug("Listing mst_pharmacy page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return repository.findAllWithFilters(search, pharmacyTypeId, pharmacyCategoryId, cityId, stateId, countryId, hasOwnDelivery, isOperationActive, pageable)
                         .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        log.info("Deleting mst_pharmacy uuid={}", uuid);
        MstPharmacy entity = findByUuid(uuid);
        repository.softDeleteById(entity.getPharmacy_id(), SecurityUtils.getCurrentUserId());
        log.info("Deleted mst_pharmacy uuid={}", uuid);
    }

    @Override
    @Transactional
    public MstPharmacyResponseDTO updateStatus(UUID uuid, String status) {
        log.info("Status update: mst_pharmacy uuid={} → {}", uuid, status);
        MstPharmacy entity = findByUuid(uuid);
        entity.setStatus(StatusEnum.fromName(status).getCode());
        MstPharmacy saved = repository.save(entity);
        return getByUuid(saved.getPharmacyUuid());
    }

    @Override
    public long countActive() {
        return repository.countActive();
    }

    /** Internal helper — throws ErrorCode.RESOURCE_NOT_FOUND if not found */
    private MstPharmacy findByUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}