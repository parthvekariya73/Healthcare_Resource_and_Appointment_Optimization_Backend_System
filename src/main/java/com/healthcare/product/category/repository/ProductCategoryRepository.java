package com.healthcare.product.category.repository;

import com.healthcare.product.category.entity.ProductCategory;
import com.healthcare.product.category.dto.projection.ProductCategoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("SELECT e FROM ProductCategory e WHERE e.categoryUuid = :uuid AND e.status != 9")
    Optional<ProductCategory> findByUuid(@Param("uuid") UUID uuid);

    @Query("SELECT e.categoryId FROM ProductCategory e WHERE e.categoryUuid = :uuid AND e.status != 9")
    Optional<Long> findIdByUuid(@Param("uuid") UUID uuid);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ProductCategory e WHERE LOWER(e.categoryCode) = LOWER(:categoryCode) AND e.status != 9")
    boolean existsByCategoryCode(@Param("categoryCode") String categoryCode);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ProductCategory e WHERE LOWER(e.categoryCode) = LOWER(:categoryCode) AND e.status != 9 AND e.categoryId != :excludeId")
    boolean existsByCategoryCodeAndNotId(
            @Param("excludeId") Long excludeId,
            @Param("categoryCode") String categoryCode
    );

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ProductCategory e WHERE LOWER(e.categoryName) = LOWER(:categoryName) AND e.status != 9")
    boolean existsByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ProductCategory e WHERE LOWER(e.categoryName) = LOWER(:categoryName) AND e.status != 9 AND e.categoryId != :excludeId")
    boolean existsByCategoryNameAndNotId(
            @Param("excludeId") Long excludeId,
            @Param("categoryName") String categoryName
    );

    @Modifying
    @Query("UPDATE ProductCategory e SET e.status = 9, e.deletedAt = CURRENT_TIMESTAMP, e.deletedBy = :deletedBy WHERE e.categoryId = :id")
    void softDeleteById(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    @Query(value = """
        SELECT 
            e.categoryId as id,
            e.categoryUuid as uuid,
            e.category_code as categoryCode,
            e.category_name as categoryName,
            e.description as description,
            e.sort_order as sortOrder,
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
        FROM master.mst_product_category e
        LEFT JOIN master.mst_users mu1 ON e.created_by = mu1.user_id
        LEFT JOIN master.mst_users mu2 ON e.updated_by = mu2.user_id
        WHERE e.status != 9
          AND (CAST(:search AS text) IS NULL OR 
               LOWER(e.category_code) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
               OR 
               LOWER(e.category_name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
               OR 
               LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
              )
        ORDER BY e.created_at DESC
        """,
        countQuery = """
        SELECT COUNT(1)
        FROM master.mst_product_category e
        WHERE e.status != 9
        """,
        nativeQuery = true)
    Page<ProductCategoryProjection> findAllWithFilters(
            @Param("search") String search,
            Pageable pageable);

    @Query(value = """
        SELECT 
            e.categoryId as id,
            e.categoryUuid as uuid,
            e.category_code as categoryCode,
            e.category_name as categoryName,
            e.description as description,
            e.sort_order as sortOrder,
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
        FROM master.mst_product_category e
        LEFT JOIN master.mst_users mu1 ON e.created_by = mu1.user_id
        LEFT JOIN master.mst_users mu2 ON e.updated_by = mu2.user_id
        WHERE e.categoryUuid = CAST(:uuid AS uuid) AND e.status != 9
        """, nativeQuery = true)
    Optional<ProductCategoryProjection> findProjectionByUuid(@Param("uuid") UUID uuid);

}
