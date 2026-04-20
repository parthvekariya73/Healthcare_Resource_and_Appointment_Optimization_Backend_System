package com.healthcare.product.category.service.impl;

import com.healthcare.product.category.dto.projection.ProductCategoryProjection;
import com.healthcare.product.category.dto.request.ProductCategoryRequestDTO;
import com.healthcare.product.category.dto.response.ProductCategoryResponseDTO;
import com.healthcare.product.category.entity.ProductCategory;
import com.healthcare.product.category.mapper.ProductCategoryMapper;
import com.healthcare.product.category.repository.ProductCategoryRepository;
import com.healthcare.product.category.service.ProductCategoryService;
import com.healthcare.common.apputil.exception.custom.BusinessException;
import com.healthcare.common.apputil.response.ErrorCode;
import com.healthcare.common.apputil.enums.StatusEnum;
import com.healthcare.common.apputil.utils.commonutil.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository repository;
    private final ProductCategoryMapper mapper;

    @Override
    @Transactional
    public ProductCategoryResponseDTO create(ProductCategoryRequestDTO requestDTO) {
        log.info("Creating new product_category");

        if (repository.existsByCategoryCode(requestDTO.getCategoryCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        if (repository.existsByCategoryName(requestDTO.getCategoryName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        ProductCategory entity = mapper.toEntity(requestDTO);
        ProductCategory saved = repository.save(entity);

        log.info("Created product_category with UUID: {}", saved.getCategoryUuid());
        return getByUuid(saved.getCategoryUuid());
    }

    @Override
    @Transactional
    public ProductCategoryResponseDTO update(UUID uuid, ProductCategoryRequestDTO requestDTO) {
        log.info("Updating product_category with UUID: {}", uuid);

        ProductCategory entity = findByUuid(uuid);

        if (repository.existsByCategoryCodeAndNotId(entity.getCategoryId(), requestDTO.getCategoryCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        if (repository.existsByCategoryNameAndNotId(entity.getCategoryId(), requestDTO.getCategoryName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        mapper.updateEntity(entity, requestDTO);
        ProductCategory saved = repository.save(entity);

        log.info("Updated product_category with UUID: {}", uuid);
        return getByUuid(saved.getCategoryUuid());
    }

    @Override
    public ProductCategoryResponseDTO getByUuid(UUID uuid) {
        log.info("Fetching product_category with UUID: {}", uuid);
        return repository.findProjectionByUuid(uuid)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Page<ProductCategoryResponseDTO> getAll(int page, int size, String search) {
        log.info("Fetching product_categorys with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductCategoryProjection> projectionPage = repository.findAllWithFilters(search, pageable);
        return projectionPage.map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        log.info("Deleting product_category with UUID: {}", uuid);

        ProductCategory entity = findByUuid(uuid);
        repository.softDeleteById(entity.getCategoryId(), SecurityUtils.getCurrentUserId());
        log.info("Deleted product_category with UUID: {}", uuid);
    }

    @Override
    @Transactional
    public ProductCategoryResponseDTO updateStatus(UUID uuid, String status) {
        log.info("Updating status for product_category with UUID: {} to: {}", uuid, status);

        ProductCategory entity = findByUuid(uuid);
        Short statusCode = StatusEnum.fromName(status).getCode();
        entity.setStatus(statusCode);
        ProductCategory saved = repository.save(entity);
        return getByUuid(saved.getCategoryUuid());
    }

    private ProductCategory findByUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
