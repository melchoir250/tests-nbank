# tests-nbank

Автотесты для NBank (API + UI).

**API:** `http://localhost:4111`  
**UI:** `http://localhost:3000`  
**Стек:** Java 25, JUnit 5, Maven, RestAssured, AssertJ, Selenide (UI — в работе)

---

## Структура

Откуда что взялось (hw_18 → hw_22) и где лежит сейчас:

| HW | Что это было | Куда легло в этом репо |
|----|--------------|------------------------|
| **18** | Дизайн API-кейсов (`.http`) | `requests/api/` |
| **19** | Junior: тесты «в лоб» на RestAssured | идеи сценариев → `src/test/java/api/` |
| **20** | Middle: models / specs / generators | `src/main/java/{models,specs,generators}/` |
| **21** | Senior: skeleton, steps, comparison | `src/main/java/requests/…`, `models/comparison/`, `configs/` |
| **22** | Дизайн UI-кейсов | `requests/ui/` (+ stub `src/test/java/ui/`, `src/main/java/ui/pages/`) |

```
tests-nbank/
├── requests/                          # дизайн кейсов (ещё не код)
│   ├── api/
│   │   └── iteration2.http            # hw_18: deposit / transfer / profile
│   └── ui/                            # hw_22
│       ├── iteration2                 # оглавление UI-итерации
│       ├── deposit_tests
│       ├── transfer_tests
│       └── profile_tests
│
├── src/main/java/                     # каркас (в основном из hw_20 + hw_21)
│   ├── configs/                       # config.properties → PROPERTY
│   ├── generators/                    # RandomData, RandomModelGenerator
│   ├── models/                        # request/response DTO
│   │   └── comparison/                # ModelAssertions / ModelComparator
│   ├── specs/                         # RequestSpecs / ResponseSpecs
│   ├── requests/
│   │   ├── skelethon/                 # Endpoint, CrudRequester, Validated…
│   │   └── steps/                     # AdminSteps, UserSteps, CustomerContext
│   └── ui/pages/                      # Page Objects (пока stub под Selenide)
│
└── src/test/java/
    ├── api/                           # API-автотесты (сценарии hw_18, каркас hw_21)
    │   ├── BaseApiTest.java
    │   ├── DepositAccountTest.java
    │   ├── TransferMoneyTest.java
    │   └── UpdateProfileNameTest.java
    ├── ui/                            # UI-автотесты (пока BaseUiTest stub)
    └── constants/                     # лимиты сумм / имён для параметризации
```

---

## Запуск API-тестов

Бэкенд должен быть на `localhost:4111`:

```bash
mvn test
```

Один класс / весь пакет:

```bash
mvn test -Dtest=api.DepositAccountTest
mvn test -Dtest=api.TransferMoneyTest
mvn test -Dtest=api.UpdateProfileNameTest
mvn test -Dtest=api.*
```

---

## Что уже есть

| Область | Статус |
|---------|--------|
| API design (`requests/api`) | ✅ |
| UI design (`requests/ui`) | ✅ |
| API autotests (deposit / transfer / profile) | ✅ |
| UI autotests (Selenide) | ⏳ stub |
| Docker / CI | ⏳ позже |
