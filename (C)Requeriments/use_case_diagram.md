# Use Case Diagram - LockerEasy System

## Diagram

```mermaid
graph TB
    subgraph System["LockerEasy System"]
        UC1["Manage Locker Rentals<br/>- Register rental with customer data<br/>- End rental and free locker<br/>- View active rentals<br/>- Calculate rental costs"]
        UC2["Manage Product Catalog<br/>- Add new products<br/>- Edit product details<br/>- Delete products<br/>- Categorize with etiquetas"]
        UC3["Record Product Sales<br/>- Link sales to customer tickets<br/>- Update inventory<br/>- Apply discounts"]
        UC4["Apply Discounts & Promotions<br/>- Apply rental discounts<br/>- Apply product sale discounts<br/>- Validate discount amounts"]
        UC5["Generate Financial Reports<br/>- View daily revenue<br/>- View transaction history<br/>- Calculate period totals"]
        UC6["Manage Prices & Rates<br/>- Update rental rates<br/>- Update product prices<br/>- Configure system settings"]
        UC7["View Dashboard<br/>- Monitor locker status<br/>- Track active rentals<br/>- View real-time metrics"]
    end
    
    Employee["👤 Employee"]
    Admin["👤 Administrator"]
    Manager["👤 Manager"]
    System_Actor["⚙️ System"]
    
    Employee -->|Performs| UC1
    Employee -->|Performs| UC3
    Employee -->|Performs| UC4
    Employee -->|Views| UC7
    
    Admin -->|Performs| UC1
    Admin -->|Manages| UC2
    Admin -->|Performs| UC3
    Admin -->|Performs| UC4
    Admin -->|Configures| UC6
    Admin -->|Views| UC7
    
    Manager -->|Views| UC5
    Manager -->|Views| UC7
    
    System_Actor -->|Automates| UC3
    System_Actor -.->|Supports| UC5
    
    UC3 -.->|includes| UC4
    UC1 -.->|includes| UC4
    UC5 -.->|extends| UC7

    classDef ucStyle fill:#9f9fff,stroke:#333,stroke-width:2px,color:#000
    classDef actorStyle fill:#fff,stroke:#333,stroke-width:2px,color:#000
    
    class UC1,UC2,UC3,UC4,UC5,UC6,UC7 ucStyle
    class Employee,Admin,Manager,System_Actor actorStyle
```


## Implementation Status Legend

- **Solid lines**: Fully implemented relationships
- **Dashed lines**: Includes/extends relationships or partial implementation
- **Red dashed**: Not implemented (e.g., Export to Excel)

## Actors Description

| Actor | Role | Primary Responsibilities |
|-------|------|-------------------------|
| **Employee** | Frontline staff | Register rentals, record sales, apply discounts, view locker status |
| **Administrator** | System manager | All employee functions + manage products, configure prices, manage categories |
| **Manager** | Business oversight | View reports, analyze metrics, export financial data |
| **System** | Automated processes | Auto-update inventory, calculate totals, maintain audit trail |

## Use Cases Summary

| Use Case | Status | Actors Involved |
|----------|--------|----------------|
| Manage Locker Rentals | **IMPLEMENTED** | Employee, Administrator |
| Manage Product Catalog | **IMPLEMENTED** | Administrator |
| Record Product Sales | **IMPLEMENTED** | Employee, Administrator, System |
| Apply Discounts & Promotions | **IMPLEMENTED** | Employee, Administrator |
| Generate Financial Reports | **IMPLEMENTED** | Manager |
| Export to Excel | **NOT IMPLEMENTED** | Manager |
| Manage Prices & Rates | **PARTIAL** (No UI) | Administrator |
| View Dashboard | **PARTIAL** (No metrics) | Employee, Administrator, Manager |

---

## Notes

- All diagrams represent the current system architecture with implemented features
- Dashed elements indicate planned but not yet implemented features
- The system supports offline operation and maintains transaction audit trails
- Missing critical features: Excel export, authentication system, comprehensive dashboard
