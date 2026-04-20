package com.healthcare.product.category.service;

import com.healthcare.product.category.dto.request.ProductCategoryRequestDTO;
import com.healthcare.product.category.dto.response.ProductCategoryResponseDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProductCategoryService {

    ProductCategoryResponseDTO create(ProductCategoryRequestDTO requestDTO);

    ProductCategoryResponseDTO update(UUID uuid, ProductCategoryRequestDTO requestDTO);

    ProductCategoryResponseDTO getByUuid(UUID uuid);

    Page<ProductCategoryResponseDTO> getAll(int page, int size, String search);

    void delete(UUID uuid);

    ProductCategoryResponseDTO updateStatus(UUID uuid, String status);
}
