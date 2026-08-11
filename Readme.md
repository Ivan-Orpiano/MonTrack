<div align="center">

# 📒 Budget Tracker

**A little ledger app that lives in the cloud — literally. Your data sits in a Google Sheet you already own.**

<img alt="Angular" src="https://img.shields.io/badge/Angular-18-DD0031?logo=angular&logoColor=white">
<img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5.5-3178C6?logo=typescript&logoColor=white">
<img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white">
<img alt="Java" src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white">
<img alt="Database" src="https://img.shields.io/badge/Database-Google%20Sheets%20API-34A853?logo=googlesheets&logoColor=white">
<img alt="PRs Welcome" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg">

<br/>

<img src="docs/demo.gif" alt="Budget Tracker dashboard preview" width="820">

<sub><em>👆 A preview mockup of the actual UI/design system — swap in your own screen recording once you're running it locally (see <a href="#-quick-start">Quick Start</a>).</em></sub>

</div>

---

## ✨ Overview

Most "budget tracker" tutorials give you a to-do list with a dollar sign on it. This one
tries to feel like an actual ledger: a serif headline, tabular numbers that line up in
columns, and index-card-style stat tiles with a coloured edge that tells you what
they mean before you even read the label.

Under the hood it's a proper three-tier app — **Angular talks only to Spring Boot,
Spring Boot talks only to Google Sheets.** No database server to provision, no ORM to
configure — just a spreadsheet, a service account, and a clean REST API in between.

## 🧩 Features

- 💰 **Add, edit, delete** income & expense transactions
- 🔍 **Filter** by type, category, and date range — on the list *and* the dashboard summary
- 📊 **Live dashboard**: total income, total expenses, current balance, transaction count
- ✅ **Validation on both ends** — reactive forms up front, Bean Validation on the API
- 🧾 **Google Sheets as the database** — open the sheet any time and see your data, raw
- 🧱 **Clean layered architecture** — controller → service → repository on the backend,
  core/shared/features/models/services on the frontend
- 🔔 **Toast notifications** for every success/error, driven by a single HTTP interceptor
- 📱 **Responsive** — works from a phone as comfortably as a desktop

## 🛠️ Built with

| Layer | Technology |
|---|---|
| **Frontend** | Angular 18 (standalone components) · TypeScript · Reactive Forms · Angular Router · HttpClient |
| **Backend** | Spring Boot 3 · Java 17 · Bean Validation · Maven |
| **Database** | Google Sheets, via the Google Sheets API v4 + a service account |
| **Styling** | Hand-written SCSS design system — no UI framework, no Tailwind |

## 🏗️ How it fits together

```
┌─────────────────────┐        HTTP / JSON        ┌──────────────────────┐        Sheets API        ┌───────────────────┐
│   Angular frontend   │  ───────────────────────▶ │   Spring Boot API     │  ───────────────────────▶ │   Google Sheet     │
│   localhost:4200      │ ◀─────────────────────── │   localhost:8080      │ ◀───────────────────────  │   "Transactions" tab│
└─────────────────────┘                            └──────────────────────┘                            └───────────────────┘
```

The frontend **never** sees a Google credential — it only ever calls `/api/**`. The
backend is the sole owner of the service account key and all spreadsheet I/O.

## 🚀 Quick Start

```bash
git clone <your-fork-or-repo-url>
cd budget-tracker
```

