# Process Evolution: Stage 2

During the second stage of the project, strategic adjustments were implemented in the organizational structure and development processes to improve efficiency and product quality. The key changes are detailed below.

## 1. Organizational Restructuring

With the objective of optimizing team cohesion and specialization, the following changes were made to the departments:

- **Creation of the Design Department (E):** The Design department was introduced, whose main responsibility is the development of the program's base code. Its first objective is the creation of a functional prototype that will include a basic visual interface and unit tests executable from the command line.
- **Dissolution of the Competences Department:** The Competences department was dissolved. Its responsibilities, related to metrics management and tracking, were transferred to the Process department (D), thus centralizing project administration.

These changes are reflected in the official project nomenclature, as detailed in the [Nomenclatures and Weights](weights_and_nomenclatures.md) document.

### Restructuring of the Nomenclature Document

The [`weights_and_nomenclatures.md`](weights_and_nomenclatures.md) document underwent a comprehensive restructuring and simplification to improve clarity and maintainability:

- **Centralization of nomenclatures:** All naming conventions were consolidated into a single, cohesive location to avoid redundancies and facilitate quick reference.
- **Removal of outdated sections:** Obsolete or deprecated information was eliminated to ensure the document reflects only current practices and standards.
- **Addition of collaboration examples:** A new PBI example was added specifically demonstrating how to define tasks with multiple collaborators, standardizing the format and preventing inconsistencies in collaborative work attribution.
- **Enhancement of commit conventions:** The commit naming rules were expanded with detailed explanations and guidelines, including:
  - Clearer definitions of commit type prefixes (`feat`, `fix`, `docs`, `test`, `chore`)
  - Best practices for activity ID references in commit messages
  - Examples of handling multi-department activities in commits
  - Guidelines to avoid working on different departments simultaneously to maintain traceability

These improvements ensure that all team members have a clear, unified reference for project standards, reducing errors and improving overall communication.

## 2. Repository Automation and Quality

Automated processes were refined to strengthen repository integrity and security. Workflows were implemented to ensure strict compliance with the Pull Request (PR) flow, documented in [pr_flow.md](pr_flow.md). This measure guarantees a robust and traceable change structure in the project history.

### Additional improvements in nomenclatures and PR control

- The **branch nomenclature section was improved**, establishing clearer and more consistent rules to avoid confusion during branch creation.  
  Now the prefixes `temp.*`, `dep.*`, and `Stage*` are explicitly defined with their function within the workflow.
- **Mandatory notes and rules were added** that must be followed to avoid errors or blocks during Pull Request opening.  
  These rules are automatically verified through workflow validation scripts, preventing incorrect merges and ensuring compliance with the hierarchical flow.

### Control File: `work_flow_scripts.md`

The [`work_flow_scripts.md`](work_flow_scripts.md) file contains the **official workflow validation scripts**, written in **YAML** for execution within **GitHub Actions**.  
These scripts perform automatic validations of the branches involved in each Pull Request, ensuring compliance with the patterns defined in the project policy and also auto-approve PRs under certain conditions:

- **Blocks** personal branches (`PA.*`) to prevent merges from unauthorized flows.  
- **Validates** allowed merges:
  - `temp.* → dep.*`
  - `dep.* → Stage*`
- **Prevents** direct merges to `main` or from `Stage*` to protect the main branch.  
- **Provides detailed error messages** indicating the cause of the block and the expected pattern.
- **Auto-approves** PRs under certain conditions, such as when the PR author is the owner of the corresponding department and the TM-ID matches.

Thanks to this automation, the team guarantees that the integration flow remains clean, secure, and documented, significantly reducing the risk of conflicts or human errors.

## 3. Team Member Management

- **Addition:** A new member was integrated into the team, identified as `TM-06` ([Wimon Rafael Solis Chen](weights_and_nomenclatures.md)). This ID system (`TM-XX`) allows maintaining a complete history of members and assigning responsibilities unambiguously.
- **Removal:** Member `TM-04` (Israel Leon), who served as Head of the Competences department, was removed from the team due to inactivity. Their contribution during the first stage of the project is recognized and appreciated, which was fundamental in establishing the initial quality standards.
