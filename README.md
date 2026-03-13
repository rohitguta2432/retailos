# RetailOS — India-first Multi-Tenant Retail SaaS Platform

> **Version**: 0.1.0-SNAPSHOT | **Java**: 21 | **Spring Boot**: 3.4.3

## 🚀 Quick Start

### Prerequisites
- Java 21+ (JDK)
- Maven 3.9+
- Docker & Docker Compose

### 1. Start Infrastructure
```bash
docker compose up -d
```
This starts:
- **PostgreSQL 16** on port `5432`
- **Redis 7** on port `6379`
- **MinIO** (S3-compatible) on port `9000` (console: `9001`)

### 2. Build & Run
```bash
mvn clean install -DskipTests
cd retailos-app
mvn spring-boot:run
```

### 3. Verify
- **Health**: http://localhost:8080/api/actuator/health
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/v3/api-docs

---

## 📦 Project Structure

```
retailos/
├── pom.xml                        # Parent POM (Maven multi-module)
├── docker-compose.yml             # Local dev infrastructure
├── docs/                          # Project documentation
│
├── retailos-common/               # 🔧 Shared kernel
│   └── tenant, security, audit, DTOs, exceptions
│
├── retailos-auth/                 # 🔐 Auth & RBAC
│   └── JWT, OTP, roles, permissions, sessions
│
├── retailos-tenant/               # 🏢 Tenant management
│   └── Tenant, Store, Warehouse, BillingCounter
│
├── retailos-kyc/                  # 📋 KYC & Onboarding
│   └── Document verification, DPDP consent
│
├── retailos-inventory/            # 📦 Inventory
│   └── Products, Categories, Stock, Movements
│
├── retailos-billing/              # 💰 Billing & POS
│   └── Bills, BillItems, Payments
│
├── retailos-invoice/              # 🧾 Invoicing
│   └── GST invoices, PDF generation
│
├── retailos-khata/                # 📒 Khata / Ledger
│   └── Credit accounts, entries, balances
│
├── retailos-file/                 # 📁 File management
│   └── MinIO integration, upload, thumbnails
│
├── retailos-sync/                 # 🔄 Offline sync
│   └── Sync queue, conflict resolution
│
├── retailos-analytics/            # 📊 Analytics
│   └── Dashboard metrics, reports
│
├── retailos-admin/                # ⚙️ Platform admin
│   └── Tenant management, impersonation
│
├── retailos-audit/                # 📝 Audit trail
│   └── Event listener, immutable log
│
├── retailos-notification/         # 🔔 Notifications
│   └── Push, SMS, email, in-app
│
└── retailos-app/                  # 🚀 Application entry point
    ├── RetailOsApplication.java
    ├── application.yml
    └── db/migration/              # Flyway scripts (V1-V5)
```

---

## 🗄️ Database

- **Engine**: PostgreSQL 16
- **Multi-tenancy**: Row-Level via `tenant_id` + Hibernate @Filter
- **Migrations**: Flyway (versioned in `retailos-app/src/main/resources/db/migration/`)
- **Schema**: 25+ tables across 5 migration scripts

| Migration | Tables Created |
|-----------|---------------|
| V1 | tenants, users, roles, user_roles, stores, warehouses, billing_counters |
| V2 | categories, products, stock, stock_movements |
| V3 | bills, bill_items, payments, invoices |
| V4 | khata_accounts, khata_entries, kyc_documents, consent_records, files, audit_log, sync_queue, impersonation_sessions, notifications |
| V5 | Seed: system roles, platform tenant, admin user |

---

## 🔐 Security

- **Auth**: Phone + OTP → JWT (access 15min / refresh 7d)
- **RBAC**: 9 system roles (3 platform + 6 tenant)
- **Tenant Isolation**: ThreadLocal + Hibernate filter on every query
- **Impersonation**: Ticket-based, fully logged, time-limited
- **Compliance**: DPDP Act consent tracking, Aadhaar tokenized

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Runtime | Java 21, Spring Boot 3.4.3 |
| Build | Maven (multi-module) |
| Database | PostgreSQL 16, Flyway |
| Cache | Redis 7 |
| Object Storage | MinIO (S3-compatible) |
| Auth | JWT (jjwt 0.12.6) |
| API Docs | SpringDoc OpenAPI 2.8 |
| Mapping | MapStruct 1.6 |
| Testing | JUnit 5, Testcontainers |

---

## 🤝 Contributing

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for architectural decisions and module contracts.
# retailos
