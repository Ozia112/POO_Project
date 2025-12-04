# Use Cases - LockerEasy System

## UC-01: Manage Locker Rentals

**Primary Actor:** Employee / Administrator  
**Goal:** Register, monitor, and finalize locker rentals with complete customer and transaction tracking  
**Implementation Status:** **FULLY IMPLEMENTED**

### Preconditions
- System running with active database connection
- At least one locker location available in system
- User has access to Renta module

### Main Success Scenario

1. User navigates to **"Renta"** tab in main interface
2. System displays grid of all locker locations with current status (Available/Occupied)
3. User selects an available locker from the grid
4. System displays rental form and confirms locker availability
5. User enters customer information:
   - Customer name
   - Customer email
   - Rental duration (in hours)
6. System validates input data:
   - Email format validation
   - Duration must be positive number
   - Locker must still be available
7. User clicks **"Iniciar Renta"** button
8. System creates ticket with customer data and timestamp
9. System records rental with start time and associates with location
10. System marks locker as OCCUPIED
11. System displays success confirmation with rental details
12. Grid updates showing locker as occupied with rental information

### Alternative Flows

**3a. User selects occupied locker:**
- System displays current rental details (customer, start time, elapsed time)
- User can choose to finalize the rental (go to Extension Flow)

**6a. Invalid customer data:**
- System displays validation error message
- User corrects information and resubmits

**6b. Locker becomes unavailable (concurrent access):**
- System displays error: "Locker no longer available"
- User returns to step 3 to select different locker

**8a. Database error during save:**
- System rolls back transaction
- System displays error message
- No changes persist to database

### Extension: End Active Rental

1. User selects occupied locker from grid
2. System displays rental information panel with:
   - Customer name and email
   - Start time
   - Elapsed time (calculated in real-time)
   - Current cost
3. User clicks **"Finalizar Renta"** button
4. System calculates final duration and cost
5. System records end time in rental record
6. System marks locker as AVAILABLE
7. System displays completion confirmation
8. Grid updates showing locker as available

### Postconditions

**Success:**
- New rental recorded with complete data (FR1)
- Locker status updated correctly (FR2)
- Financial transaction logged in system (FR6)
- Ticket created and linked to services (FR15)

**Failure:**
- No data changed in database
- Locker status unchanged
- User receives clear error message

### Implementation Evidence

- **Controller:** `RentaController.iniciarRenta(Ubicacion, Ticket)` - Records rental with all required data
- **Controller:** `RentaController.finalizarRenta(Ubicacion, Ticket)` - Closes rental and updates status
- **Controller:** `RentaController.getRenta(Ubicacion)` - Retrieves active rental details
- **Model:** `Renta.java` - Entity with cliente, email, inicio_renta, cierre_renta, ubicacion
- **UI:** `RentaGUI.java` - Complete interface with locker grid and rental management

---

## UC-02: Manage Product Catalog and Sales

**Primary Actor:** Employee / Administrator  
**Goal:** Maintain product inventory and record customer purchases  
**Implementation Status:** **FULLY IMPLEMENTED**

### Preconditions
- User authenticated with system access
- Product database initialized with at least one category (etiqueta)

### Main Success Scenario

#### Part A: Product Management

1. User navigates to **"Config" → "Venta"** tab
2. System displays product table with all current products:
   - ID, Name, Price, Stock, Availability, Category
3. User clicks **"Nuevo"** button to add new product
4. System clears form and prepares for data entry
5. User enters product information:
   - Name (required)
   - Price (positive decimal)
   - Stock quantity (non-negative integer)
   - Category selection from dropdown
   - Availability checkbox
6. System validates all inputs
7. User clicks **"Agregar"** button
8. System saves product with assigned category
9. System updates table showing new product
10. System displays success confirmation

#### Part B: Product Sales (from Servicios tab)

1. User navigates to **"Servicios"** tab
2. User enters customer ticket information
3. User selects product category from **"Tipo de Servicio"** dropdown
4. System loads products matching selected category
5. User selects specific product and enters quantity
6. System validates:
   - Product availability
   - Sufficient stock (for consumible items)
7. User clicks **"Agregar Servicio"**
8. System records sale linked to customer ticket
9. System updates inventory (if applicable)
10. System calculates and displays updated ticket total

### Alternative Flows

**5a. Duplicate product name:**
- System allows duplicate names (business decision)
- Products distinguished by ID

**6a. Invalid price or stock:**
- System displays validation error
- User corrects and resubmits

**7a. Missing required category:**
- System prevents save with error message
- User selects category before proceeding

**8a. Database constraint violation:**
- Transaction rolls back
- System displays error
- User notified to retry

#### Sales Alternative Flows

**6a. Insufficient stock:**
- System displays error: "Stock insuficiente"
- User adjusts quantity or selects different product

**6b. Product not available:**
- Product disabled in dropdown
- User cannot select unavailable items

### Extension: Edit Existing Product

1. User clicks product row in table
2. System fills form with product data
3. User modifies desired fields
4. User clicks **"Modificar"** button
5. System validates changes
6. System updates product record
7. Table refreshes with updated data

### Extension: Delete Product

1. User clicks product row in table
2. User clicks **"Eliminar"** button
3. System displays confirmation dialog
4. User confirms deletion
5. System removes product from database
6. Table refreshes without deleted product

### Postconditions

