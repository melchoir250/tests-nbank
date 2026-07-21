# tests-nbank

Автотесты для NBank (API + UI).

**API:** `http://localhost:4111`  
**UI:** `http://localhost:3000` (в Selenoid — `http://host.docker.internal:3000`)  
**Стек:** Java 25, JUnit 5, Maven, RestAssured, AssertJ, Selenide, Selenoid

---

## Структура

Откуда что взялось (hw_18 → hw_22) и где лежит сейчас:

| HW | Что это было | Куда легло в этом репо |
|----|--------------|------------------------|
| **18** | Дизайн API-кейсов (`.http`) | `requests/api/` |
| **19** | Junior: тесты «в лоб» на RestAssured | идеи сценариев → `src/test/java/api/` |
| **20** | Middle: models / specs / generators | `src/main/java/api/{models,specs,generators}/` |
| **21** | Senior API: skeleton, steps, comparison | `src/main/java/api/requests/…`, `api/models/comparison/`, `api/configs/` |
| **22** | Дизайн UI-кейсов | `requests/ui/` |
| **UI mid** | Page Objects + общий `BaseUiTest` | `src/main/java/ui/pages/`, `src/test/java/ui/` |
| **UI senior** | Sessions через JUnit Extensions | `src/main/java/common/`, UI-тесты на `@UserSession` |

```
tests-nbank/
├── requests/                          # дизайн кейсов
│   ├── api/
│   │   └── iteration2.http            # deposit / transfer / profile
│   └── ui/
│       ├── deposit_tests
│       ├── transfer_tests
│       └── profile_tests
│
├── src/main/java/
│   ├── api/
│   │   ├── configs/                   # config.properties → PROPERTY
│   │   ├── generators/                # RandomData, RandomModelGenerator
│   │   ├── models/                    # request/response DTO + comparison
│   │   ├── specs/                     # RequestSpecs / ResponseSpecs
│   │   └── requests/
│   │       ├── skelethon/             # Endpoint, CrudRequester, Validated…
│   │       └── steps/                 # AdminSteps, UserSteps, CustomerContext
│   ├── common/                        # senior UI-инфра
│   │   ├── annotations/               # @UserSession, @AdminSession, @Browsers
│   │   ├── extensions/                # JUnit Extensions под аннотации
│   │   └── storage/                   # SessionStorage
│   └── ui/
│       ├── config/                    # UiTestConfig, SelenoidChromeOptions
│       ├── elements/                  # BaseElement, UserBage
│       └── pages/                     # Page Objects (Deposit/Transfer/Profile…)
│
└── src/test/java/
    ├── api/                           # API-автотесты (CustomerContext)
    ├── ui/                            # UI-автотесты (@UserSession + pages)
    └── constants/                     # лимиты сумм / имён
```

---

## Уровни UI

| Уровень | Идея |
|---------|------|
| **Junior** | Селекторы и setup в теле теста |
| **Middle** | Page Objects, общий `BaseUiTest`, fluent-цепочки |
| **Senior** | `@UserSession` / extensions создают юзера и auth до теста; данные сценария (счета, депозиты) — через `SessionStorage.getSteps()` |

Пример senior-теста:

```java
@Test
@UserSession
void shouldDepositMoneyToAccount() {
    CreateAccountResponse account =
        SessionStorage.getSteps().createAccountWithZeroBalance();
    // UI через page objects…
}
```

`@UserSession(2)` — два пользователя (transfer). Auth по умолчанию под 1-м.

---

## Конфиг

`src/main/resources/config.properties`:

- API: `server`, `apiVersion`, `admin.*`
- UI / Selenoid: `uiRemote`, `uiBaseUrl`, `browser`, `browserVersion`, `browserSize`

---

## Запуск

```bash
mvn test -Dtest=api.*                 # только API
mvn test -Dtest=ui.*                  # только UI
mvn test -Dtest=DepositAccountUiTest  # один класс
```

Нужны поднятые API (`:4111`), UI (`:3000`) и Selenoid (`:4444`) для UI-тестов.
