# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 5/9 (55.6%)
- **Function parity:** 52/60 matched (target 91) — 86.7%
- **Class/type parity:** 10/16 matched (target 20) — 62.5%
- **Combined symbol parity:** 62/76 matched (target 111) — 81.6%
- **Average inline-code cosine:** 0.15 (function body across 5 matched files)
- **Average documentation cosine:** 0.87 (doc text across 5 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 5 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

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

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |
| `client.mod` | `unix.client.Mod` | 0 | `unix/client/mod.rs` | `unix/client/Mod.kt` |
| `unix.mod` | `unix.Mod` | 0 | `unix/mod.rs` | `unix/Mod.kt` |
| `server.mod` | `unix.server.Mod` | 0 | `unix/server/mod.rs` | `unix/server/Mod.kt` |

