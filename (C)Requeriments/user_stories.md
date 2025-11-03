# Unified User Stories

---

## US-01 – Register Locker Rental

**As an** Employee or Administrator  
**I want to** register a new locker rental with customer details, start time, duration, and cost  
**So that** I can maintain control of which lockers are occupied and by whom  

### Acceptance Criteria

- The system allows registering a new locker rental with customer details, start time, and duration.  
- The system automatically calculates the rental cost.  
- Once confirmed, the locker is marked as **occupied**.  
- A financial transaction record is created.  

---

## US-02 – End or Cancel Locker Rental

**As an** Employee or Administrator  
**I want to** end or cancel an active locker rental and mark the locker as available  
**So that** I can keep the locker inventory accurate and ready for new customers  

### Acceptance Criteria

- The system allows ending a rental, marking the locker as **available**.  
- The system records the end or cancellation time.  
- If canceled early, the system updates availability immediately.  
- The locker becomes available for a new rental without overlap.  

---

## US-03 – Extend Rental Duration

**As an** Employee  
**I want to** extend a locker rental by up to 15 minutes without extra charge  
**So that** I can give customers more flexibility when needed  

### Acceptance Criteria

- The system allows extending the rental by a maximum of **15 minutes**.  
- No extra cost is applied within that limit.  
- The system records the extension in the rental history.  

---

## US-04 – Apply Discounts and Promotions

**As an** Employee or Administrator  
**I want to** apply discounts or promotional codes to rentals or product sales  
**So that** I can offer special prices or loyalty benefits to customers  

### Acceptance Criteria

- The system displays available discounts and promotions.  
- The user can manually input a discount or promo code.  
- The system validates and recalculates totals automatically.  
- Invalid discounts are rejected with a clear error message.  

---

## US-05 – Record Product Sales

**As an** Employee or Administrator  
**I want to** record product sales and link them to customers with active locker rentals  
**So that** I can keep inventory updated and understand customer buying habits  

### Acceptance Criteria

- The system allows selecting products and quantities from inventory.  
- The sale can be linked to a customer’s active locker rental.  
- The inventory updates automatically after the sale.  
- A financial transaction is logged for each sale.  

---

## US-06 – Manage Product Catalog

**As an** Administrator  
**I want to** add, edit, or remove products in the catalog  
**So that** I can keep the product list accurate and up to date  

### Acceptance Criteria

- The system allows adding new products with name, price, and stock.  
- The system allows editing existing products.  
- The system allows removing discontinued products.  
- Changes are reflected immediately across the system.  

---

## US-07 – Manage Prices and Rental Rates

**As an** Administrator  
**I want to** manage rental rates and product prices  
**So that** I can adjust prices according to business conditions  

### Acceptance Criteria

- Prices and rates can be modified through the administration panel.  
- All changes are validated before saving.  
- Updates take effect immediately across the system.  
- Invalid or duplicate values trigger an error message.  

---

## US-08 – Record All Financial Transactions

**As the** System  
**I want to** automatically register all financial operations (rentals, sales, payments)  
**So that** I can maintain a complete and traceable financial history  

### Acceptance Criteria

- Every operation generates a transaction record with type, date/time, amount, and user.  
- Transactions are stored securely in the database.  
- Each transaction receives a unique identifier.  
- The system validates data integrity before saving.  

---

## US-09 – Generate and View Reports

**As a** Manager or Administrator  
**I want to** generate reports with daily income, transactions, and activity summaries  
**So that** I can analyze performance and make business decisions  

### Acceptance Criteria

- The user can select report type (rentals, sales, revenue, combined).  
- Reports display total income and transaction breakdowns.  
- Reports can be viewed on screen and exported to Excel or PDF.  
- If no data exists, the system displays a “No transactions available” message.  

---

## US-10 – Export Financial Data

**As a** Accountant or Administrator  
**I want to** export financial data and reports to Excel  
**So that** I can perform detailed analysis and comply with reporting obligations  

### Acceptance Criteria

- Reports can be exported to `.xlsx` format.  
- The user can define date ranges and report types.  
- The system downloads the generated file automatically.  
- For large data volumes, the system divides the export into multiple files.  

---

## US-11 – Visual Dashboard and Locker Status

**As an** Employee or Administrator  
**I want to** have a visual interface showing locker status and sales summaries  
**So that** I can quickly understand the business situation in real time  

### Acceptance Criteria

- The system displays a color-coded locker map (available, occupied, maintenance).  
- The dashboard shows daily sales summaries and best-selling products.  
- Metrics update automatically in real time.  
- The interface remains accessible and clear on all devices.  

---

## US-12 – Data Traceability and Security

**As the** System  
**I want to** maintain an audit trail of all user and financial actions  
**So that** every transaction and change is fully traceable for accountability  

### Acceptance Criteria

- The system logs all actions with timestamp, user, and operation type.  
- Critical actions (delete, edit prices) require admin credentials.  
- All logs are stored securely and can be reviewed by authorized users.  

---

## US-13 – Offline Operation

**As the** System  
**I want to** continue operating when there is no internet connection  
**So that** users can keep registering rentals and sales without interruption  

### Acceptance Criteria

- The system detects loss of connectivity automatically.  
- Transactions are stored locally while offline.  
- Once reconnected, pending data synchronizes automatically.  
- The interface remains responsive in offline mode.  

---

## US-14 – Adaptive Cross-Platform Interface

**As the** System  
**I want to** adapt the interface for different devices and platforms (Windows, Android)  
**So that** the user experience remains consistent and accessible  

### Acceptance Criteria

- The system detects device resolution and operating system.  
- Layout and controls adjust automatically for desktop, tablet, or mobile.  
- The interface remains fully functional and visually consistent.  
- Performance standards are maintained across platforms.  
