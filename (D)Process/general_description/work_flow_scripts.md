# Workflow Scripts for Project Stage 2

Two workflow scripts were implemented in GitHub Actions to manage pull requests (PRs) in the project. The first script automates PR approvals for department leads under specific conditions, while the second validates branch flow to ensure PRs follow established policies.

## Script 1: Auto-Approval of PRs for Department Leads

To prevent conflicts with GitHub's ruleset policies, which prohibit users from approving their own PRs even when they are department owners, a workflow was created that automatically approves PRs through a bot when the following conditions are met:

- The PR author is the owner of the department to which the merge is being made.
- The TM-ID in the temporary branch matches the TM-ID assigned to the department owner.

> If either of these conditions is not met, the workflow adds a comment to the PR indicating that manual review is required from the department owner or another team member.

### Implementation Details

The workflow is triggered on pull request events (`opened`, `synchronize`) and can also be manually dispatched. It performs the following validations:

1. **Branch Pattern Verification**: Only processes PRs following the `temp.* → dep.*` pattern.
2. **Department Identification**: Extracts the department name from the base branch (target) and maps it to its corresponding owner and TM-ID.
3. **Authorization Check**: Verifies that:
   - The PR author matches the department owner's GitHub username.
   - The TM-ID extracted from the temporary branch matches the department owner's TM-ID.
4. **Automated Response**:
   - If conditions are met: Auto-approves the PR using a bot token.
   - If conditions are not met: Adds an informative comment requesting manual approval.

### Department Mapping

The workflow maintains a mapping of departments to their respective owners:

| Department   | Owner (GitHub)                    | TM-ID |
| ------------ | --------------------------------- | ----- |
| Process      | Ozia112                           | TM-01 |
| Product      | Fabio0302                         | TM-02 |
| Presentation | LicNico                           | TM-03 |
| Requirements | MaximilianoCarrilloAlvarado       | TM-05 |
| Design       | Wimon77                           | TM-06 |

```yml
name: Auto Approve PRs for Department Leads

on:
  pull_request:
    types: [opened, synchronize]
    
  workflow_dispatch:

jobs:
  auto-approve:
    runs-on: ubuntu-latest
    steps:
      - name: Extract branch info and determine code owner
        id: get_info
        run: |
          BRANCH="${{ github.head_ref }}"
          BASE="${{ github.base_ref }}"
          AUTHOR="${{ github.actor }}"
          
          echo "Branch: $BRANCH"
          echo "Base: $BASE"
          echo "Author: $AUTHOR"
          
          # Solo procesar PRs temp.* -> dep.*
          if [[ ! "$BRANCH" == temp.* ]] || [[ ! "$BASE" == dep.* ]]; then
            echo "This workflow only applies to temp.* -> dep.* PRs"
            echo "should_approve=false" >> $GITHUB_OUTPUT
            exit 0
          fi
          
          # Extraer departamento de la rama BASE (destino)
          BASE_DEPARTMENT=$(echo "$BASE" | sed 's/dep\.//')
          echo "Base Department: $BASE_DEPARTMENT"
          
          # Extraer TM-ID de la rama temporal (source)
          TM_ID=$(echo "$BRANCH" | cut -d'.' -f2)
          echo "TM-ID from temp branch: $TM_ID"
          
          # Mapear el propietario del departamento BASE
          case "$BASE_DEPARTMENT" in
            Process)
              BASE_OWNER="Ozia112"
              BASE_TM_ID="TM-01"
              ;;
            Product)
              BASE_OWNER="Fabio0302"
              BASE_TM_ID="TM-02"
              ;;
            Presentation)
              BASE_OWNER="LicNico"
              BASE_TM_ID="TM-03"
              ;;
            Requirements)
              BASE_OWNER="MaximilianoCarrilloAlvarado"
              BASE_TM_ID="TM-05"
              ;;
            Design)
              BASE_OWNER="Wimon77"
              BASE_TM_ID="TM-06"
              ;;
            *)
              echo "Unknown base department: $BASE_DEPARTMENT"
              echo "should_approve=false" >> $GITHUB_OUTPUT
              exit 0
              ;;
          esac
          
          echo "base_owner=$BASE_OWNER" >> $GITHUB_OUTPUT
          echo "base_department=$BASE_DEPARTMENT" >> $GITHUB_OUTPUT
          echo "base_tm_id=$BASE_TM_ID" >> $GITHUB_OUTPUT
          
          # Verificar si:
          # 1. El autor del PR es el dueño del departamento BASE
          # 2. El TM-ID de la rama temporal coincide con el TM-ID del departamento BASE
          if [[ "$AUTHOR" == "$BASE_OWNER" ]] && [[ "$TM_ID" == "$BASE_TM_ID" ]]; then
            echo "✅ AUTO-APPROVE CONDITIONS MET:"
            echo "   - PR author ($AUTHOR) is the owner of $BASE_DEPARTMENT department ($BASE_OWNER)"
            echo "   - TM-ID ($TM_ID) matches the department's TM-ID ($BASE_TM_ID)"
            echo "should_approve=true" >> $GITHUB_OUTPUT
          else
            echo "ℹ️ MANUAL APPROVAL REQUIRED:"
            if [[ "$AUTHOR" != "$BASE_OWNER" ]]; then
              echo "   - PR author ($AUTHOR) is NOT the owner of $BASE_DEPARTMENT department ($BASE_OWNER)"
            fi
            if [[ "$TM_ID" != "$BASE_TM_ID" ]]; then
              echo "   - TM-ID ($TM_ID) does NOT match the department's TM-ID ($BASE_TM_ID)"
            fi
            echo "should_approve=false" >> $GITHUB_OUTPUT
          fi

      - name: Auto-approve PR
        if: steps.get_info.outputs.should_approve == 'true'
        uses: hmarr/auto-approve-action@v3
        with:
          github-token: ${{ secrets.BOT_TOKEN }}

      - name: Add comment if manual approval needed
        if: steps.get_info.outputs.should_approve == 'false'
        uses: actions/github-script@v7
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            const author = '${{ github.actor }}';
            const baseOwner = '${{ steps.get_info.outputs.base_owner }}';
            const baseDepartment = '${{ steps.get_info.outputs.base_department }}';
            
            if (baseOwner) {
              github.rest.issues.createComment({
                issue_number: context.issue.number,
                owner: context.repo.owner,
                repo: context.repo.repo,
                body: `ℹ️ **Manual approval required**\n\nThe PR author (@${author}) is not authorized for auto-approval to the **${baseDepartment}** department.\n\nA review from @${baseOwner} (${baseDepartment} department owner) or another team member is needed.`
              });
            }
```

