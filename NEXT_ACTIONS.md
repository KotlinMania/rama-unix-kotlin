# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/9 (100.0%)
- **Function parity:** 59/67 matched (target 99) — 88.1%
- **Class/type parity:** 12/19 matched (target 22) — 63.2%
- **Combined symbol parity:** 71/86 matched (target 121) — 82.6%
- **Average inline-code cosine:** 0.29 (function body across 6 matched files)
- **Average documentation cosine:** 0.88 (doc text across 6 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 8 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 1 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. unix.stream

- **Target:** `unix.Stream`
- **Similarity:** 0.26
- **Dependents:** 1
- **Priority Score:** 1011107.4
- **Functions:** 10/11 matched (target 30)
- **Missing functions:** `from`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 2. client.connector

- **Target:** `client.Connector`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 61708.4
- **Functions:** 6/9 matched (target 6)
- **Missing functions:** `connect`, `make_connector`, `make_connector`
- **Types:** 5/8 matched (target 6)
- **Missing types:** `Output`, `Error`, `Connector`

### 3. unix.frame

- **Target:** `unix.Frame`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 31609.1
- **Functions:** 12/13 matched (target 15)
- **Missing functions:** `new`
- **Types:** 1/3 matched (target 5)
- **Missing types:** `Item`, `Error`

### 4. unix.address

- **Target:** `unix.Address`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 30809.9
- **Functions:** 4/7 matched (target 10)
- **Missing functions:** `from`, `from`, `from`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 5. server.listener

- **Target:** `server.Listener`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 12407.6
- **Functions:** 20/20 matched (target 30)
- **Missing functions:** _none_
- **Types:** 3/4 matched (target 6)
- **Missing types:** `Error`

### 6. unix.mod

- **Target:** `unix.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 11010.0
- **Functions:** 7/7 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Target`

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

- **Target:** `ramaunix.Lib`
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

