# System Use Case Specification

## Use Case 1 – Manage Locker Rentals

**Primary Actor:** Employee / Administrator  
**Goal:** Register, manage, end, or cancel locker rentals including customer data, duration, and cost.  

### Preconditions

- Active session (Employee or Administrator)
- Available lockers in the system

### Main Flow

1. User selects **"New Rental"**.  
2. System displays a form and a map of available lockers.  
3. User enters customer data, rental duration, and locker selection.  
4. System calculates the total cost.  
5. User confirms registration.  
6. System marks the locker as occupied and creates a financial transaction record.

### Alternative Flows

- **Returning Customer:** System auto-fills customer information.  
- **Invalid Data:** System displays specific validation errors.  
- **End Rental:**  
  1. User selects an active rental and chooses **"End Rental"**.  
  2. System marks the locker as available, records end time, and optionally requests customer feedback.  
- **Cancel Rental:**  
  1. User selects **"Cancel Rental"** for an active rental.  
  2. System verifies and processes the cancellation, freeing the locker.

### Postconditions

- Rental registered, ended, or canceled successfully  
- Locker status updated  
- Financial transaction logged  

---

## Use Case 2 – Manage Products and Sales

**Primary Actor:** Employee / Administrator  
**Goal:** Manage the product catalog and record product sales, optionally linked to active locker rentals.  

### Preconditions

- Authenticated session  
- Available products in inventory

### Main Flow

1. User opens **"Product Catalog"** or **"Product Sales"** section.  
2. System displays available products and their stock levels.  
3. User selects products to add, update, or remove, or records a sale.  
4. For sales, system suggests linking to an active locker rental.  
5. User confirms action.  
6. System updates the catalog, inventory, and financial transaction records.

### Alternative Flows

- **No Active Rental:** Sale is recorded independently.  
- **Low Stock:** System alerts user and restricts quantities.  
- **Invalid Product Data:** System rejects and shows validation errors.

### Postconditions

- Product catalog and inventory updated  
- Sales recorded and transactions created  

---

## Use Case 3 – Manage Prices and Discounts

**Primary Actor:** Administrator / Employee  
**Goal:** Modify product prices, rental rates, and apply discounts or promotions during transactions.  

### Preconditions

- Authenticated administrator (for price changes)  
- Ongoing rental or sale (for discounts)

### Main Flow

1. User accesses **"Manage Prices"** or **"Apply Discount"** section.  
2. System displays current prices and promotions.  
3. User modifies rates or applies a discount/promo code.  
4. System validates and recalculates totals automatically.  
5. User confirms and system updates the records.

### Alternative Flows

- **Invalid Price/Discount:** System rejects the input and provides feedback.  
- **Bulk Update:** Administrator modifies multiple prices at once.

### Postconditions

- Updated prices and discounts applied  
- Totals recalculated and confirmed  

---

## Use Case 4 – Generate and Export Reports

**Primary Actor:** Administrator / Employee  
**Goal:** Generate, view, and export daily or periodic reports of sales, rentals, and revenue.  

### Preconditions

- Active session  
- Access to reporting module  

### Main Flow

1. User selects **"Reports"** from the main menu.  
2. System displays date range and filter options.  
3. User chooses report type (e.g., rentals, sales, revenue).  
4. System generates the report and displays it on screen.  
5. User exports report to Excel (.xlsx) or PDF if desired.

### Alternative Flows

- **No Data:** System displays message “No transactions available.”  
- **Large Dataset:** System splits data into multiple files.  
- **Export Error:** System notifies user and suggests retry.

### Postconditions

- Report generated and optionally downloaded  

---

## Use Case 5 – Record Financial Transactions

**Primary Actor:** System  
**Goal:** Automatically log every financial operation for audit and traceability.  

### Preconditions

- A financial operation (rental, sale, or payment) is in process  

### Main Flow

1. System detects a transaction event.  
2. Captures details: type, amount, timestamp, user, and location.  
3. Validates data integrity.  
4. Saves the record securely in the database.  
5. Confirms successful registration.

### Alternative Flows

- **Incomplete Data:** System rejects transaction and requests correction.  
- **Storage Error:** System retries saving until successful.

### Postconditions

- Financial transaction securely recorded with unique ID  

---

## Use Case 6 – View Real-Time Dashboard

**Primary Actor:** Employee / Administrator  
**Goal:** Display live business data including locker status, sales summary, and performance metrics.  

### Preconditions

- Active user session  

### Main Flow

1. User opens **"Dashboard"**.  
2. System loads and displays:  
   - Locker map (color-coded by availability)  
   - Daily sales and rental summary  
   - Top-selling products  
   - Key performance metrics  
3. Dashboard refreshes automatically in real time.

### Alternative Flows

- **First Access:** System displays a short tutorial.  
- **Offline Mode:** System shows last synchronized data.

### Postconditions

- Dashboard information displayed and updated in real time  

---

## Use Case 7 – Offline Operation

**Primary Actor:** System  
**Goal:** Maintain full system functionality without internet connectivity.  

### Preconditions

- System installed and configured  

### Main Flow

1. System detects loss of network connection.  
2. Switches automatically to **offline mode**.  
3. Stores transactions locally.  
4. Maintains responsive interface and real-time features locally.  
5. Synchronizes all pending data when connection is restored.

### Postconditions

- Operations continue without interruption  
- Data synchronized automatically after reconnection  

---

## Use Case 8 – Administrative Authorization

**Primary Actor:** System  
**Goal:** Protect critical operations by requiring administrator confirmation.  

### Preconditions

- User attempts a critical modification or deletion  

### Main Flow

1. User initiates a sensitive action.  
2. System requests administrator credentials.  
3. Administrator enters username and password.  
4. System validates permissions.  
5. If authorized, system proceeds and logs the event.

### Alternative Flows

- **Invalid Credentials:** Operation denied and recorded.  

### Postconditions

- Authorized actions logged securely  
- Unauthorized attempts blocked  

---

## Use Case 9 – Transaction Audit Trail

**Primary Actor:** System  
**Goal:** Record a complete trace of all financial and operational activities.  

### Preconditions

- Transaction or system event occurs  

### Main Flow

1. System logs event with details: timestamp, user, type, location, and amount.  
2. Generates a unique transaction ID.  
3. Stores data securely in audit database.  

### Postconditions

- Complete and traceable transaction history available  

---

## Use Case 10 – Adaptive and Cross-Platform Interface

**Primary Actor:** System  
**Goal:** Ensure optimal user experience across devices and platforms (Windows and Android).  

### Preconditions

- System running on supported platform  

### Main Flow

1. System detects device type and screen resolution.  
2. Loads appropriate configuration and layout.  
3. Adapts UI for readability and accessibility.  
4. Ensures consistent performance and responsiveness.  

### Postconditions

- Interface adapts correctly to device  
- User experience consistent across platforms  