## Script 2: PR Flow Validation

To prevent incorrect merges between branches, a workflow was created to validate the branch flow in pull requests (PRs) according to the following hierarchical rules:

### Flow Rules

1. **Temporary branches** (`temp.*`) can only merge into department branches (`dep.*`).
2. **Department branches** (`dep.*`) can only merge into delivery stage branches (`Stage*`).
3. **Stage branches** (`Stage*`) can only be used as target branches and cannot be used as source branches.
4. **Personal activity branches** (`PA.*`) are blocked as source branches for any merge.
5. **Main branch** (`main`) cannot be used as a source branch.
6. **Any other branch pattern** not following these conventions is blocked as a source branch.

### Validation Process

The workflow is triggered on pull request events (`opened`, `reopened`, `synchronize`, `ready_for_review`) and performs the following checks:

1. **Personal Branch Block**: Immediately rejects any PR originating from `PA.*` branches with a clear error message.
2. **Temporary Branch Flow**: Validates that `temp.*` branches only target `dep.*` branches.
3. **Department Branch Flow**: Validates that `dep.*` branches only target `Stage*` branches.
4. **Stage Branch Protection**: Prevents `Stage*` branches from being used as source branches in any merge.
5. **Pattern Enforcement**: Blocks any branch that doesn't match the allowed patterns with a descriptive error message.

### Error Handling

When a validation fails, the workflow provides:

- A clear error title indicating the type of violation.
- A detailed explanation of why the PR was blocked.
- The expected flow pattern that should be followed.
- A list of allowed and blocked patterns for reference.

``` yml
  name: PR flow validation

on:
  pull_request:
    types: [opened, reopened, synchronize, ready_for_review]

jobs:
  validate-flow:
    name: validate PR flow
    runs-on: ubuntu-latest
    steps:
      - name: Enforce branch flow policy
        shell: bash
        run: |
          HEAD="${GITHUB_HEAD_REF}"
          BASE="${GITHUB_BASE_REF}"

          echo "=== PR flow validation ==="
          echo "source branch(HEAD): $HEAD"
          echo "target branch(BASE): $BASE"
          echo ""

          # Block PA.* branches as source branches
          if [[ "$HEAD" == PA.* ]]; then
            echo "::error title=Blocked::Pull resquest from personal activities branches (PA.*) are not allowed."
            exit 1
          fi

          # Validate temp.* -> dep.* flow
          if [[ "$HEAD" == temp.* ]]; then
            if [[ "$BASE" != dep.* ]]; then
              echo "::error title=Invalid Flow::Branches with prefix 'temp.*' can only merge into 'dep.*' branches."
              echo "Expected: temp.* -> dep.*"
              exit 1
            fi
            echo "✓ Valid flow: temp.* -> dep.*"
            exit 0
          fi

          #Validate dep.* -> Stage* flow
          if [[ "$HEAD" == dep.* ]]; then
            if [[ "$BASE" != Stage* ]]; then
              echo "::error title=Invalid Flow::Branches with prefix 'dep.*' can only merge into 'Stage*' branches."
              echo "Expected: dep.* -> Stage*"
              exit 1
            fi
            echo "✓ Valid flow: dep.* -> Stage*"
            exit 0
          fi

          # Validate Stage* -> main flow(Blocked above, but documented here)
          if [[ "$HEAD" == Stage* ]]; then
            echo "::error title=Invalid Flow::Branches with prefix 'Stage*' cannot be used as source branches."
            exit 1
          fi

          # Block any other branch
          echo "::error title=Invalid Flow::The source branch '$HEAD' is not allowed for pull requests."
          echo "Allowed patterns: temp.*, dep.*, Stage* (as target only)"
          echo "Blocked patterns: PA.*, any other patterns"
          exit 1
```

---

## Benefits and Impact

These workflow scripts provide significant improvements to the project's development process:

### Automation Benefits

- **Reduced Bottlenecks**: Department leads can merge their own work without waiting for external approvals, while maintaining security through validation.
- **Consistency Enforcement**: The hierarchical branch flow is automatically enforced, preventing human errors in the merge process.
- **Clear Communication**: Automated comments and detailed error messages guide team members when manual intervention is needed.

### Quality Assurance

- **Repository Integrity**: The strict branch flow prevents accidental merges to protected branches like `main`.
- **Traceability**: All merges follow a documented path: `temp.* → dep.* → Stage* → main`.
- **Policy Compliance**: GitHub's security policies are respected while allowing necessary workflow flexibility.

### Team Productivity

- **Immediate Feedback**: Developers receive instant validation results when opening PRs.
- **Self-Service**: Team members can identify and correct flow violations without requiring assistance.
- **Reduced Manual Review**: Department leads' PRs are automatically approved when conditions are met, freeing up time for more complex reviews.

> With these workflow scripts, pull request management is automated, ensuring that established policies for approval and branch flow are consistently followed throughout the project.
