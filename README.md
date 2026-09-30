# DemoProject – UI + API Automation

- **UI:** Selenium 4 + TestNG for https://www.saucedemo.com
- **API:** REST Assured for https://restful-booker.herokuapp.com

Both share one TestNG setup, config, test data, logging and Extent report, and follow the same structure as
the Adda frameworks (`Automation/AddaWeb`).

## Structure

```
src/main/java
  pageObject/        *_OR       – locators only (Page Object Model, PageFactory)
  applicationUtil/   *Util      – reusable page/component actions + verifications (return boolean, reasons in *MsgList)
  pojo/                         – Product, CustomerDetails
  util/                         – Common_Function (driver + waits + actions), ConfigFileReader, TestDataReader,
                                  ExtentManager, Log, RetryAnalyzer, Constant, TestDescriptionContant
src/main/resources
  config/<env>.properties       – urls, credentials, wait timeouts per environment
  testdata/testData.json        – products, customer, expected messages, negative data sets
  logback.xml                   – console + target/logs/automation.log
src/test/java
  test_scripts/                 – BaseTest + LoginTest, InventoryTest, CartTest, CheckoutTest, EndToEndPurchaseTest
  dataProvider/                 – TestNG data providers reading testData.json
  listener/                     – Extent report, screenshot on failure, retry transformer
```

## API framework

```
src/main/java
  apiUtil/
    SpecBuilder         – request spec (base url, JSON headers, logging filter) and response specs (status, content type, time limit)
    ApiClient           – reusable get/post/put/patch/delete, retries 5xx + connection errors (apiRetryCount)
    ApiLogFilter        – logs every request/response to the log file and the report, masks token/basic auth/password
    Auth                – token cookie / basic auth / none, applied per request
    AuthApiUtil         – POST /auth, creates the token once and shares it with all tests
    BookingApiUtil      – GET/POST/PUT/PATCH/DELETE /booking, returns Response for the test to validate
  pojo/auth, pojo/booking – request/response POJOs (AuthRequest, AuthResponse, Booking, BookingDates, CreateBookingResponse)
  util/JsonUtil         – single Jackson ObjectMapper: POJO -> JSON for requests, JSON -> POJO for responses and test data
  util/APIEndPoint      – endpoint paths
src/main/resources
  schemas/*.json        – JSON schemas (auth token, booking, create response, booking id list)
  testdata/apiTestData.json – bookings, update/patch payloads, invalid credentials/ids, missing field payloads
src/test/java
  api_test_scripts/     – BaseApiTest (cleans up created bookings) + Auth, BookingGet, BookingCreate, BookingUpdate,
                          BookingDelete, BookingLifecycle tests
  dataProvider/ApiDataProvider
```

Token flow: `AuthApiUtil.getToken()` calls `POST /auth` with `apiUsername` / `apiPassword` from config once and caches
the token. Every PUT / PATCH / DELETE made with `Auth.sharedToken()` sends it as `Cookie: token=...`; GET / POST are public
on this API. Restful-Booker tokens expire after a few minutes. When a request with the shared token gets a 403, a new
token is created and the request is sent once more.

Serialisation: `util/JsonUtil` holds a single Jackson `ObjectMapper`. Request POJOs are converted with `JsonUtil.toJson()`,
and responses and test data are read with `JsonUtil.fromJson(..., Pojo.class)`.

### Restful-Booker behaviour covered by the tests

Tests assert what the API actually returns. Where that differs from REST conventions, a comment in the test says so.

| Case | Response |
|---|---|
| Bad credentials on `/auth` | 200 + `{"reason":"Bad credentials"}` (no 401) |
| Missing mandatory field on `POST /booking` | 500 (no validation) |
| Malformed JSON | 400 |
| Unsupported `Accept` header | 418 |
| `totalprice: "abc"` | 200, value stored as `null` |
| PUT/PATCH/DELETE without / with invalid auth | 403 |
| Unknown id on GET | 404, on PUT/PATCH/DELETE 405 |
| Successful DELETE | 201 |

