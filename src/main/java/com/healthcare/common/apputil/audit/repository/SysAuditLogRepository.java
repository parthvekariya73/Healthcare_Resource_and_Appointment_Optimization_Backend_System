package com.healthcare.common.apputil.audit.repository;

import com.healthcare.common.apputil.audit.entity.AuditLogId;
import com.healthcare.common.apputil.audit.entity.SysAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysAuditLogRepository
        extends JpaRepository<SysAuditLog, AuditLogId>, SysAuditLogCustomRepository {
}