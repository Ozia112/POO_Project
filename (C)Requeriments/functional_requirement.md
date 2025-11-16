# System Requirements: Locker Rental and Product Sales Manager

## Functional Requirements (FR)

**FR1.** Register and manage locker rentals including customer data, start time, duration, and status  
**FR2.** End or cancel (feedback request) a rental and mark the locker as available (prevent double booking)  
**FR3.** Apply discounts or promotions to services  
**FR4.** Saves record product sales and link them to customers with active rentals  
**FR5.** Manage pricing: edit rental rates and product prices from the admin panel  
**FR6.** Register all financial transactions (rentals, sales, payments)  
**FR7.** Generate and view daily income and transaction history  
**FR8.** Export reports or accounting data to Excel format  
**FR9.** Provide a visual interface showing locker status and sales summary

# 2.2 Module: Reports

The Reports module provides consolidated and actionable information about all operations
performed within the LockerEasy system. This module generates unified, statistical, and exportable
reports.

---

## FR-R1. General Ticket Report
The system must generate a report containing all registered tickets, including:
- Customer name
- Customer email
- Associated services (sales and/or rentals)
- Ticket totals
- Creation date

This report provides a complete overview of customer activity and acquired services.

---

## FR-R2. Sales Report
The system must generate a report displaying all completed sales, showing:
- Product sold
- Quantity
- Unit price
- Sale date
- Total amount per transaction

The report must allow filtering sales by specific date ranges.

---

## FR-R3. Rental Report (Active and Completed)
The system must display all rentals categorized into:
- **Active rentals**
- **Completed rentals**

Each rental entry must include:
- Rental ID
- Locker assigned
- Start and end dates
- Price
- Quantity rented
- Current status

---

## FR-R4. Locker Status Report
The system must generate a real-time report showing the current status of every locker:
- Locker ID
- Location (upper or lower floor)
- Status (occupied or available)

This report assists administrators in monitoring locker availability.

---

## FR-R5. Product Inventory Report
The system must generate a sales-based inventory report, showing:
- Product name
- Total quantity sold
- Stock/availability (if applicable)
- Ranking of most and least sold products

This report helps analyze product rotation and demand.

---

## FR-R6. Locker Usage Report
The system must provide statistical data about locker usage, including:
- Number of times each locker has been rented
- Usage percentage
- Lockers with highest demand
- Lockers with lowest demand

This report supports operational and maintenance decision-making.

---

## FR-R7. Report Exporting
The system must allow exporting any generated report to:
- **Excel (.xlsx)**

The exported file must preserve all data and match the structure of the on-screen report.

