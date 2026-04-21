package com.healthcare.pharmacy.repository;

import com.healthcare.pharmacy.dto.projection.MstPharmacyProjection;
import com.healthcare.pharmacy.entity.MstPharmacy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for MstPharmacy.
 * All paginated queries use native SQL for optimal DB performance.
 */
@Repository
public interface MstPharmacyRepository extends JpaRepository<MstPharmacy, Long> {

    // ── Standard Finders ─────────────────────────────────────

    /** Find active entity by UUID */
    @Query("SELECT e FROM MstPharmacy e WHERE e.pharmacyUuid = :uuid AND e.status != 9")
    Optional<MstPharmacy> findByUuid(@Param("uuid") UUID uuid);

    /** Fetch only the PK for a given UUID — avoids full entity load */
    @Query("SELECT e.pharmacy_id FROM MstPharmacy e WHERE e.pharmacyUuid = :uuid AND e.status != 9")
    Optional<Long> findIdByUuid(@Param("uuid") UUID uuid);

    // ── Unique Constraint Checks ─────────────────────────────

    /** Check for duplicate uq_pharmacy_license on create */
    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM MstPharmacy e
            WHERE LOWER(e.licenseNumber) = LOWER(:licenseNumber) AND e.status != 9""")
    boolean existsByLicenseNumber(@Param("licenseNumber") String licenseNumber);

    /** Check for duplicate uq_pharmacy_license on update (exclude current record) */
    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM MstPharmacy e
            WHERE LOWER(e.licenseNumber) = LOWER(:licenseNumber) AND e.status != 9
              AND e.pharmacy_id != :excludeId""")
    boolean existsByLicenseNumberAndNotId(@Param("excludeId") Long excludeId, @Param("licenseNumber") String licenseNumber);

    // ── Soft Delete ───────────────────────────────────────────

    @Modifying
    @Query("""
            UPDATE MstPharmacy e
            SET e.status = 9, e.deletedAt = CURRENT_TIMESTAMP, e.deletedBy = :deletedBy
            WHERE e.pharmacy_id = :id""")
    void softDeleteById(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    // ── Paginated Listing with Search + Filter ───────────────

    @Query(value = """
        SELECT
            e.pharmacy_id as pharmacy_id,
            e.pharmacyUuid as pharmacyUuid,
            e.pharmacy_code as pharmacyCode,
            e.pharmacy_name as pharmacyName,
            e.license_number as licenseNumber,
            e.owner_name as ownerName,
            e.owner_mobile as ownerMobile,
            e.owner_email as ownerEmail,
            e.pharmacy_type_id as pharmacyTypeId,
            e.pharmacy_category_id as pharmacyCategoryId,
            e.address_line1 as addressLine1,
            e.address_line2 as addressLine2,
            e.city_id as cityId,
            e.state_id as stateId,
            e.country_id as countryId,
            e.pincode as pincode,
            e.latitude as latitude,
            e.longitude as longitude,
            e.has_own_delivery as hasOwnDelivery,
            e.is_operation_active as isOperationActive,
            e.credit_limit as creditLimit,
            e.credit_days as creditDays,
            e.remark as remark,
            e.profile_file_meta_id as profileFileMetaId,
CASE
    WHEN e.status = 1 THEN 'active'
    WHEN e.status = 0 THEN 'inactive'
    WHEN e.status = 9 THEN 'deleted'
    ELSE 'unknown'
END as status,
            e.created_at as createdAt,
            e.updated_at as updatedAt,
            mu1.full_name as createdBy,
            mu2.full_name as updatedBy
        FROM pharmacy.mst_pharmacy e
        LEFT JOIN master.mst_users mu1 ON e.created_by = mu1.user_id
        LEFT JOIN master.mst_users mu2 ON e.updated_by = mu2.user_id
        WHERE e.status != 9

          AND (CAST(:search AS text) IS NULL OR 
  LOWER(e.pharmacy_code) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
            OR   LOWER(e.pharmacy_name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
            OR   LOWER(e.owner_name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))

          AND (CAST(:pharmacyTypeId AS text) IS NULL OR e.pharmacy_type_id = CAST(:pharmacyTypeId AS text))
          AND (CAST(:pharmacyCategoryId AS text) IS NULL OR e.pharmacy_category_id = CAST(:pharmacyCategoryId AS text))
          AND (CAST(:cityId AS text) IS NULL OR e.city_id = CAST(:cityId AS text))
          AND (CAST(:stateId AS text) IS NULL OR e.state_id = CAST(:stateId AS text))
          AND (CAST(:countryId AS text) IS NULL OR e.country_id = CAST(:countryId AS text))
          AND (CAST(:hasOwnDelivery AS text) IS NULL OR e.has_own_delivery = CAST(:hasOwnDelivery AS text))
          AND (CAST(:isOperationActive AS text) IS NULL OR e.is_operation_active = CAST(:isOperationActive AS text))
        ORDER BY e.createdAt DESC
            """,
            countQuery = """
        SELECT COUNT(1)
        FROM pharmacy.mst_pharmacy e
        WHERE e.status != 9
            """,
            nativeQuery = true)
    Page<MstPharmacyProjection> findAllWithFilters(
            @Param("search") String search,
        @Param("pharmacyTypeId") String pharmacyTypeId,
        @Param("pharmacyCategoryId") String pharmacyCategoryId,
        @Param("cityId") String cityId,
        @Param("stateId") String stateId,
        @Param("countryId") String countryId,
        @Param("hasOwnDelivery") String hasOwnDelivery,
        @Param("isOperationActive") String isOperationActive,
            Pageable pageable);

    // ── Single Record by UUID ─────────────────────────────────

    @Query(value = """
        SELECT
            e.pharmacy_id as pharmacy_id,
            e.pharmacyUuid as pharmacyUuid,
            e.pharmacy_code as pharmacyCode,
            e.pharmacy_name as pharmacyName,
            e.license_number as licenseNumber,
            e.owner_name as ownerName,
            e.owner_mobile as ownerMobile,
            e.owner_email as ownerEmail,
            e.pharmacy_type_id as pharmacyTypeId,
            e.pharmacy_category_id as pharmacyCategoryId,
            e.address_line1 as addressLine1,
            e.address_line2 as addressLine2,
            e.city_id as cityId,
            e.state_id as stateId,
            e.country_id as countryId,
            e.pincode as pincode,
            e.latitude as latitude,
            e.longitude as longitude,
            e.has_own_delivery as hasOwnDelivery,
            e.is_operation_active as isOperationActive,
            e.credit_limit as creditLimit,
            e.credit_days as creditDays,
            e.remark as remark,
            e.profile_file_meta_id as profileFileMetaId,
CASE
    WHEN e.status = 1 THEN 'active'
    WHEN e.status = 0 THEN 'inactive'
    WHEN e.status = 9 THEN 'deleted'
    ELSE 'unknown'
END as status,
            e.created_at as createdAt,
            e.updated_at as updatedAt,
            mu1.full_name as createdBy,
            mu2.full_name as updatedBy
        FROM pharmacy.mst_pharmacy e
        LEFT JOIN master.mst_users mu1 ON e.created_by = mu1.user_id
        LEFT JOIN master.mst_users mu2 ON e.updated_by = mu2.user_id
        WHERE e.pharmacyUuid = CAST(:uuid AS uuid) AND e.status != 9
            """, nativeQuery = true)
    Optional<MstPharmacyProjection> findProjectionByUuid(@Param("uuid") UUID uuid);

    // ── Count Active Records ─────────────────────────────────

    @Query(value = "SELECT COUNT(1) FROM pharmacy.mst_pharmacy WHERE status != 9", nativeQuery = true)
    long countActive();
}