# 🏥 Healthcare Resource & Appointment Optimization System

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-336791?logo=postgresql&logoColor=white)
![Schema](https://img.shields.io/badge/Schemas-3-blue)
![Tables](https://img.shields.io/badge/Tables-30%2B-green)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

A production-grade PostgreSQL database schema for managing multi-hospital operations — covering appointments, doctor scheduling, resource allocation, clinical records, and analytics.

---

## 📖 Table of Contents

- [Overview](#overview)
- [Schema Architecture](#schema-architecture)
- [Modules](#modules)
- [Getting Started](#getting-started)
- [Key Design Decisions](#key-design-decisions)
- [Appointment Lifecycle](#appointment-lifecycle)
- [Triggers (Opt-In)](#triggers-opt-in)
- [Default System Configuration](#default-system-configuration)
- [Roles](#roles)
- [Performance Indexes](#performance-indexes)
- [Contributing](#contributing)

---

## Overview

| Metric | Count |
|---|---|
| Tables | 30+ |
| PostgreSQL Schemas | 3 |
| Modules | 8 |
| Optimized Indexes | 20+ |
| Trigger Functions | 6+ |

This system is designed for **multi-hospital, multi-tenant** healthcare environments. It supports:

- Booking and tracking patient appointments across doctors and facilities
- Managing doctor schedules, time slots, and leave
- Allocating rooms and equipment without double-booking
- Recording clinical notes and prescription data
- Generating daily analytics on utilization and revenue
- Full audit logging and token-based authentication support

---

## Schema Architecture

The database uses **three PostgreSQL schemas** to separate concerns cleanly:

| Schema | Purpose | Example Tables |
|---|---|---|
| `master` | Reference & core entities | `mst_hospital`, `mst_doctor`, `mst_patient` |
| `transactional` | Operations & live events | `txn_appointments`, `txn_resource_allocations` |
| `system` | Auth, config & audit | `sys_audit_logs`, `sys_token_blacklist` |

### Extensions Required

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";   -- UUID generation
CREATE EXTENSION IF NOT EXISTS "btree_gist";  -- Exclusion constraints for overlap detection
```

---

## Modules

### Module 1 — Hospital & Organization

Manages hospital entities and geographic reference data.

**Tables:** `mst_hospital`, `mst_hospital_type`, `mst_country`, `mst_state`, `mst_city`

- Supports multiple hospital types (public, private, clinic, etc.)
- Stores working hours and timezone per hospital
- Geo hierarchy: country → state → city

---

### Module 2 — User, Role & Identity Management

Handles all user types across the platform.

**Tables:** `mst_role`, `mst_user`, `mst_patient`, `mst_patient_address`, `mst_patient_medical`, `mst_doctor`, `mst_doctor_qualification`, `mst_doctor_practice`, `mst_staff`, `mst_staff_assignment`

- Unified `mst_user` table with role-based identity
- Doctors can practice at multiple hospitals via `mst_doctor_practice`
- Patient medical history tracked separately for versioning
- Staff assignment includes shift times and reporting structure

---

### Module 3 — Authentication & Authorization

**Tables:** `sys_token_blacklist`, `sys_audit_logs`

- JWT token blacklisting with expiry tracking
- Full audit log with `old_values` / `new_values` JSONB diffing
- Severity levels: `INFO`, `WARNING`, `ERROR`, `CRITICAL`
- Action categories: `AUTH`, `APPOINTMENT`, `RESOURCE`, `USER`, `ADMIN`, `SYSTEM`

---

### Module 4 — Doctor Availability Management

**Tables:** `mst_doctor_schedules`, `mst_doctor_time_slots`, `mst_doctor_leaves`

- Weekly schedule templates with effective date ranges
- Time slots generated from schedule templates
- Leave management with approval workflow (`PENDING` → `APPROVED` / `REJECTED`)
- Full-day and partial-day leave support

---

### Module 5 — Resource Management

**Tables:** `mst_resources`, `mst_rooms`, `mst_equipment`, `txn_resource_allocations`

- Resource types: `ROOM`, `EQUIPMENT`, `FACILITY`
- Room metadata: bed capacity, ICU/OT/OPD type, sterilization status
- Equipment metadata: serial number, warranty, calibration schedule
- Allocation tracking with overlap prevention (exclusion constraint, opt-in)

---

### Module 6 — Appointment Management

**Tables:** `txn_appointments`, `txn_appointment_clinical_notes`, `txn_appointment_resources`, `txn_appointment_staff`, `txn_appointment_status_history`

- Appointment types: `CONSULTATION`, `FOLLOW_UP`, `EMERGENCY`, `ROUTINE_CHECKUP`, `VACCINATION`, `DIAGNOSTIC`
- Booking channels: `WALK_IN`, `PHONE`, `ONLINE`, `MOBILE_APP`, `STAFF`
- Clinical notes with vital signs (JSONB), diagnosis, prescription, and follow-up plan
- Full status change history with reason tracking
- Doctor double-booking prevented via exclusion constraint (opt-in)

---

### Module 7 — Analytics & Reporting

**Tables:** `txn_daily_analytics`, `txn_doctor_utilization`, `txn_resource_utilization`

- Pre-aggregated daily snapshots for fast dashboard queries
- Doctor utilization: slots available vs booked vs completed
- Resource utilization: available hours vs utilized vs maintenance
- Revenue tracking per doctor and per hospital

---

### Module 8 — System Configuration

**Tables:** `sys_system_config`, `sys_appointment_daily_counter`

- Per-hospital configuration key-value store
- Config types: `STRING`, `INTEGER`, `BOOLEAN`, `JSON`, `DECIMAL`, `TIME`, `DATE`
- Atomic daily counter for appointment number generation (no race conditions)

---

## Getting Started

### Prerequisites

- PostgreSQL 14 or higher
- Extensions: `uuid-ossp`, `btree_gist`

### Installation

```bash
# Clone the repository
git clone https://github.com/your-org/healthcare-db-schema.git
cd healthcare-db-schema
```

```sql
-- Connect to your PostgreSQL database
psql -U postgres -d your_database_name

-- Run the schema
\i schema.sql
```

> **Note:** All `UNIQUE` constraints are commented out by default for write performance. Enable them selectively based on your application's consistency requirements.

---

## Key Design Decisions

| Decision | Rationale |
|---|---|
| `BIGSERIAL` + `UUID` PKs | Internal joins use fast integer IDs; UUIDs are safe to expose in APIs |
| Soft deletes via `status` | `0` = inactive, `1` = active, `9` = deleted — preserves full audit trail |
| UNIQUE constraints commented out | Optimized for high-write throughput; enable per use case |
| Exclusion constraints (`btree_gist`) | Prevents double-booking of doctors and resources at the database level |
| Partial indexes with `WHERE status = 1` | Smaller index size, faster scans on active records only |
| `JSONB` for permissions & vitals | Flexible schema for variable-structure medical and role data |
| `TIMESTAMPTZ` everywhere | All timestamps are timezone-aware; hospital timezone is configurable |
| Three-schema separation | Clear boundary between reference data, live operations, and system internals |

---

## Appointment Lifecycle

```
SCHEDULED → CONFIRMED → IN_PROGRESS → COMPLETED
                ↓               ↓
           CANCELLED        NO_SHOW
                ↓
           RESCHEDULED
```

Every status transition is recorded in `txn_appointment_status_history` (via trigger, opt-in).

---

## Triggers (Opt-In)

All triggers are included in the schema but **commented out**. Enable them as needed:

| Trigger | Table | Purpose |
|---|---|---|
| `trg_update_*_timestamp` | All major tables | Auto-updates `updated_at` on every row change |
| `trg_track_appointment_status` | `txn_appointments` | Logs every status transition to history table |
| `trg_prevent_booking_on_leave` | `txn_appointments` | Blocks booking when doctor has an approved leave |
| `trg_before_insert_appointment` | `txn_appointments` | Auto-generates appointment number (e.g. `HOSP-20250418-000042`) |
| `trg_release_appointment_resources` | `txn_appointments` | Frees allocated rooms/equipment on cancellation or no-show |

---

## Default System Configuration

Seeded automatically on installation:

| Config Key | Default Value | Description |
|---|---|---|
| `default_slot_duration_minutes` | `30` | Default appointment slot length |
| `max_advance_booking_days` | `90` | How far ahead patients can book |
| `min_cancellation_hours` | `24` | Minimum notice required for cancellation |
| `max_daily_appointments_per_patient` | `3` | Per-patient daily booking cap |
| `overbooking_allowed` | `false` | Whether overbooking is permitted |
| `auto_confirm_appointments` | `true` | Auto-confirm on booking |
| `send_reminder_hours_before` | `24` | When to send appointment reminders |
| `default_working_hours_start` | `08:00:00` | Hospital opening time |
| `default_working_hours_end` | `20:00:00` | Hospital closing time |
| `allow_same_day_booking` | `true` | Permit same-day appointments |
| `require_payment_before_booking` | `false` | Gate booking behind payment |

---

## Roles

Four default roles are seeded:

| Role Code | Role Name | Description |
|---|---|---|
| `PATIENT` | Patient | Appointment booking and health record access |
| `DOCTOR` | Doctor | Schedule and patient management |
| `STAFF` | Staff | Operational support |
| `ADMIN` | Administrator | Full system access |

Role permissions are stored as `JSONB` in `mst_role.permissions` for flexible, fine-grained access control.

---

## Contributing

1. Fork this repository
2. Create a feature branch: `git checkout -b feature/your-change`
3. Commit your changes: `git commit -m "feat: describe your change"`
4. Push to the branch: `git push origin feature/your-change`
5. Open a Pull Request

---

<p align="center">Built with PostgreSQL 14+ &nbsp;·&nbsp; Schema v1.0.0</p>
