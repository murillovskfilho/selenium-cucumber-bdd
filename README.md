# selenium-cucumber-bdd

[![bdd](https://github.com/murillovskfilho/selenium-cucumber-bdd/actions/workflows/bdd.yml/badge.svg)](https://github.com/murillovskfilho/selenium-cucumber-bdd/actions/workflows/bdd.yml)
[![report](https://img.shields.io/badge/Allure%20report-GitHub%20Pages-2ea44f)](https://murillovskfilho.github.io/selenium-cucumber-bdd/)

A **BDD acceptance suite in Java**: Gherkin specifications that read like business rules, backed
by Selenium 4 page objects, REST Assured API steps and an Allure report. The scenarios run
**in parallel on a Selenium Grid** (Chrome and Firefox) in Docker.

The application under test is **ShipDesk**, the multi-tenant shipment portal from
[playwright-e2e-framework](https://github.com/murillovskfilho/playwright-e2e-framework), pinned
to a fixed commit. Same system, two automation stacks: Playwright/TypeScript there,
Selenium/Cucumber/Java here.

## Stack

Java 21 · Maven · Selenium 4.51 · Cucumber 7 · TestNG (parallel runner) · PicoContainer (DI) ·
REST Assured · AssertJ · Allure · Selenium Grid on Docker · GitHub Actions

## What this demonstrates

| Practice | Where |
|---|---|
| **Declarative Gherkin** written as business rules, not click scripts | `src/test/resources/features/` |
| Scenario Outlines with readable example names (`Sign-in is refused: locked account`) | `authentication.feature`, `create_shipment.feature` |
| Custom **parameter types**: `{persona}` resolves to an enum, `{availability}` to a boolean | `steps/ParameterTypes.java` |
| **Dependency injection** with PicoContainer: one fresh `ScenarioContext` per scenario | `context/ScenarioContext.java` |
| **Parallel scenarios** through TestNG's parallel data provider, safe by design | `runners/RunCucumberTest.java` |
| Page objects with **explicit waits only**; fields found by their visible `<label>` | `pages/` |
| **API-backed Givens**: data via REST Assured; login via API + session cookie | `api/ShipDeskApi.java`, `LoginPage.signInViaApi` |
| UI **and** API checks for the same rule (RBAC, tenancy, cancellation) | `access_control.feature` |
| Native browser dialogs (`confirm`) handled explicitly | `ShipmentDetailPage.cancel` |
| Failure evidence: screenshot, URL and page source attached to the report | `hooks/Hooks.java` |
| Remote (Grid) or local browser from the same code, configured by env or `-D` | `config/Config.java`, `driver/DriverFactory.java` |
| CI matrix (Chrome × Firefox) merged into **one Allure report on GitHub Pages** | `.github/workflows/bdd.yml` |

## Run it

Only Docker is required. No JDK, Maven, browsers or drivers on your machine.

```bash
docker compose up -d --wait                          # ShipDesk + Selenium Grid (Chrome, Firefox)
docker compose run --rm tests                        # all scenarios on Chrome
BROWSER=firefox docker compose run --rm tests        # all scenarios on Firefox
TAGS="@smoke" docker compose run --rm tests          # a tag expression
docker compose down
```

Watch the browsers live while tests run: http://localhost:7900 (Chrome) and http://localhost:7901 (Firefox).

With a local JDK 21 and Maven, you can also run against the grid from your IDE or terminal:

```bash
mvn test -Dgrid.url=http://localhost:4444 -Dapp.url=http://shipdesk:3000 -Dapi.url=http://localhost:3000
mvn test -Dheadless=false            # no grid: a local Chrome via Selenium Manager, app on localhost:3000
mvn allure:serve                     # open the Allure report
```

## Feature coverage

| Feature | Scenarios | Highlights |
|---|:-:|---|
| `authentication` | 6 | UI sign-in, refused sign-ins as an outline, redirect back after login |
| `shipment_search` | 4 | API-created data found in the UI, status filter, empty state, paging |
| `create_shipment` | 10 | Data-table input, 8 validation examples, duplicate reference, UI vs API check |
| `cancel_shipment` | 3 | Confirm, dismiss, and a concurrent cancellation by another admin |
| `access_control` | 11 | Role matrix in UI and API, tenant isolation in direct links and search |

**34 scenarios, 217 steps**, run on both Chrome and Firefox.

Tags: `@smoke`, `@auth`, `@search`, `@create`, `@cancel`, `@rbac`, `@tenancy`, `@api`.

## A Gherkin sample

```gherkin
Scenario Outline: Cancelling shipments is <availability> for a <persona>
  Given a shipment created by "admin"
  And I am signed in as "<persona>"
  When I open that shipment
  Then the cancel action is <availability>

  Examples:
    | persona  | availability  |
    | admin    | available     |
    | operator | not available |
    | viewer   | not available |
```

## Project layout

```
src/test/java/com/murilofilho/bdd/
  config/      run configuration (system property > env var > default)
  driver/      WebDriver factory: Grid or local, Chrome or Firefox
  context/     per-scenario state, injected by PicoContainer
  api/         REST Assured client for ShipDesk
  data/        personas and shipment test data
  pages/       page objects
  steps/       step definitions and parameter types
  hooks/       failure evidence and browser teardown
  runners/     parallel TestNG runner
src/test/resources/features/   Gherkin specifications
docker-compose.yml             ShipDesk + Selenium Grid + a Maven container
```

## Notes from building it

* **Why the app's host is `shipdesk`, not `app`.** The first Grid run failed every scenario with
  `ERR_SSL_PROTOCOL_ERROR` on `http://app:3000`. Chrome flags didn't help, because the cause isn't a
  setting: `.app` is a Google TLD on Chrome's **HSTS preload list**, so Chrome forces HTTPS for any
  host named `app`. Renaming the service fixed it.
* **The suite waits for the right response, not just any.** Search is debounced, and the old rows stay
  on screen until the new response renders. `ShipmentsPage.searchFor` waits for the filter to appear
  in the URL and then for the loading indicator to clear. Without that, the step would assert on stale rows.

## License

MIT
