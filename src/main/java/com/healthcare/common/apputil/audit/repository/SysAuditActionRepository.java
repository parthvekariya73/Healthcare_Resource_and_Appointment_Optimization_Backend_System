package com.healthcare.common.apputil.audit.repository;

import com.healthcare.common.apputil.audit.entity.SysAuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysAuditActionRepository extends JpaRepository<SysAuditAction, Short> {
}