**1. Backend** — needs Java 17+, Maven, and a Google service account (see the
[Google Cloud & Sheets setup](#-google-cloud--google-sheets-setup) below the first time):

```bash
cd backend
cp .env.example .env        # fill in your spreadsheet ID + credentials path
export $(grep -v '^#' .env | xargs)
mvn spring-boot:run          # → http://localhost:8080
```

**2. Frontend** — needs Node 18.19+/20+:

```bash
cd frontend
npm install
npm start                    # → http://localhost:4200
```

Open **http://localhost:4200**, hit **+ Add Transaction**, and watch the row land in
your Google Sheet in real time. 🎉

---

## 📖 Full documentation

<details>
<summary><strong>🔐 Google Cloud & Google Sheets setup</strong></summary>
<br>

The backend authenticates as a **service account** — the standard way for a server
(as opposed to a signed-in person) to call Google APIs unattended.

1. **Create/select a Google Cloud project** at [console.cloud.google.com](https://console.cloud.google.com/).
2. **Enable the Google Sheets API** — *APIs & Services → Library* → search "Google Sheets API" → **Enable**.
3. **Create a service account** — *APIs & Services → Credentials → Create Credentials → Service account*. No IAM role needed; access is controlled by sharing the sheet (step 6).
4. **Create a JSON key** — open the service account → *Keys* tab → *Add Key → Create new key → JSON*. Keep this file private; never commit it.
5. **Create the sheet** — a new spreadsheet, first tab named `Transactions` (or set `GOOGLE_SHEETS_SHEET_NAME` to match). The header row is created automatically on first run.
6. **Share the sheet** with the service account's `client_email` (found in the JSON key) as **Editor**.
7. **Copy the spreadsheet ID** from the URL: `.../spreadsheets/d/`**`THIS_PART`**`/edit`.

</details>

<details>
<summary><strong>⚙️ Environment variables</strong></summary>
<br>

Copy `backend/.env.example` to `.env` and fill these in — nothing sensitive is
hard-coded anywhere in the source.

| Variable | Required | Default | Description |
|---|---|---|---|
| `GOOGLE_SHEETS_SPREADSHEET_ID` | Yes | — | ID from the sheet's URL |
| `GOOGLE_SHEETS_SHEET_NAME` | No | `Transactions` | Tab used as the data table |
| `GOOGLE_SHEETS_CREDENTIALS_PATH` | One of these two | — | Path to the service account JSON key (local dev) |
| `GOOGLE_SHEETS_CREDENTIALS_JSON` | One of these two | — | Raw JSON key content (cloud/secret-manager friendly); wins if both are set |
| `SERVER_PORT` | No | `8080` | API port |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:4200` | Comma-separated allowed origins |

> No Maven wrapper is bundled — install Maven locally, or run `mvn -N wrapper:wrapper`
> once inside `backend/` to generate `mvnw` for the project.

</details>

<details>
<summary><strong>📡 API reference</strong></summary>
<br>

Base path: `/api/transactions`.

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/transactions` | List, with optional `type`, `category`, `startDate`, `endDate` filters | `200` |
| `GET` | `/api/transactions/{id}` | Fetch one | `200` / `404` |
| `POST` | `/api/transactions` | Create | `201` |
| `PUT` | `/api/transactions/{id}` | Replace | `200` / `404` |
| `DELETE` | `/api/transactions/{id}` | Delete | `204` / `404` |
| `GET` | `/api/transactions/summary` | Totals for the same filters | `200` |

**Create/update body:**

```json
{
  "date": "2026-01-15",
  "type": "EXPENSE",
  "category": "Groceries",
  "description": "Weekly shop",
  "amount": 84.32
}
```

**Summary response:**

```json
{
  "totalIncome": 5200.00,
  "totalExpense": 1830.45,
  "balance": 3369.55,
  "transactionCount": 12
}
```

`balance = totalIncome − totalExpense`, computed server-side over whatever filters
were applied.

**Error shape** (every failure looks like this):

```json
{
  "timestamp": "2026-08-10T14:32:01.123",
  "status": 404,
  "error": "Not Found",
  "message": "Transaction not found with id: abc-123",
  "path": "/api/transactions/abc-123",
  "fieldErrors": null
}
```

| Situation | Status |
|---|---|
| Missing transaction | `404` |
| Invalid request body | `400` (+ `fieldErrors`) |
| Bad query param | `400` |
| Google Sheets failure | `502` |
| Anything unhandled | `500` |

</details>

<details>
<summary><strong>🗂️ Project structure</strong></summary>
<br>

```
budget-tracker/
├── backend/                             Spring Boot REST API
│   └── src/main/java/com/budgettracker/
│       ├── controller/                  REST endpoints
│       ├── service/                     Business logic
│       ├── repository/                  Google Sheets persistence
│       ├── model/                       Domain model
│       ├── dto/                         Request/response/error shapes
│       ├── config/                      Beans & CORS config
│       └── exception/                   Custom exceptions + global handler
│
└── frontend/                            Angular application
    └── src/app/
        ├── core/interceptors/           Global HTTP error handling
        ├── models/                      TypeScript interfaces mirroring the DTOs
        ├── services/                    Centralized API communication
        ├── shared/components/           navbar · toast · confirm-dialog · summary-card
        └── features/                    dashboard · transactions (list + form)
```

</details>

<details>
<summary><strong>🧪 Testing</strong></summary>
<br>

```bash
cd backend && mvn test    # JUnit 5 + Mockito + MockMvc — no real Google creds needed
cd frontend && npm test   # Jasmine + Karma
```

Backend tests cover business logic (create, filtering, summary math, not-found
handling) and the HTTP layer (status codes, error shape) with the service mocked.
Frontend tests cover every `TransactionService` HTTP call and the reactive form's
validation rules.

</details>

<details>
<summary><strong>🩹 Troubleshooting</strong></summary>
<br>

- **`Google Sheets credentials are not configured`** — neither credentials env var is set, or the path is wrong.
- **`502 Bad Gateway`** — usually the sheet isn't shared with the service account's email, the spreadsheet ID is wrong, the tab name doesn't match, or the Sheets API isn't enabled.
- **CORS errors** — make sure the frontend's origin is in `CORS_ALLOWED_ORIGINS`, then restart the backend.
- **"Unable to reach the server"** — the backend isn't running, or it's on a different port than `environment.ts` expects.

</details>

<details>
<summary><strong>⚖️ Known trade-offs</strong></summary>
<br>

Google Sheets is a lightweight, zero-infrastructure database, which comes with real
limits: writes resolve a row number by scanning first (not built for heavy
concurrency), reads pull the full range and filter in-memory (fine for personal use,
not tens of thousands of rows), and the Sheets API has its own quotas.

The layered architecture keeps the door open, though — swap
`GoogleSheetsTransactionRepository` for a JPA-backed implementation of the same
`TransactionRepository` interface, and nothing in the service, controller, or
frontend layers needs to change.

</details>

---

## 🗺️ Ideas for going further

- 📈 Charts on the dashboard (spend by category, income vs. expense over time)
- 🔁 Recurring transactions
- 📤 CSV export
- 💱 Multi-currency support
- 🔐 Real user accounts (right now it's single-sheet, single-user by design)

## 🤝 Contributing

Issues and PRs are welcome. If you're proposing something bigger than a bug fix,
open an issue first so we can talk through the approach.


---

<div align="center">
<sub>Built with Angular, Spring Boot, and a Google Sheet doing its best impression of a database.</sub>
</div>
