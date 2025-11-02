# 📌 Nomenclature, Branches, and Weights — LockerEasy

This document standardizes **activity IDs**, **branch names**, **abbreviations**, and **weights** to maintain clarity and traceability in the project.

---

## 1) Team Member IDs

Each team member is assigned a unique identifier for traceability in branches and activity assignments. In order to maintain consistency, the regular expression pattern for Team Member IDs is `TM-ID` (two digits) eg. `TM-01`, `TM-02`, etc.

### Assignment table

| ID    | Name/GitHub user     |
| ----- | -------------------- |
| TM-01 | Isaac Ortiz          |
| TM-02 | Fabio Gonzalez       |
| TM-03 | Nicolas Canul        |
| TM-04 | ~~Jesus Leon~~       |
| TM-05 | Maximiliano Carrillo |
| TM-06 | Wimon Solis          |

> **Note:** Team Member `TM-04` (Jesus Leon) has been removed from the team due to inactivity.
---

## 2) Branch naming conventions

### 2.1) Delivery branches

In the project timeline will be three main stages of delivery, each with its own branch for integrating the work completed by all departments. The delivery branches are named as follows:

- `Stage1` → **Delivery branch (academic):** in the lapse August to September.
- `Stage2` → **Delivery branch (mid-term):** in the lapse September to October.
- `Stage3` → **Delivery branch (final):** in the lapse October to November.

> The regular expression pattern for all delivery branches is: `Stage[stage_number]`

---

### 2.2) Department branches

Each department has a dedicated branch for integrating activities completed by its members. The following table summarizes the department codes and their corresponding branches, the prefix `dep.` is used for all department branches in order to maintain consistency and automation in the workflow scripts by regular expressions pattern matching:

| Code | Department name    | Department branch                 |
| ---: | ------------------ | --------------------------------- |
|    A | Project (umbrella) | *(implicit, no dedicated branch)* |
|    B | Product            | dep.Product                       |
|    C | Requirements       | dep.Requirements                  |
|    D | Process            | dep.Process                       |
|    E | Design             | dep.Design                        |
|    F | Presentation       | dep.Presentation                  |

> **Note:** The **Competencies** department has been dissolved, and its responsibilities have been integrated into the **Process** department (D). Additionally, a new **Design** department (E) has been created. The previous ID for **Design** (E) was reassigned from **Presentation**, which is now identified as department (F), previously associated with **Competencies**.

---

### 2.3) Temporary branches

Temporary branches are created for individual work on specific tasks before integrating the changes into the corresponding department branches. The naming convention for temporary branches is as follows:

#### **Format**

`temp.TM-ID.Department`

