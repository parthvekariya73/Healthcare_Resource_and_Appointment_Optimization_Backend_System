package com.healthcare.common.apputil.audit.repository;

import com.healthcare.common.apputil.audit.entity.AuditLogId;
import com.healthcare.common.apputil.audit.entity.SysAuditLogData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysAuditLogDataRepository extends JpaRepository<SysAuditLogData, AuditLogId> {
}

