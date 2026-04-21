package com.healthcare.pharmacy.controller;

import com.healthcare.common.apputil.response.ApiResponse;
import com.healthcare.common.apputil.utils.commonutil.CommonUtil;
import com.healthcare.pharmacy.dto.request.MstPharmacyRequestDTO;
import com.healthcare.pharmacy.dto.response.MstPharmacyResponseDTO;
import com.healthcare.pharmacy.service.MstPharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for MstPharmacy master data management.
 * Base path: /api/v1/mst-pharmacy
 */
@Tag(name = "MstPharmacy Management")
@Slf4j
@RestController
@RequestMapping("/api/v1/mst-pharmacy")
@RequiredArgsConstructor
public class MstPharmacyController {

    private final MstPharmacyService service;

    @Operation(summary = "Create a new MstPharmacy")
    @PostMapping
    public ResponseEntity<ApiResponse<MstPharmacyResponseDTO>> create(
            @Valid @RequestBody MstPharmacyRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        MstPharmacyResponseDTO response = service.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "MstPharmacy created successfully",
                        response,
                        CommonUtil.getRequestId(httpRequest)));
    }

    @Operation(summary = "Update MstPharmacy by UUID")
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<MstPharmacyResponseDTO>> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody MstPharmacyRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        MstPharmacyResponseDTO response = service.update(uuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(
                "MstPharmacy updated successfully",
                response,
                CommonUtil.getRequestId(httpRequest)));
    }

    @Operation(summary = "Get MstPharmacy by UUID")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<MstPharmacyResponseDTO>> getByUuid(
            @PathVariable UUID uuid,
            HttpServletRequest httpRequest) {

        MstPharmacyResponseDTO response = service.getByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.success(
                "MstPharmacy retrieved successfully",
                response,
                CommonUtil.getRequestId(httpRequest)));
    }

    @Operation(summary = "List MstPharmacy with pagination, search, and filters")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MstPharmacyResponseDTO>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
        @RequestParam(required = false) String pharmacyTypeId,
        @RequestParam(required = false) String pharmacyCategoryId,
        @RequestParam(required = false) String cityId,
        @RequestParam(required = false) String stateId,
        @RequestParam(required = false) String countryId,
        @RequestParam(required = false) String hasOwnDelivery,
        @RequestParam(required = false) String isOperationActive,
            HttpServletRequest httpRequest) {

        Page<MstPharmacyResponseDTO> response = service.getAll(page - 1, size, search, pharmacyTypeId, pharmacyCategoryId, cityId, stateId, countryId, hasOwnDelivery, isOperationActive);
        return ResponseEntity.ok(ApiResponse.success(
                "MstPharmacy list retrieved successfully",
                response,
                CommonUtil.getRequestId(httpRequest)));
    }

    @Operation(summary = "Delete MstPharmacy by UUID")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID uuid,
            HttpServletRequest httpRequest) {

        service.delete(uuid);
        return ResponseEntity.ok(ApiResponse.success(
                "MstPharmacy deleted successfully",
                null,
                CommonUtil.getRequestId(httpRequest)));
    }

    @Operation(summary = "Toggle MstPharmacy active/inactive status")
    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<MstPharmacyResponseDTO>> updateStatus(
            @PathVariable UUID uuid,
            @RequestParam String status,
            HttpServletRequest httpRequest) {

        MstPharmacyResponseDTO response = service.updateStatus(uuid, status);
        return ResponseEntity.ok(ApiResponse.success(
                "MstPharmacy status updated successfully",
                response,
                CommonUtil.getRequestId(httpRequest)));
    }

    @Operation(summary = "Count all active MstPharmacy records")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countActive(HttpServletRequest httpRequest) {
        long count = service.countActive();
        return ResponseEntity.ok(ApiResponse.success(
                "MstPharmacy count retrieved",
                count,
                CommonUtil.getRequestId(httpRequest)));
    }
}