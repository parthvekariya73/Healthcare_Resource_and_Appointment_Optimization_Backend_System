package com.healthcare.product.category.controller;

import com.healthcare.product.category.dto.request.ProductCategoryRequestDTO;
import com.healthcare.product.category.dto.response.ProductCategoryResponseDTO;
import com.healthcare.product.category.service.ProductCategoryService;
import com.healthcare.common.apputil.response.ApiResponse;
import com.healthcare.common.apputil.utils.commonutil.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/product-category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategoryResponseDTO>> create(
            @Valid @RequestBody ProductCategoryRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        ProductCategoryResponseDTO response = service.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "ProductCategory created successfully",
                        response,
                        CommonUtil.getRequestId(httpRequest)));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductCategoryResponseDTO>> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody ProductCategoryRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        ProductCategoryResponseDTO response = service.update(uuid, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "ProductCategory updated successfully",
                        response,
                        CommonUtil.getRequestId(httpRequest)));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductCategoryResponseDTO>> getByUuid(
            @PathVariable UUID uuid,
            HttpServletRequest httpRequest) {

        ProductCategoryResponseDTO response = service.getByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "ProductCategory retrieved successfully",
                        response,
                        CommonUtil.getRequestId(httpRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductCategoryResponseDTO>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            HttpServletRequest httpRequest) {

        Page<ProductCategoryResponseDTO> response = service.getAll(page - 1, size, search);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "ProductCategorys retrieved successfully",
                        response,
                        CommonUtil.getRequestId(httpRequest)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID uuid,
            HttpServletRequest httpRequest) {

        service.delete(uuid);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "ProductCategory deleted successfully",
                        null,
                        CommonUtil.getRequestId(httpRequest)));
    }

    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<ProductCategoryResponseDTO>> updateStatus(
            @PathVariable UUID uuid,
            @RequestParam String status,
            HttpServletRequest httpRequest) {

        ProductCategoryResponseDTO response = service.updateStatus(uuid, status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "ProductCategory status updated successfully",
                        response,
                        CommonUtil.getRequestId(httpRequest)));
    }

}
