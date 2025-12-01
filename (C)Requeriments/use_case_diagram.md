# Use Case Diagram - LockerEasy System

## Mermaid Diagram

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

---

## Alternative Diagram (Detailed)

```mermaid
flowchart LR
    subgraph Actors
        E[Employee]
        A[Administrator]
        M[Manager]
        S[System]
    end
    
    subgraph "Rental Management"
        UC1_1[Register Locker Rental]
        UC1_2[End Rental]
        UC1_3[View Rental Status]
    end
    
    subgraph "Product Management"
        UC2_1[Add Product]
        UC2_2[Edit Product]
        UC2_3[Delete Product]
        UC2_4[Manage Categories]
    end
    
    subgraph "Sales Management"
        UC3_1[Record Sale]
        UC3_2[Link to Customer]
        UC3_3[Update Inventory]
    end
    
    subgraph "Financial Management"
        UC4_1[Apply Discounts]
        UC5_1[Generate Reports]
        UC5_2[Export to Excel]
        UC6_1[Manage Prices]
    end
    
    subgraph "Monitoring"
        UC7_1[View Locker Grid]
        UC7_2[View Dashboard]
        UC7_3[Track Metrics]
    end
    
    E --> UC1_1
    E --> UC1_2
    E --> UC1_3
    E --> UC3_1
    E --> UC4_1
    E --> UC7_1
    
    A --> UC1_1
    A --> UC1_2
    A --> UC2_1
    A --> UC2_2
    A --> UC2_3
    A --> UC2_4
    A --> UC3_1
    A --> UC6_1
    A --> UC7_2
    
    M --> UC5_1
    M --> UC5_2
    M --> UC7_2
    M --> UC7_3
    
    S -.-> UC3_3
    S -.-> UC5_1
    
    UC3_1 --> UC3_2
    UC3_1 --> UC3_3
    UC1_1 -.->|may include| UC4_1
    UC3_1 -.->|may include| UC4_1

    style E fill:#ffcccc
    style A fill:#ccffcc
    style M fill:#ccccff
    style S fill:#ffffcc
```

---

## Simplified Use Case Diagram

```mermaid
graph TD
    E((Employee))
    A((Administrator))
    M((Manager))
    
    E --> RentLocker[Manage Locker<br/>Rentals]
    E --> RecordSale[Record Product<br/>Sales]
    E --> ApplyDiscount[Apply<br/>Discounts]
    E --> ViewStatus[View Locker<br/>Status]
    
    A --> RentLocker
    A --> ManageProducts[Manage Product<br/>Catalog]
    A --> RecordSale
    A --> ManagePrices[Manage Prices<br/>& Rates]
    A --> ViewDashboard[View<br/>Dashboard]
    
    M --> GenerateReports[Generate Financial<br/>Reports]
    M --> ViewDashboard
    M --> ExportExcel[Export to<br/>Excel]
    
    style E fill:#e1f5ff
    style A fill:#ffe1e1
    style M fill:#e1ffe1
    style RentLocker fill:#b3d9ff
    style ManageProducts fill:#b3d9ff
    style RecordSale fill:#b3d9ff
    style ApplyDiscount fill:#b3d9ff
    style ManagePrices fill:#b3d9ff
    style GenerateReports fill:#b3d9ff
    style ViewStatus fill:#b3d9ff
    style ViewDashboard fill:#b3d9ff
    style ExportExcel fill:#ffb3b3,stroke-dasharray: 5 5
```

---

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
