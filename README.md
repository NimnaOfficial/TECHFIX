<div align="center">
  <img src="https://img.icons8.com/color/120/000000/android-os.png" alt="Android Logo" width="80"/>
  <img src="https://img.icons8.com/color/120/000000/cloudflare.png" alt="Cloudflare Logo" width="80"/>
  
  # 📱 TechFix - Enterprise Repair Management System
  
  **A scalable, multi-role Android application powered by Cloudflare's serverless edge computing.**
  
  [![Android](https://img.shields.io/badge/Android-Java-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#)
  [![Cloudflare Workers](https://img.shields.io/badge/Cloudflare_Workers-F38020?style=for-the-badge&logo=cloudflare&logoColor=white)](#)
  [![SQLite D1](https://img.shields.io/badge/Cloudflare_D1-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](#)
  [![Retrofit](https://img.shields.io/badge/Retrofit-2.9.0-FF4081?style=for-the-badge)](#)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
</div>

---

## 🎯 Overview

**TechFix** is a fully comprehensive mobile platform designed to bridge the gap between repair technicians, shop managers, and customers. 
Built as a **Native Android Application**, it leverages an ultra-fast, serverless backend via **Cloudflare Workers** and **D1 (SQLite at the edge)**.

From real-time GPS tracking and algorithmic appointment routing to multi-tier Role-Based Access Control (RBAC), TechFix removes the friction from technical repair lifecycles.

---

<p align="center">
  <a href="https://streamable.com/x1kgdd">
    <img src="YOUR_THUMBNAIL_URL" width="300" alt="Watch Project Demo">
  </a>
</p>

<p align="center">
  ▶️ <b>Click to watch the project demo</b>
</p>

---

## 🌟 Advanced System Highlights

* 📍 **Algorithmic Geospatial Auto-Routing:** When customers request a repair, the Cloudflare backend utilizes the **Haversine formula** to calculate the distance from their GPS coordinates to all active branches, instantly auto-assigning them to the nearest available service center and a free technician.
* 🛡️ **Zero-Trust Technician Onboarding:** Newly created technician accounts utilize auto-generated secure credentials. Upon their very first login, the system forcibly traps them in a mandatory password reset flow before granting dashboard access.
* ⚡ **Edge-Native Speed:** By utilizing Cloudflare Workers, API routes execute globally within milliseconds of the end user, with the D1 SQLite database distributed at the edge.

---

## 👥 Features by Role

TechFix enforces strict RBAC to deliver customized dashboard experiences based on the user's role.

| 🧑‍💻 **Customer** | 🛠️ **Technician** | 🏢 **Manager** | 👑 **Admin** |
| :--- | :--- | :--- | :--- |
| 📍 **Smart Auto-Routing**<br>📱 Device Registration & Mgt<br>🔄 Real-time Repair Tracking<br>🧾 View Detailed Invoices<br>⚙️ Profile Customization | 🔒 **Forced 1st-Login Pass Reset**<br>📝 Dynamic Profile Updation<br>📋 View Assigned Tasks<br>📦 Consume Spare Parts<br>📸 Upload Photo Evidence | 📊 Full Branch Management<br>📦 Global Inventory Ledger<br>🧑‍🔧 Manual Tech Assignment<br>💰 Revenue & Sales Reports<br>🔍 Monitor Branch Activity | 👥 Manage All Users<br>⚙️ System-wide Settings<br>🛠️ High-level Diagnostics<br>🗄️ Database Administration |

---

## 🏗️ System Architecture

TechFix relies on an Edge-First architecture, ensuring extremely low latency and high availability by executing the backend on Cloudflare's global CDN nodes.

```mermaid
graph TD
    %% Entities
    APP[📱 Android Native App<br>Retrofit / Java]
    EDGE[⚡ Cloudflare Worker<br>Serverless REST API<br>Geospatial Algorithm]
    D1[(🗄️ Cloudflare D1<br>Edge SQLite Database)]
    CLOUDINARY[☁️ Cloudinary API<br>Image & Object Storage]
    
    %% Relationships
    APP -- "JSON via HTTPS" --> EDGE
    EDGE -- "SQL Transactions" --> D1
    APP -- "Multipart Upload" --> CLOUDINARY
    EDGE -- "Signed Tokens" --> CLOUDINARY
    
    %% Styling
    classDef mobile fill:#3DDC84,stroke:#fff,stroke-width:2px,color:#000
    classDef cf fill:#F38020,stroke:#fff,stroke-width:2px,color:#fff
    classDef db fill:#003B57,stroke:#fff,stroke-width:2px,color:#fff
    classDef cloud fill:#3448C5,stroke:#fff,stroke-width:2px,color:#fff
    
    class APP mobile
    class EDGE cf
    class D1 db
    class CLOUDINARY cloud
```

---

## 🗄️ Database Schema Snapshot

The D1 database is highly normalized to ensure data integrity during parallel API transactions and automated routing algorithms.

```mermaid
erDiagram
    USERS ||--o| CUSTOMERS : "has profile"
    USERS ||--o{ APPOINTMENTS : "technician assigned"
    CUSTOMERS ||--o{ APPOINTMENTS : "books"
    CUSTOMERS ||--o{ DEVICES : "owns"
    BRANCHES ||--o{ BRANCH_SPARE_PARTS : "stocks"
    APPOINTMENTS ||--o{ REPAIR_STATUS_HISTORY : "tracks"
    APPOINTMENTS ||--o{ APPOINTMENT_PARTS : "consumes"

    USERS {
        string id PK
        string role
        string email
        string password_hash
    }
    APPOINTMENTS {
        string id PK
        string device_id FK
        string branch_id FK
        string status
        float customer_latitude
        float customer_longitude
    }
    BRANCHES {
        string id PK
        string name
        float latitude
        float longitude
    }
```

---

## 🚀 Complete Setup Guide

Follow these steps to deploy both the backend and frontend locally or to production.

### 1️⃣ Backend Setup (Cloudflare Workers + D1)

**Prerequisites:** 
- Install [Node.js](https://nodejs.org/) and NPM.
- Install Wrangler CLI: `npm install -g wrangler`

<details>
<summary><b>Click to expand backend deployment steps</b></summary>
<br>

1. **Login to Cloudflare**
   ```bash
   npx wrangler login
   ```
2. **Navigate to the Backend Directory**
   ```bash
   cd cloudflare-backend
   ```
3. **Initialize the Database**
   Create a new D1 database via your Cloudflare Dashboard, or using the CLI:
   ```bash
   npx wrangler d1 create techfix-db
   ```
   *Copy the generated `database_id` and paste it into `cloudflare-backend/wrangler.toml`.*
4. **Set Environment Variables**
   Ensure `JWT_SECRET` is populated in your `wrangler.toml` file under the `[vars]` block for token generation to work successfully.
5. **Run the Schema Migrations**
   Push the table structures to your remote D1 instance:
   ```bash
   npx wrangler d1 execute techfix-db --remote --file=./schema.sql
   ```
6. **Deploy the Worker**
   ```bash
   npx wrangler deploy
   ```
   *This will output a live URL (e.g., `https://techfix-backend.<your-subdomain>.workers.dev`)*.

</details>

### 2️⃣ Frontend Setup (Android Studio)

**Prerequisites:**
- Install [Android Studio](https://developer.android.com/studio) (Giraffe or later).
- Java Development Kit (JDK 17).

<details>
<summary><b>Click to expand Android setup steps</b></summary>
<br>

1. **Open the Project**
   Open the root `TECHFIX` folder inside Android Studio.
2. **Connect the Backend API**
   Open the following file:
   `app/src/main/java/com/mad/techfix/network/RetrofitClient.java`
   
   Replace the `BASE_URL` with your fully unified Cloudflare Worker deployment URL:
   ```java
   private static final String BASE_URL = "https://techfix-backend.codse251f-003.workers.dev/";
   ```
3. **Sync and Build**
   - Wait for Gradle to sync dependencies.
   - Click **Run (Shift + F10)** to launch the app on an emulator or physical device.

</details>

---

## 🔐 Security & Authentication
* **PBKDF2 Hashing:** Passwords are never stored in plaintext. They are salted and hashed using 100k iterations of PBKDF2 (SHA-256) inside the V8 engine natively on the Cloudflare Edge.
* **JWT (JSON Web Tokens):** Secure, stateless session management. Tokens include expiration claims and strict signature validation using HMAC Web Crypto API.
* **Case-Insensitive Constraints:** Email collision checks and logins are strictly evaluated via `LOWER()` SQL queries to ensure case-insensitive routing.
* **Granular API Interceptors:** All API endpoints automatically validate the user's role claim against the required permissions before executing queries.

---

## 🤝 Meet the Team (Contributors)

* **Member 1 (System Admin & Customer Features):** Role assignments, Device management, Location services, Main Dashboard configurations.
* **Member 2 (Manager & Core Logic):** Branch handling, Global Inventory ledgers, High-level Reporting, Data visualization.
* **Member 3 (Android Architecture):** Auth (Login/Register) workflows, UI layouts, Retrofit abstractions, Application foundation.
* **Member 4 (Technician & Repair Lifecycles):** Payment intents, Status history chronologies, Camera image evidence modules.

---

<div align="center">
  <i>Built with ☕ for the Mobile Application Development Module.</i><br>
  <b>Licensed under the MIT License</b>
</div>