**Success:**
- Product catalog updated correctly (FR10)
- Sales recorded with customer link (FR4)
- Inventory adjusted appropriately (FR12)
- Financial transaction logged (FR6)

### Implementation Evidence

- **Controller:** `InventarioController.agregarProducto()` - Creates products with category validation
- **Controller:** `VentaController.registrarVenta()` - Records sales linked to tickets
- **Controller:** `InventarioController.eliminarProducto()` - Removes products
- **Model:** `Venta.java` - Product entity with etiqueta relationship
- **Model:** `Etiqueta.java` - Category entity with afecta_inventario logic
- **UI:** `VentaGUI.java` - Full CRUD interface with validation
- **UI:** `ServiciosGUI.java` - Sales recording with dynamic product filtering

---

## UC-03: Generate and View Financial Reports

**Primary Actor:** Administrator / Manager  
**Goal:** Access consolidated financial data and transaction history  
**Implementation Status:** **PARTIALLY IMPLEMENTED** (No Excel export)

### Preconditions
- System contains transaction data (tickets, rentals, sales)
- User has access to reporting functionality

### Main Success Scenario

1. User accesses reporting module (currently programmatic)
2. System retrieves current active report period
3. System calculates totals:
   - Total revenue from all tickets in period
   - Number of transactions
   - Breakdown by service type (rentals vs sales)
4. System displays report data on screen
5. User reviews financial summary

### Alternative Flows

**2a. No active report period:**
- System creates new report for current date
- Initializes with zero totals

**3a. Large transaction volume:**
- System processes incrementally
- Display updates as calculations complete

**5a. User requests Excel export:** **NOT IMPLEMENTED**
- System currently cannot export to Excel
- Manual data extraction required

### Extension: Period Report Management

1. User can view historical reports by date
2. System retrieves report for specified period
3. Report shows aggregated ticket totals
4. Each ticket includes:
   - Customer information
   - Services purchased
   - Timestamp
   - Total amount

### Postconditions

**Success:**
- Report generated with accurate totals (FR7)
- Transaction history accessible (FR6)
- Data available for analysis

**Limitation:**
- No Excel export capability (FR8)

### Implementation Evidence

- **Controller:** `ReporteController.getReporteActual()` - Retrieves current report
- **Controller:** `ReporteController.recalcularTotal()` - Aggregates ticket totals
- **Model:** `Reporte.java` - Report entity with fecha_generacion and total_reportado
- **Model:** `Ticket.java` - Transaction grouping with calcularTotal()

---

## UC-04: Apply Discounts and Promotions

**Primary Actor:** Employee  
**Goal:** Apply price reductions to services during transaction creation  
**Implementation Status:** **IMPLEMENTED**

### Preconditions
- Active transaction in progress
- Discount configuration available in system

### Main Success Scenario

1. User creating service (rental or sale)
2. User identifies discount eligibility
3. User accesses discount configuration
4. System provides discount options:
   - Percentage discount (from Config.descuento_unico)
   - Service-level discount amount
5. User applies discount to service
6. System recalculates service total with discount applied
7. System records discount amount in service record
8. Discounted total reflects in ticket calculation

### Alternative Flows

**4a. Invalid discount value:**
- System validates discount within acceptable range
- Rejects negative or excessive discounts

**5a. Multiple discounts:**
- System allows one discount per service
- User chooses applicable discount type

### Postconditions

**Success:**
- Discount applied and recorded (FR3)
- Total price adjusted correctly
- Audit trail preserved

### Implementation Evidence

- **Model:** `Servicio.aplicar_descuento` - Field storing discount amount
- **Model:** `Config.descuento_unico` - System-wide discount configuration
- Business logic in service creation workflow

---

## UC-05: Monitor System Operations (Dashboard)

**Primary Actor:** Employee / Administrator  
**Goal:** View real-time business status and operational metrics  
**Implementation Status:** **PARTIALLY IMPLEMENTED**

### Preconditions
- System running with active data

### Main Success Scenario

1. User opens main application interface
2. System displays primary tabs: Servicios, Renta, Config
3. **Renta tab** provides:
   - Visual grid of all locker locations
   - Color-coded status (Available/Occupied)
   - Active rentals list with details
4. **Servicios tab** shows:
   - Product selection by category
   - Current ticket in progress
5. System updates displays as operations occur

### Limitations

- No unified dashboard with KPIs (FR9)
- No sales summary visualization
- No revenue trend charts
- No inventory alerts

### Postconditions

**Partial Success:**
- Locker status visible and accurate
- Current operations trackable
- Missing: Comprehensive metrics dashboard

### Implementation Evidence

- **UI:** `RentaGUI.java` - Locker grid with real-time status
- **UI:** `ServiciosGUI.java` - Service creation interface
- **Model:** `Ubicacion.disponible` - Availability tracking

---

## Summary: Use Case Implementation Status

| Use Case | Status | Notes |
|---|---|---|
| UC-01: Manage Locker Rentals | Complete | Full rental lifecycle implemented |
| UC-02: Product Catalog & Sales | Complete | Full CRUD with category management |
| UC-03: Financial Reports | Partial | Reports exist, Excel export missing |
| UC-04: Apply Discounts | Complete | Discount fields and logic present |
| UC-05: Dashboard | Partial | Status views exist, metrics dashboard missing |

### Critical Missing Functionality
1. **Excel export** for reports (UC-03)
2. **Comprehensive dashboard** with KPIs (UC-05)
3. **Authentication system** for sensitive operations (all use cases)
