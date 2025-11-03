### **Product Evolution**

During this stage, the work of the **Design Department (E)** was consolidated, focusing on the implementation of the **Model–View–Controller (MVC)** pattern and the refinement of the **functional and non-functional requirements**.  
The clarity of the **Class Diagram (CD)**, the correspondence with the requirements, and the overall system structure were improved, showing significant evolution compared to the previous delivery.

**Main Changes**

| Category | Description |
|-----------|-------------|
| **Abstraction Process** | A new version of the **Class Diagram (CD)** was developed with clearer relationships and better-defined classes. Iterations were made over the previous version. |
| **Requirement Correspondence (FR/NFR)** | A direct link was established between the requirements and the CD classes, ensuring traceability between the system’s needs and its design artifacts. |
| **MVC Implementation** | The **MVC** design pattern was partially or fully applied, with defined packages for **Model**, **Controller**, and **View**. An optional data access layer is also considered. |
| **Requirement Refinement** | The number of functional requirements was reduced (from 16 to 9) and non-functional requirements (from 11 to 5), removing redundancies and reinforcing accounting traceability, security, and compatibility. |
| **Process Adjustments** | The internal organization was adapted after team member changes, maintaining coherence and continuity in the workflow. |

```mermaid
flowchart TD
    A[Department A<br>Administration] --> B[Department B<br>Product]
    B --> D[Department D<br>Process]
    D --> E[Department E<br>Design]
    E -->|MVC Implementation<br>and Design Artifacts| B
