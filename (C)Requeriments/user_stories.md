# User Stories - LockerEasy System

---

## Core Business Operations

### US-01: Register Locker Rental

**As an** Employee  
**I want to** register a new locker rental with customer details and duration  
**So that** I can track which lockers are occupied and by whom  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ System displays visual grid of all locker locations  
- ✓ Available lockers are selectable, occupied lockers show current rental  
- ✓ System validates customer email format  
- ✓ System prevents renting already-occupied lockers  
- ✓ System automatically calculates rental cost based on duration  
- ✓ Once confirmed, locker marked as OCCUPIED immediately  
- ✓ Financial transaction recorded in system  
- ✓ Customer receives ticket with rental details  

#### Implementation Evidence
- `RentaController.iniciarRenta()` creates rental with validation
- `RentaGUI` provides locker grid interface
- `Ubicacion.disponible` prevents double booking

---

### US-02: End Locker Rental

**As an** Employee  
**I want to** finalize an active locker rental and free the locker  
**So that** lockers become available for new customers  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ System shows active rental details when occupied locker selected  
- ✓ System displays elapsed time and current cost  
- ✓ "Finalizar Renta" button ends the rental  
- ✓ System records exact end time  
- ✓ Locker immediately marked as AVAILABLE  
- ✓ No overlap possible - locker ready for next rental  
- ✓ Final transaction recorded in financial history  

#### Implementation Evidence
- `RentaController.finalizarRenta()` closes rental
- `Renta.calcularTiempoTrancurrido()` calculates duration
- Real-time status updates in `RentaGUI`

---

### US-03: Add New Product to Catalog

**As an** Administrator  
**I want to** add new products to the system with pricing and category  
**So that** I can sell products to customers  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ "Nuevo" button clears form for new product entry  
- ✓ Required fields: Name, Price, Stock, Category  
- ✓ System validates all inputs before saving  
- ✓ Category dropdown loads from database dynamically  
- ✓ System assigns auto-generated ID to product  
- ✓ Product appears immediately in table after save  
- ✓ Success confirmation displayed to user  

#### Implementation Evidence
- `InventarioController.agregarProducto()` creates products
- `VentaGUI` provides full CRUD interface
- Dynamic etiqueta loading from database

---

### US-04: Update Product Information

**As an** Administrator  
**I want to** modify existing product details (name, price, stock, category)  
**So that** I can keep the catalog accurate  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Clicking table row populates form with product data  
- ✓ All fields editable except ID (auto-generated)  
- ✓ Category can be changed via dropdown  
- ✓ "Modificar" button saves changes  
- ✓ System validates updated data  
- ✓ Table refreshes showing updated information  

#### Implementation Evidence
- `InventarioController.getVentaDAO().actualizar()` updates products
- Table selection listener fills form automatically
- Validation prevents invalid updates

---

### US-05: Delete Product from Catalog

**As an** Administrator  
**I want to** remove discontinued products from the system  
**So that** employees cannot sell unavailable items  
**Status:** **PARTIAL** (No admin authentication)

#### Acceptance Criteria

- ✓ Select product from table to delete  
- ✓ Confirmation dialog prevents accidental deletion  
- ✓ System removes product from database  
- ✓ Table refreshes without deleted product  
- ✗ Admin password required before deletion (NOT IMPLEMENTED - NFR3)  

#### Implementation Evidence
- `InventarioController.eliminarProducto()` removes products
- Confirmation dialog in `VentaGUI`
- **Missing:** Authentication check before deletion

---

### US-06: Record Product Sale

**As an** Employee  
**I want to** record customer purchases and link to their ticket  
**So that** I can complete transactions and update inventory  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Select product category to filter available products  
- ✓ Choose specific product and enter quantity  
- ✓ System validates stock availability (for consumible items)  
- ✓ Service-type products always available regardless of stock  
- ✓ Sale linked to customer ticket automatically  
- ✓ Inventory decremented for consumible products  
- ✓ Transaction recorded in financial system  

#### Implementation Evidence
- `VentaController.registrarVenta()` records sales
- `ServiciosGUI` provides category filtering
- `Etiqueta.afecta_inventario` determines inventory logic

