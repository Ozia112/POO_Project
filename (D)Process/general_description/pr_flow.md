# 📌 Pull Request and Review Flow — LockerEasy

## Working Branches  

- Each **department** has its own working branch.  
- **Developers** create temporary branches based on their department branch to work on specific activities.  

## Pull Request Flow  

1. **Temporary Developer Branch**  
   - The Developer creates a temporary branch cloned from their department branch.  

2. **Pull Request to Department Branch**  
   - When the activity is finished, the Developer opens a PR to the corresponding department branch.  
   - The **department lead** reviews the PR and decides whether to approve it.  
   - ✅ If approved → merged into the department branch.  
   - ❌ If rejected → returned to the Developer or reassigned.  

3. **Pull Request to Delivery Branch**  
   - Once a department completes all planned activities, the **department lead** opens a PR from the department branch into the **delivery branch**.  

4. **Reviews of Delivery PR**  

   - **Internal Sprint Review** (department leads + Scrum Master):  
     - The **only review that approves or rejects the merge into the delivery branch**.  
     - ✅ If approved → PR is merged into the delivery branch.  
     - ❌ If not → missing activities are reassigned to the same Developer or another available one.  

   - **Academic Sprint Review** (professor as academic Stakeholder):  
     - Reviews the Increment already merged into the delivery branch.  
     - Provides feedback on academic compliance (rubric, deadlines).  
     - **Does not authorize or block merges**, only validates results.  

   - **Client Sprint Review** (main Stakeholder):  
     - Evaluates the Increment and provides functional feedback.  
     - **Does not authorize or block merges**, only validates the product from the business perspective.  

## Internal Review Checklist  

- [ ] Acceptance criteria met.  
- [ ] Code tested and free of critical errors.  
- [ ] Minimal documentation included.  
- [ ] Complies with the Definition of Done (DoD).  

## Workflow

```mermaid
flowchart TD
    A[Developer creates temporary branch <br/> from department branch] --> B[Developer completes activity]
    B --> C[Opens PR to department branch]
    C --> D{Department lead reviews PR}
    D -->|Approved| E[Merged into department branch]
    D -->|Rejected| F[Returned to Dev or reassigned]

    E --> G{Did the department finish all activities?}
    G -->|Yes| H[Lead opens PR to delivery branch]
    G -->|No| B

    H --> I["Internal Sprint Review <br/> (Leads + SM)"]
    I --> J{Meets internal criteria?}
    J -->|Yes| K[Merged into delivery branch]
    J -->|No| F

    K --> L["Academic Sprint Review <br/> (Professor - Academic Stakeholder)"]
    L --> M["Client Sprint Review <br/> (Main Stakeholder)"]
    M --> N[Feedback recorded for future Sprints]
```

---

## Flujo de branchs

``` mermaid
gitGraph
   commit id: "Initial Repo"

   %% Delivery branch (academic delivery)
   branch stage
   checkout stage
   commit id: "Delivery base"

   %% === Department B (Product) ===
   branch dept-B
   checkout dept-B
   commit id: "B: setup"

   %% Feature 1 (approved)
   branch feat-B-1
   checkout feat-B-1
   commit id: "Dev: B.1.0 Implementation"
   commit id: "Tests and docs"
   checkout dept-B
   merge feat-B-1 tag: "PR approved by Lead B"

   %% Feature 2 (needs fixes)
   branch feat-B-2
   checkout feat-B-2
   commit id: "Dev: B.1.1 Initial implementation"
   checkout dept-B
   %% PR initially rejected
   commit id: "Review: requested changes"
   checkout feat-B-2
   commit id: "Fixes applied"
   checkout dept-B
   merge feat-B-2 tag: "PR approved after changes"

   %% When dept B finishes planned activities
   checkout stage
   merge dept-B tag: "PR dept-B → delivery (approved in Internal Sprint Review)"


   %% === Department C (Requirements) ===
   branch dept-C
   checkout dept-C
   commit id: "C: setup"

   %% Feature 1 (approved)
   branch feat-C-1
   checkout feat-C-1
   commit id: "Dev: C.1.0 Initial RFs"
   checkout dept-C
   merge feat-C-1 tag: "PR approved by Lead C"

   %% Feature 2 (approved)
   branch feat-C-2
   checkout feat-C-2
   commit id: "Dev: C.1.1 RNFs and prioritization"
   checkout dept-C
   merge feat-C-2 tag: "PR approved by Lead C"

   %% When dept C finishes planned activities
   checkout stage
   merge dept-C tag: "PR dept-C → delivery (approved in Internal Sprint Review)"

   %% Accumulated delivery after internal reviews
   commit id: "Increment integrated for academic/client review"
```
