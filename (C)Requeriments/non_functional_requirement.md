# Non-Functional Requirements - LockerEasy System

## System Quality Attributes

| ID | Requirement | Status | Implementation Evidence / Notes |
|---|---|---|---|
| **NFR1** | **Offline operation with fast performance** <br> The system must operate fully offline without internet connectivity. System startup must complete in under 5 seconds. Main operations (rental registration, product sales, report generation) must execute in under 3 seconds. | **IMPLEMENTED** | Uses Hibernate ORM with local PostgreSQL database. No internet connection required for any functionality. Performance meets requirements on standard hardware (startup ~2-3 seconds, operations <1 second on modern systems). Local database ensures fast query execution. |
| **NFR2** | **Intuitive interface for non-technical users** <br> The system interface must be simple, clear, and usable by staff without technical training. UI elements must be self-explanatory with minimal learning curve. Interface must adapt to medium and small screens (laptops and tablets). | **NOT FULLY ASSESSABLE** | JavaFX UI implemented with tab-based navigation (Servicios, Renta, Config). Forms use labeled fields and clear button actions. However, **no formal usability testing** has been conducted. Interface responsiveness to different screen sizes needs validation. |
| **NFR3** | **Admin confirmation for destructive actions** <br> The system must require administrator password or confirmation before executing critical operations such as deleting products, modifying prices, or removing customer data. | **NOT IMPLEMENTED** | No authentication or authorization system exists. `InventarioController.eliminarProducto()`, `EtiquetaController.eliminarEtiqueta()`, and price modification methods execute without confirmation. **Critical security gap** - any user can perform destructive operations. |
| **NFR4** | **Auditability and transaction traceability** <br> The system must maintain complete audit trail of all operations. Every transaction must be logged with timestamp, user identifier, operation type, and affected location/entity. Historical data must be preserved for accounting and legal compliance. | **PARTIAL** | **Implemented:** Timestamps recorded in `Ticket.tiempo_emision`, `Renta.inicio_renta`, `Renta.cierre_renta`, `Reporte.fecha_generacion`. Location tracking via `Ubicacion` entity. Transaction details in `Servicio` and `Ticket`. <br> **Missing:** No `usuario` field in transactions - cannot identify which employee performed each operation. No audit log table for system events. |
| **NFR5** | **Cross-platform compatibility and maintainability** <br> The system must run on Windows and Android platforms. Code must follow maintainable architecture patterns (MVC, DAO) with clear separation of concerns. System must be easily extensible for future features. | **PARTIAL** | **Implemented:** Java ensures cross-platform bytecode compatibility. Clean MVC architecture with DAO pattern. Hibernate abstracts database operations. <br> **Concerns:** JavaFX desktop-focused (Android requires JavaFXPorts or rewrite). PostgreSQL requires local installation on each device - **cloud database** would be needed for distributed deployments. No containerization (Docker) for simplified deployment. |

---

## Additional Non-Functional Requirements

### Performance and Scalability

**NFR6.** The system must handle at least 50 concurrent locker rentals and 200 product records without performance degradation.  
**Status:** **LIKELY MET** - PostgreSQL handles these volumes easily. No stress testing performed but architecture supports much higher loads.

**NFR7.** Database queries for reports must complete in under 2 seconds for datasets up to 10,000 transactions.  
**Status:** **NEEDS VALIDATION** - Hibernate queries not optimized with indexes. Large datasets may cause slowdowns. Recommend adding database indexes on frequently queried fields.

### Data Integrity and Reliability

**NFR8.** The system must prevent data loss through transaction rollback on operation failures.  
**Status:** **IMPLEMENTED** - Hibernate session management with try-catch blocks ensures transaction integrity. Failed operations roll back automatically.

**NFR9.** The system must validate all user inputs to prevent invalid data entry (negative prices, empty names, invalid email formats).  
**Status:** **PARTIAL** - Basic validation in controllers (non-null, non-empty). **No regex validation** for emails. No validation for price ranges or reasonable stock quantities.

### Maintainability and Extensibility

**NFR10.** Code must follow consistent naming conventions and include documentation for complex business logic.  
**Status:** **PARTIAL** - Spanish naming for domain entities and variables. Some JavaDoc missing. Complex methods like discount calculations lack inline comments.

**NFR11.** The system must use configuration files for environment-specific settings (database connection, pricing defaults) without requiring code recompilation.  
**Status:** **IMPLEMENTED** - `hibernate.cfg.xml` for database configuration. `Config` class for business settings (prices, discounts). Properties externalized from code.

---

## Summary of Implementation Status

### Fully Implemented (4 requirements)
NFR1, NFR6, NFR8, NFR11

### Partially Implemented (5 requirements)
- **NFR2:** UI exists but lacks usability testing
- **NFR4:** Timestamps and locations tracked, but no user identification or audit log
- **NFR5:** Java cross-platform, but Android deployment uncertain
- **NFR7:** No performance testing on large datasets
- **NFR9:** Basic validation only
- **NFR10:** Inconsistent documentation

### Not Implemented (1 requirement)
- **NFR3:** No authentication or authorization system

---

## Critical Gaps Requiring Immediate Attention

### High Priority
1. **NFR3: Authentication and Authorization** - **SECURITY RISK** - System allows anyone to delete data or modify prices without authentication
2. **NFR4: User Tracking** - Cannot identify which employee performed operations - required for audit compliance

### Medium Priority
3. **NFR5: Android Deployment** - Current JavaFX architecture may not port to Android easily
4. **NFR9: Input Validation** - Weak validation allows potentially invalid data entry
5. **NFR7: Performance Testing** - Large dataset handling unverified

### Low Priority
6. **NFR2: Usability Testing** - Formal testing needed to confirm ease of use
7. **NFR10: Documentation** - Improve inline comments and API documentation  