---

## Financial Management

### US-07: Apply Discount to Service

**As an** Employee  
**I want to** apply discounts to rentals or product sales  
**So that** I can offer promotional pricing to customers  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Discount field available in service creation  
- ✓ System validates discount amount  
- ✓ Total price recalculated with discount applied  
- ✓ Discount amount recorded in transaction  
- ✓ Ticket shows discounted total  

#### Implementation Evidence
- `Servicio.aplicar_descuento` field stores discount
- `Config.descuento_unico` provides system discount value
- Discount applied at service level

---

### US-08: View Daily Revenue Report

**As a** Manager  
**I want to** see total income and transaction count for current period  
**So that** I can monitor business performance  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ System maintains current report period  
- ✓ Total revenue calculated from all tickets  
- ✓ Breakdown by service type available  
- ✓ Transaction count displayed  
- ✓ Historical reports accessible by date  

#### Implementation Evidence
- `ReporteController.getReporteActual()` retrieves reports
- `Reporte.recalcularTotal()` aggregates totals
- `Ticket.calcularTotal()` sums services

---

### US-09: Export Financial Data to Excel

**As an** Accountant  
**I want to** export transaction reports to Excel format  
**So that** I can perform external analysis and comply with accounting requirements  
**Status:** **NOT IMPLEMENTED**

#### Acceptance Criteria

- ✗ "Export to Excel" button available in report view  
- ✗ User can select date range for export  
- ✗ System generates .xlsx file with transaction data  
- ✗ File includes: Date, Customer, Service Type, Amount, Total  
- ✗ Large datasets split into multiple files if needed  

#### Gap Analysis
- **Missing:** Apache POI library integration
- **Missing:** Export UI and workflow
- **Impact:** Manual data extraction currently required

---

## Product and Inventory Management

### US-10: Categorize Products with Labels

**As an** Administrator  
**I want to** assign categories (etiquetas) to products  
**So that** I can organize inventory and control stock behavior  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Product form includes category dropdown  
- ✓ Categories define if product affects inventory  
- ✓ Consumible products track stock and deplete on sale  
- ✓ Service products remain available regardless of "stock" number  
- ✓ Category selection required when creating product  

#### Implementation Evidence
- `Etiqueta.afecta_inventario` flag controls behavior
- `EtiquetaController` manages categories
- `EtiquetasGUI` provides category CRUD interface

---

### US-11: Automatic Product Availability Updates

**As the** System  
**I want to** automatically update product availability based on stock  
**So that** employees cannot sell out-of-stock items  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Consumible products become unavailable when stock = 0  
- ✓ Service products always remain available  
- ✓ Availability updates immediately after sale  
- ✓ Unavailable products disabled in sales interface  

#### Implementation Evidence
- `InventarioController.actualizarDisponibilidad()` auto-updates
- `Venta.disponible` field tracks status
- Dynamic product filtering in `ServiciosGUI`

---

### US-12: Manage Product Categories

**As an** Administrator  
**I want to** create, edit, and delete product categories  
**So that** I can organize products by type and business rules  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Add new categories with name and inventory flag  
- ✓ Edit existing category properties  
- ✓ Delete categories not in use  
- ✓ System prevents deleting categories with associated products  
- ✓ Changes reflect immediately across system  

#### Implementation Evidence
- `EtiquetaController` provides full CRUD
- `EtiquetasGUI` provides management interface
- Foreign key constraints prevent orphaned products

---

## Operational Visibility

### US-13: View Real-Time Locker Status

**As an** Employee  
**I want to** see which lockers are available or occupied at a glance  
**So that** I can quickly rent available lockers to customers  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Visual grid shows all locker locations  
- ✓ Available lockers clearly marked (green/enabled state)  
- ✓ Occupied lockers show different status (with rental details)  
- ✓ Grid updates immediately when status changes  
- ✓ Can see customer name and elapsed time for occupied lockers  

#### Implementation Evidence
- `RentaGUI` displays locker grid dynamically
- `Ubicacion.disponible` tracked in real-time
- Active rentals list shows current details

---

### US-14: Monitor Active Rentals

