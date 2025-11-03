# 📌 Rules for Measuring Participation Metrics (Academic)

This document describes how to measure the **individual participation** of the LockerEasy team members for academic purposes.  

---

## 1. Calculation Basis

1. Each Developer earns points for completed activities.  
2. The **weights of completed activities** are added, except:  
   - **Epic** → not counted in individual participation.  
   - **Weight 4 (department lead)** → not counted, as they are role-specific responsibilities.  

3. For each Sprint or delivery, the **average points** across all Developers is calculated.  
4. Interpretation:  
   - **Above average** → higher-than-average contribution (extra work).  
   - **Below average** → lower-than-average contribution (less work).  
   - **At average** → balanced contribution.  

---

## 2. Adjustment for Department Leads

- At the end of the calculation, **+4 fixed points** are added for each department lead, regardless of their normal activities.  
- This acknowledges the additional responsibility of organizing the backlog, reviewing PRs, and leading their area.  

---

## 3. example of measuring

| Miembro  | Actividades completadas (pesos) | Total puntos | Ajuste jefe | Total final |
|----------|----------------------------------|--------------|-------------|-------------|
| TM-01    | 2 + 3 + 1                        | 6            | +4 (jefe)   | 10          |
| TM-02    | 3 + 2                            | 5            | —           | 5           |
| TM-03    | 2 + 2 + 1                        | 5            | —           | 5           |
| TM-04    | 3                                | 3            | —           | 3           |
| TM-05    | 1 + 2                            | 3            | +4 (jefe)   | 7           |

- Media = (10 + 5 + 5 + 3 + 7) / 5 = **6.0**  
- TM-01 y TM-05 → **encima de la media**.  
- TM-02 y TM-03 → **en la media**.  
- TM-04 → **debajo de la media**.  

---

## 4. Data Source

The data on activities completed will be taken from:  

📂 [Logs and activity records per member](../log_keeper_docs/)  

This folder stores the **Sprint logs**, which include the completed activities per member and will be used to calculate participation metrics.  
