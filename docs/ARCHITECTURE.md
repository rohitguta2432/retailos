# RetailOS — Architecture Guide

## Architecture Style: Modular Monolith

RetailOS is built as a **modular monolith** — a single deployable unit with strict module boundaries that can be extracted into microservices when scale demands it.

```
┌──────────────────────────────────────────────────┐
│                 retailos-app                      │
│           (Spring Boot Entry Point)               │
├──────────────────────────────────────────────────┤
│                                                   │
│  ┌─────────┐ ┌───────┐ ┌──────────┐ ┌─────────┐│
│  │  auth   │ │tenant │ │inventory │ │ billing ││
│  └─────────┘ └───────┘ └──────────┘ └─────────┘│
│  ┌─────────┐ ┌───────┐ ┌──────────┐ ┌─────────┐│
│  │ invoice │ │ khata │ │   kyc    │ │  file   ││
│  └─────────┘ └───────┘ └──────────┘ └─────────┘│
│  ┌─────────┐ ┌───────┐ ┌──────────┐ ┌─────────┐│
│  │  sync   │ │ admin │ │analytics │ │  audit  ││
│  └─────────┘ └───────┘ └──────────┘ └─────────┘│
│  ┌──────────────┐                                │
│  │ notification │                                │
│  └──────────────┘                                │
│                                                   │
├──────────────────────────────────────────────────┤
│              retailos-common                      │
│    (Tenant Context, Security, Audit, DTOs)        │
└──────────────────────────────────────────────────┘
```

---

## Module Dependency Rules

1. **All modules depend on `retailos-common`** — shared kernel
2. **No circular dependencies** — enforce via Maven module boundaries
3. **Inter-module communication** via Spring ApplicationEvent (not direct service calls)
4. **Each module owns its domain** — entities, repositories, services, APIs

### Dependency Graph

```
common ← auth ← admin
common ← tenant ← admin
common ← inventory ← billing ← invoice
common ← khata
common ← file ← kyc
common ← sync
common ← analytics
common ← audit
common ← notification
```

---

## Multi-Tenancy Architecture

### Strategy: Row-Level Isolation with `tenant_id`

Every tenant-scoped table has a `tenant_id` column. Isolation is enforced at three levels:

| Level | Mechanism |
|-------|-----------|
| **Application** | `TenantContext` (ThreadLocal) set by `TenantInterceptor` from JWT |
| **ORM** | Hibernate `@Filter("tenantFilter")` on all tenant entities |
| **Database** | PostgreSQL RLS policies (future enhancement) |

### Request Lifecycle

```
HTTP Request → JwtAuthFilter (extract JWT)
  → TenantInterceptor (set TenantContext from claims)
    → Controller → Service → Repository (Hibernate filter active)
      → TenantInterceptor.afterCompletion (clear context)
```

---

## Package Convention

Each module follows a consistent structure:

```
retailos-{module}/
└── src/main/java/com/retailos/{module}/
    ├── api/           # REST controllers
    ├── domain/        # JPA entities
    ├── repository/    # Spring Data JPA repositories
    ├── service/       # Business logic
    ├── event/         # Domain events (publish/listen)
    └── config/        # Module-specific configuration
```

---

## Key Design Patterns

| Pattern | Where Used |
|---------|-----------|
| **BaseEntity** | All tenant-scoped entities extend it (UUID, tenant_id, timestamps, version) |
| **Spring Events** | Cross-module communication (audit, notifications, stock updates) |
| **Repository Pattern** | Spring Data JPA with custom query methods |
| **DTO Mapping** | MapStruct for entity ↔ API DTO transformations |
| **Global Error Handling** | `@RestControllerAdvice` with standardized `ApiResponse` |
| **Optimistic Locking** | `@Version` field on all mutable entities |

---

## API Design Standards

- **Base Path**: `/api/v1/{module}/...`
- **Response Envelope**: `ApiResponse<T>` with `status`, `data`, `error`, `meta`
- **Pagination**: `?page=0&size=20&sort=createdAt,desc`
- **Versioning**: Header-based (`X-API-Version`)
- **Auth**: Bearer JWT in `Authorization` header
- **Tenant**: Extracted from JWT `tenant_id` claim

---

## Security Architecture

```
Request → API Gateway (rate limit) → JwtAuthFilter (validate token)
  → TenantInterceptor (set context)
    → @PreAuthorize / hasRole() (RBAC check)
      → Business Logic
        → Audit Event Published
```

### Role Hierarchy

| Level | Roles |
|-------|-------|
| Platform | PLATFORM_ADMIN, PLATFORM_SUPPORT, PLATFORM_ANALYST |
| Tenant | OWNER, MANAGER, CASHIER, WAREHOUSE_STAFF, ACCOUNTANT, VIEWER |
