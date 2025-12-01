# Functional Requirements - LockerEasy System

## Core Functional Requirements

| ID | Requirement | Status | Implementation Evidence |
|---|---|---|---|
| **FR1** | **Register and manage locker rentals** <br> The system must register locker rentals including customer data (name, email), start time, rental duration, and assigned locker location. The system must prevent double booking and maintain rental status. | **IMPLEMENTED** | `RentaController.iniciarRenta()` records rentals with customer information, start time, and location. `Renta.java` entity contains all required fields (cliente, email, inicio_renta, ubicacion). `RentaController.estaDisponible()` prevents double booking. |
| **FR2** | **End or cancel rental and mark locker available** <br> The system must allow ending or canceling active rentals, automatically marking the locker as available for new rentals. The system must record the end time and calculate elapsed time. | **IMPLEMENTED** | `RentaController.finalizarRenta()` closes rentals, records end time, and frees the locker location. `Renta.calcularTiempoTrancurrido()` calculates rental duration. `Ubicacion.disponible` flag ensures proper availability tracking. |
| **FR3** | **Apply discounts and promotions** <br> The system must support applying discounts or promotional codes to services (rentals and product sales) to adjust final prices. | **IMPLEMENTED** | `Servicio.aplicar_descuento` field stores discount amounts per service. `Config.descuento_unico` provides system-wide discount configuration. Discounts are applied at the service level. |
| **FR4** | **Record product sales linked to customers** <br> The system must record product sales and link them to customer tickets, maintaining inventory and transaction history. | **IMPLEMENTED** | `VentaController.registrarVenta()` links sold products to a `Ticket` that contains customer data. `InventarioController` manages product CRUD operations. Sales are tracked through `Servicio` entities linked to tickets. |
| **FR5** | **Manage pricing from admin panel** <br> The system must provide an administrative interface to modify rental rates and product prices dynamically without code changes. | **PARTIAL** | Prices can be updated programmatically via `Config.setPrecioHoraLocker()` and `InventarioController.actualizarProducto()`. Backend logic exists but **no admin UI** is implemented for price management. |
| **FR6** | **Record all financial transactions** <br> The system must automatically log all financial operations (rentals, sales, payments) with complete details for audit and accounting purposes. | **IMPLEMENTED** | `Servicio` entity records all transactions with `total_servicio`, `tipo_servicio`, and associated `Ticket`. `Ticket` groups multiple services per customer. `Reporte` aggregates tickets for period summaries. |
| **FR7** | **Generate daily income and transaction history** <br> The system must generate reports showing daily or period-based income, transaction breakdowns, and business performance metrics. | **IMPLEMENTED** | `ReporteController.getReporteActual()` retrieves current period reports. `Reporte.recalcularTotal()` aggregates ticket totals. Reports include transaction history and financial summaries. |
| **FR8** | **Export reports to Excel** <br> The system must allow exporting financial reports and transaction data to Excel format (.xlsx) for external analysis and accounting compliance. | **NOT IMPLEMENTED** | No code exists for Excel export functionality. Apache POI or similar library would need to be integrated to generate .xlsx files from report data. |
| **FR9** | **Visual interface showing locker status** <br> The system must provide a real-time visual dashboard displaying locker availability status, rental activity, and sales summary for quick operational oversight. | **PARTIAL** | `Ubicacion.disponible` field tracks locker availability. `RentaGUI` displays locker grid dynamically loaded from database. However, **no comprehensive dashboard UI** exists showing combined status, metrics, and sales summaries. |

---

## Critical Gaps Requiring Attention

1. **[HIGH PRIORITY]** FR8: Export reports to Excel - Required for accounting and external analysis
2. **[MEDIUM PRIORITY]** FR5: Admin UI for price management - Currently requires code changes
3. **[MEDIUM PRIORITY]** FR9: Comprehensive dashboard with combined metrics and KPIs