The public server is shared and occasionally returns 5xx. `ApiClient` retries those requests (`apiRetryCount`), and API tests
also get one full test retry (`apiTestRetryCount`).

## Test coverage

| Class | Tests | Groups |
|---|---|---|
| LoginTest | valid login, 5 invalid login cases (data driven), logout | smoke, regression, login, negative |
| InventoryTest | product list, select product → details, add/remove updates badge, sort by price / name | smoke, regression, inventory, cart |
| CartTest | cart details (name, price, qty), remove from cart | regression, cart |
| CheckoutTest | 4 mandatory field cases (data driven), overview totals/tax/payment/shipping, cancel | regression, checkout, negative |
| EndToEndPurchaseTest | full scenario: login → select → add → cart → checkout → details → finish → confirmation (single + multiple products) | smoke, regression, e2e |
| AuthApiTest | token (schema, format), 4 invalid credential cases, malformed body | api, smoke, regression, auth, negative |
| BookingGetApiTest | ping, all ids (schema), by id, filter by name, 4 invalid ids | api, smoke, regression, booking, negative |
| BookingCreateApiTest | 4 data driven bookings (schema + read back), 5 missing field cases, malformed JSON, unsupported Accept, invalid type | api, smoke, regression, booking, negative |
| BookingUpdateApiTest | PUT with token and basic auth, PATCH, 3 invalid auth cases, missing fields, invalid id | api, smoke, regression, booking, auth, negative |
| BookingDeleteApiTest | delete + 404 after, 3 invalid auth cases, invalid id | api, smoke, regression, booking, auth, negative |
| BookingLifecycleApiTest | create → get → put → patch → delete → 404 | api, smoke, regression, e2e |

## Running

```bash
mvn test                                   # full regression, UI + API (testng.xml)
mvn test -DsuiteXmlFile=api.xml            # API only, 4 parallel threads
mvn test -DsuiteXmlFile=smoke.xml          # smoke group
mvn test -DsuiteXmlFile=negative.xml       # negative group
mvn test -DsuiteXmlFile=parallel.xml       # regression, 4 parallel threads
mvn test -DsuiteXmlFile=e2e.xml            # end to end purchase only
mvn test -DsuiteXmlFile=login.xml          # login only
mvn test -Dtest=CartTest                   # one class
mvn test -Dtest='CheckoutTest#verifyCheckoutOverview'   # one method
```

Every test opens its own browser and logs in itself, so any test can run on its own or in any order.

| Option | Values | Default |
|---|---|---|
| `-Denv` | staging, dev, qa, sigmaqa, prod | staging |
| `-Dbrowser` | chrome, firefox, edge, brave | chrome |
| `-Dheadless` | true / false | false |
| `-DrunMode` | local, localLab (headless lab/CI) | local |
| `-DretryCount` | number of retries for failed tests | 0 |
| `-DrunOnDocker` / `-DdockerUrl` | Selenium Grid | false / http://localhost:4444/ |

Maven is not on the PATH on this machine; use IntelliJ's bundled one:
`"/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn" test`

## Output

- HTML report: `TestReport/DemoProject.html` (steps, groups as categories, failure screenshot)
- Failure screenshots: `ErrorScreenshots/`
- Log file: `target/logs/automation.log`
- TestNG reports: `target/surefire-reports/`

## Design notes

- **Locators:** `id` and `data-test` attributes first; XPath only to find a product card by its visible name.
- **Waits:** explicit waits only (`implicitWait=0`, `explicitWait` from config). SauceDemo is a React SPA, so
  the URL changes before the new page renders. Checks wait for page content (titles, row counts) and retry stale elements.
- **Parallel:** driver kept in a `ThreadLocal` in BaseTest, fresh Chrome profile per session, and the report node
  per thread in `ExtentManager`.
- **Chrome popups:** the password manager / leak detection is disabled. Otherwise Chrome's "Change your password"
  dialog (secret_sauce is in public breach lists) blocks clicks after login.