- `TM-ID`: The unique identifier assigned to each team member ([see Team Member IDs](#1-team-member-ids)).  
- `Department`: The name of the department to which the activity belongs ([see Activity ID Format](#31-activity-id-format)).  

**Examples:**  

- `temp.TM-01.Product`  
- `temp.TM-02.Requirements`  

> These branches allow team members to work independently while maintaining traceability and organization within the project.

---

### 2.4) Individual branches

Each team member can create personal branches for individual activities, following the naming conventions outlined by: `PA.TM-ID` eg. `PA.TM-01`, `PA.TM-02`, etc.
PA stands for "Personal Activity" and TM-ID is the unique identifier assigned to each team member ([see Team Member IDs](#1-team-member-ids)).

> `PA.*` branches are **blocked** from merging for any other branch, as per the workflow validation script detailed in [work_flow_scripts.md](work_flow_scripts.md).
---

## 3) Activity Naming Conventions

To ensure clarity and traceability across all tasks and deliverables, the project adopts a structured ID system for identifying activities. This system reflects the hierarchical position and departmental association of each activity.

### 3.1) Activity ID Format

Each activity within the project is assigned a unique identifier (ID) that follows a standardized format. This format ensures consistency and facilitates easy identification of activities throughout the project.

**Canonical form:** `X.n.n`  
Where:

- `X` → Department code (`B`, `C`, `D`, `E`, `F`).  
- `n.n` → Hierarchical numbering:  
  - `x.0` → **Main activity**.  
  - `x.1`, `x.2`, … → **Sub-activities** dependent on the main activity.  

**Examples:**  

- `B.1.0` → Product definition  
- `B.1.1` → Market research  
- `D.1.2` → Nomenclature and weights  

> **Note:** The project name (*LockerEasy*) is implicit in all activity IDs and is not included in the identifier. If the team were to work on additional projects, IDs would include a project-specific prefix (e.g., `A2.X.n.n`, `A3.X.n.n`).

### 3.2) PBI Titles

Each PBI[^1] is titled using the following format:

**Format:** `[ID] Title`  
Where:

- `ID` → The activity ID, following the format described above.  
- `Title` → A concise description of the activity.  

**Examples:**  

- `[C.2.0] Functional requirements specification`  
- `[E.3.1] User interface wireframes`  

> This naming convention ensures that all PBIs are easily identifiable and their context is immediately clear.
---

## 4) Commit title conventions

Each commit message must start with a prefix indicating the type of change being made, followed by the activity ID in parentheses, and a brief description of the change.

- `feat(ID): description` → new feature.  
- `fix(ID): description` → fix.  
- `docs(ID): description` → documentation.  
- `test(ID): description` → tests.  
- `chore(ID): description` → support tasks.  

Examples:  

- `feat(B.1.0): define value proposition`  
- `docs(D.1.0): add general_process.md`  

In case of multiple department activities, you can abreviate the ID to the department code only and specify the full ID activities in the description. eg.:

- `fix(B): correct product backlog items and create scope document`
- Description:
  - B.2.1: corrected PBI[^1] titles and acceptance criteria.
  - B.1.0: created scope document in /Product/.

> **Note:** We should avoid work in different departments activities at the same time in the same branch to maintain clarity and traceability and to avoid merge errors and incompatibilities in the final product.
---

## 5) Activity Weights

- **1 to 3** → Regular tasks.  
- **Chief Department(4)** → Responsibilities of the **Department Lead** (only within their department). Value: 4 points, not acumulative.
- **Epic** → Activities that correspond to the overall project (**level A**).

If the activity needs a collaboration from multiple team members, it can be marked with the weight, and specify in the description the collaboration value of each member and how the collaboration will be done.

> If a task has weight **4**, it is **assumed** to belong to the lead of the same department.

---

## 6) Minimum Criteria per PBI (suggested structure)

Each PBI must contain (in the issue description or related .md doc):  

- **Description** (clear and concise).  
- **Acceptance criteria** (verifiable).  
- **Deliverable** (file(s) or concrete output).  
- **Dependency** (if applicable).  
- **Expected value** (impact).  
- **Weight** (1–3, 4, or Epic).  
- **Sprint** (number of the sprint within the stage).
- **Stage** (number of the stage the activity belongs to).

> **Sub-activities** inherit the context of their main activity and are numbered `x.1`, `x.2`, etc. Each sub-activity must have its own acceptance criteria and deliverable.  

---

## 7) Examples

**Example 1 — Main activity**  
  
  ``` markdown
  [B.1.0] Product definition
  
  - Description: Consolidate the definition of the product (scope, users, value proposition).
  - Acceptance criteria:
    - Clear document in /Product/ with scope and value proposition.
    - Deliverable: product_definition.md
  - Dependency: —
  - Expected value: Align the team with the product vision.
  - Weight: 3
  ```

**Example 2 — Sub-activity**  
  
  ``` markdown
  [B.1.1] Market research
  - Description: Gather references and needs of target users.
  - Acceptance criteria:
    - Comparative matrix of existing solutions.
    - List of findings applicable to the scope.
    - Deliverable: market_research.md
  - Dependency: B.1.0
  - Expected value: Prioritize features based on evidence.
  - Weight: 2
  ```

**Example 3 — Process**  

``` markdown
[D.1.2] Nomenclature and weights
- Description: Document and publish the rules for IDs, branches, and weights.
- Acceptance criteria:
  - nomenclature_and_weights.md file updated and referenced from general_process.md.
  - Deliverable: /Process/general_description/weights_and_nomenclatures.md
- Dependency: —
- Expected value: Standardize project communication and traceability.
- Weight: 2
```

**Example 4 — Activity with collaboration**  

``` markdown
[B.2.0] Product backlog creation
- Description: Create the initial product backlog with prioritized PBIs.
- Acceptance criteria:
  - Initial backlog with at least 10 PBIs.
  - Prioritization based on stakeholder input.
  - Deliverable: product_backlog.md
- Dependency: B.1.0
- Expected value: Provide a clear development roadmap.
- Weight: 3
- Collaboration:
  - TM-01: 2 points (main author)
  - TM-02: 1 point (review and feedback)
```

---

## 8) Abbreviations and Glossary

- **DoD** (*Definition of Done*): Minimum criteria for work to be considered **Done** (tests, review, docs, etc.).  
- **DoR** (*Definition of Ready*): Minimum conditions for a PBI to be ready to be taken (clarity, acceptance criteria, reasonable size).  
- **SM** (*Scrum Master*): Facilitates events and ensures process compliance.  
- **PO** (*Product Owner*): Prioritizes PBIs and represents stakeholder interests.  
- **Academic Stakeholder**: Professor; reviews progress and deadlines, does not authorize merges.  
- **Main Stakeholder**: Client; provides functional feedback, does not authorize merges.  
- **PR** (*Pull Request*): Request to integrate branches.  
- **Internal Sprint Review**: The only review that **authorizes merges** into `stage`.

[^1]: **PBI** (*Product Backlog Item*): Prioritized backlog item.
