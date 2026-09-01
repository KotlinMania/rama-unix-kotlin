# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/9 (100.0%)
- **Function parity:** 54/56 matched (target 93) — 96.4%
- **Class/type parity:** 12/19 matched (target 25) — 63.2%
- **Combined symbol parity:** 66/75 matched (target 118) — 88.0%
- **Average inline-code cosine:** 0.48 (function body across 5 matched files)
- **Average documentation cosine:** 0.87 (doc text across 5 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. unix.stream

- **Target:** `unix.Stream`
- **Similarity:** 0.45
- **Dependents:** 1
- **Priority Score:** 1011005.4
- **Functions:** 9/10 matched (target 26)
- **Missing functions:** `extensions`
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 2. unix.frame

- **Target:** `unix.Frame`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 31603.2
- **Functions:** 12/13 matched (target 15)
- **Missing functions:** `new`
- **Types:** 1/3 matched (target 5)
- **Missing types:** `Item`, `Error`

### 3. client.connector

- **Target:** `client.Connector`
- **Similarity:** 0.48
- **Dependents:** 0
- **Priority Score:** 31405.2
- **Functions:** 6/6 matched
- **Missing functions:** _none_
- **Types:** 5/8 matched (target 7)
- **Missing types:** `Output`, `Error`, `Connector`

### 4. server.listener

- **Target:** `server.Listener`
- **Similarity:** 0.63
- **Dependents:** 0
- **Priority Score:** 12003.7
- **Functions:** 16/16 matched (target 28)
- **Missing functions:** _none_
- **Types:** 3/4 matched (target 7)
- **Missing types:** `Error`

### 5. unix.mod

- **Target:** `unix.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 11010.0
- **Functions:** 7/7 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Target`

### 6. unix.address

- **Target:** `unix.Address`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 508.7
- **Functions:** 4/4 matched (target 10)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 7. client.mod

- **Target:** `client.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 8. server.mod

- **Target:** `server.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 9. lib

- **Target:** `ramaunix.Lib [STUB]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

