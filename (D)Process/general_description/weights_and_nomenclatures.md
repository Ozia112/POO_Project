# 📌 Nomenclature, Branches, and Weights — LockerEasy

This document standardizes **activity IDs**, **branch names**, **abbreviations**, and **weights** to maintain clarity and traceability in the project.

---

## 1) Departments and Branches

| Code | Department name    | Department branch                 |
| ---: | ------------------ | --------------------------------- |
|    A | Project (umbrella) | *(implicit, no dedicated branch)* |
|    B | Product            | dep.Product                       |
|    C | Requirements       | dep.Requirements                  |
|    D | Process            | dep.Process                       |
|    E | Presentation       | dep.Presentation                  |
|    F | Competencies       | dep.Competencies                  |

**Delivery branch (academic):** `entrega`  
> All inter-department integrations approved in the **Internal Sprint Review** are merged into `entrega`.

### 1.1) Individual branches

Each team member can create personal branches for individual activities, following the naming conventions outlined

---

## 2) Activity ID Format

**Canonical form:** `A.X.n.n`

- `A` → Project. **Implicit** in all activities for the current project (*LockerEasy*), not written in the ID.  
- `X` → Department (`B`, `C`, `D`, `E`, `F`).  
- `n.n` → Hierarchical numbering:  
  - `x.0` → **Main activity**.  
  - `x.1`, `x.2`, … → **Sub-activities** dependent on the main one.  

**Examples:**  

- `B.1.0 - Product definition`  
- `B.1.1 - Market research`  
- `D.1.2 - Nomenclature and weights`  

> **Note:** If the same team had another project, IDs would be `A2.X.n.n`, `A3.X.n.n`, etc. For *LockerEasy*, `A` is unique and **omitted** in IDs.

---

## 3) Activity Weights

- **1 to 3** → Regular tasks.  
- **Chief Department** → Responsibilities of the **Department Lead** (only within their department). Value: 4 points, not acumulative.
- **Epic** → Activities that correspond to the overall project (**level A**).

If the activity needs a collaboration from multiple team members, it can be marked with the weight, and specify in the description the collaboration value of each member and how the collaboration will be done.

> If a task has weight **4**, it is **assumed** to belong to the lead of the same department.

---

## 4) Team Member IDs

Format: `TM-XX` (two digits)  

- Examples: `TM-01`, `TM-02`, `TM-03`, …  
- Suggested use in titles/descriptions/commits for assignment and traceability.  

**Assignment table (to be filled):**

| ID    | Name/GitHub user     |
| ----- | -------------------- |
| TM-01 | Isaac Ortiz          |
| TM-02 | Fabio Gonzalez       |
| TM-03 | Nicolas Canul        |
| TM-04 | Jesus Leon           |
| TM-05 | Maximiliano Carrillo |

---

## 5) Abbreviations and Glossary

- **PBI** (*Product Backlog Item*): Prioritized backlog item.  
- **DoD** (*Definition of Done*): Minimum criteria for work to be considered **Done** (tests, review, docs, etc.).  
- **DoR** (*Definition of Ready*): Minimum conditions for a PBI to be ready to be taken (clarity, acceptance criteria, reasonable size).  
- **SM** (*Scrum Master*): Facilitates events and ensures process compliance.  
- **PO** (*Product Owner*): Prioritizes PBIs and represents stakeholder interests.  
- **Academic Stakeholder**: Professor; reviews progress and deadlines, does not authorize merges.  
- **Main Stakeholder**: Client; provides functional feedback, does not authorize merges.  
- **PR** (*Pull Request*): Request to integrate branches.  
- **Internal Sprint Review**: The only review that **authorizes merges** into `entrega`.  

---

## 6) Naming Conventions

### Branches

- Stage branches: `stage[stage_number]` (e.g., `stage1`).
- Department: `dep.[department]` (e.g., `dep.requisitos`, `dep.producto`).  
- Temporal (individual work): `temp.[Team ID].[Department]`  
  - Example: `temp.TM-01.Product`.
- Individual branches: `user.[Team ID].individual`  
  - Example: `user.TM-02.individual`.

### PBI Titles

- `[ID] Title`  
  - Example: `[C.2.0] Functional requirements specification`  

### Commit Messages (suggested)

- `feat(ID): description` → new feature.  
- `fix(ID): description` → fix.  
- `docs(ID): description` → documentation.  
- `test(ID): description` → tests.  
- `chore(ID): description` → support tasks.  

Examples:  

- `feat(B.1.0): define value proposition`  
- `docs(D.1.0): add general_process.md`  

---

## 7) Minimum Criteria per PBI (suggested structure)

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

## 8) Examples

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

---

## 9) Operational Summary (branches and merges)

- **Feature → Department branch**: merged **only** with PR **approved by the Department Lead**.  
- **Department → `stage`**: merged **only** after approval in the **Internal Sprint Review**.  
- **Academic review** (professor) and **client review**: provide **feedback**; **do not** authorize merges.
