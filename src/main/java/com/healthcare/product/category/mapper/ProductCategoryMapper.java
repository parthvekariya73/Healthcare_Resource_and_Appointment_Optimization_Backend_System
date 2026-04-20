package com.healthcare.product.category.mapper;

import com.healthcare.product.category.dto.projection.ProductCategoryProjection;
import com.healthcare.product.category.dto.request.ProductCategoryRequestDTO;
import com.healthcare.product.category.dto.response.ProductCategoryResponseDTO;
import com.healthcare.product.category.entity.ProductCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCategoryMapper {

    public ProductCategory toEntity(ProductCategoryRequestDTO dto) {
        return ProductCategory.builder()
                .categoryCode(dto.getCategoryCode())
                .categoryName(dto.getCategoryName())
                .description(dto.getDescription())
                .sortOrder(dto.getSortOrder())
                .status((short) 1)
                .build();
    }

    public void updateEntity(ProductCategory entity, ProductCategoryRequestDTO dto) {
        if (dto.getCategoryCode() != null) {
            entity.setCategoryCode(dto.getCategoryCode());
        }
        if (dto.getCategoryName() != null) {
            entity.setCategoryName(dto.getCategoryName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public ProductCategoryResponseDTO toResponseDTO(ProductCategoryProjection projection) {
        if (projection == null) return null;

        return ProductCategoryResponseDTO.builder()
                .categoryUuid(projection.getCategoryUuid())
                .categoryCode(projection.getCategoryCode())
                .categoryName(projection.getCategoryName())
                .description(projection.getDescription())
                .sortOrder(projection.getSortOrder())
                .status(projection.getStatus())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .createdBy(projection.getCreatedBy())
                .updatedBy(projection.getUpdatedBy())
                .build();
    }

    // Helper methods for resolving IDs from UUIDs
    // Implement based on your relationship mappings
}
