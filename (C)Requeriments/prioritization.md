# Classification

High: Essential requirement  
Medium: Desirable requirement  
Low: Luxury requirement (it’s there, but doesn’t really affect execution)

---

## Functional Requirements

[High] FR1. Register and manage locker rentals including customer data, start time, duration, and status  
[High] FR2. End or cancel (feedback request) a rental and mark the locker as available (prevent double booking)  
[High] FR4. Save product sales records and link them to customers with active rentals  
[High] FR5. Manage pricing: edit rental rates and product prices from the admin panel  
[High] FR6. Register all financial transactions (rentals, sales, payments)  
[High] FR7. Generate and view daily income and transaction history  

[Medium] FR3. Apply discounts or promotions to services  
[Medium] FR8. Export reports or accounting data to Excel format  
[Medium] FR9. Provide a visual interface showing locker status and sales summary  

---

## Non-Functional Requirements

[High] NFR1. The system must operate offline, start in less than 5 seconds, and perform main actions in under 3 seconds  
[High] NFR3. The system must require admin confirmation or password before editing or deleting data  
[High] NFR4. The system must maintain accounting traceability by logging each transaction with timestamp, user, and location  

[Medium] NFR2. The interface must be intuitive, suitable for non-technical users, and adaptable to medium and small screens (laptops and tablets)  
[Medium] NFR5. The system must be compatible with Windows and Android, and easily maintainable by the developer  