**As an** Employee  
**I want to** see list of all current active rentals  
**So that** I can track rental duration and customer information  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ Active rentals list visible in Renta interface  
- ✓ Shows customer name, location, start time  
- ✓ Updates automatically as rentals start/end  
- ✓ Clicking rental displays full details  

#### Implementation Evidence
- `RentaController.getRenta()` retrieves active rental
- Real-time display in `RentaGUI`
- Rental details panel with elapsed time calculation

---

### US-15: Comprehensive Business Dashboard

**As a** Manager  
**I want to** see consolidated view of business metrics and KPIs  
**So that** I can make informed decisions quickly  
**Status:** **NOT IMPLEMENTED**

#### Acceptance Criteria

- ✗ Dashboard shows: total daily revenue, active rentals count, top-selling products  
- ✗ Visual charts for revenue trends  
- ✗ Locker utilization percentage  
- ✗ Low-stock product alerts  
- ✗ Real-time updates as transactions occur

#### Gap Analysis
- **Partial:** Locker status grid exists (FR9)
- **Missing:** Unified metrics dashboard
- **Missing:** Charts and visualizations
- **Impact:** Managers must navigate multiple screens for overview---

## System Quality and Maintenance

### US-16: Offline System Operation

**As the** System  
**I want to** operate fully without internet connectivity  
**So that** business operations continue during network outages  
**Status:** **IMPLEMENTED**

#### Acceptance Criteria

- ✓ All functionality works offline  
- ✓ Uses local PostgreSQL database  
- ✓ No internet required for any operation  
- ✓ Fast response times (< 1 second for most operations)  

#### Implementation Evidence
- Hibernate ORM with local database
- No external API dependencies
- All data stored and processed locally

---

### US-17: Transaction Audit Trail

**As an** Auditor  
**I want to** review complete history of all financial operations  
**So that** I can verify accounting accuracy and compliance  
**Status:** **PARTIAL**

#### Acceptance Criteria

- ✓ Every transaction logged with timestamp  
- ✓ Transaction type recorded (rental/sale)  
- ✓ Customer information preserved  
- ✓ Location tracked for rentals  
- ✗ User/employee who performed transaction NOT RECORDED (NFR4)  
- ✗ No audit log for system events (product edits, deletions)  

#### Gap Analysis
- **Missing:** Usuario field in Ticket/Servicio entities
- **Missing:** System event logging
- **Impact:** Cannot identify which employee performed operations

---

### US-18: Secure Administrative Actions

**As an** Administrator  
**I want to** require password confirmation before critical operations  
**So that** I can prevent accidental or unauthorized data changes  
**Status:** **NOT IMPLEMENTED**

#### Acceptance Criteria

- ✗ System prompts for password before deleting products  
- ✗ Price changes require admin authentication  
- ✗ Category deletion requires confirmation  
- ✗ Failed authentication attempts logged  

#### Gap Analysis
- **Missing:** User authentication system (NFR3)
- **Missing:** Role-based access control
- **Impact:** **SECURITY RISK** - anyone can perform destructive operations

---

## Summary: User Story Implementation Status

### Fully Implemented (13 stories)
US-01, US-02, US-03, US-04, US-06, US-07, US-08, US-10, US-11, US-12, US-13, US-14, US-16

### Partially Implemented (3 stories)
- **US-05:** Product deletion works but lacks authentication
- **US-17:** Timestamps recorded but no user tracking
- **US-15:** Locker status visible but no comprehensive dashboard

### Not Implemented (2 stories)
- **US-09:** Excel export functionality
- **US-18:** Administrative authentication system

---

## Critical User Stories Requiring Implementation

### Must Have (Next Sprint)
1. **US-18:** Authentication for admin actions (Security)
2. **US-09:** Excel export for accounting (Business requirement)
3. **US-17 Enhancement:** Add user tracking to transactions (Compliance)

### Should Have (Future Sprint)
4. **US-15:** Comprehensive dashboard with KPIs (User experience)
5. Enhanced input validation across all forms (Data quality)

### Nice to Have (Backlog)
6. Mobile interface for Android deployment
7. Performance optimization for large datasets
8. Advanced reporting with filtering and charts
