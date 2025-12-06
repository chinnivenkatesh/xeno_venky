# 🛍️ Xeno Shopify Data Ingestion & Insights Service

### FDE Internship Assignment - 2025
**Author:** Venkatesh Chinni

## 📖 Project Overview
This is a full-stack application designed to ingest real-time data from a Shopify store (Customers, Products, Orders) and visualize key business insights on a multi-tenant dashboard.

The system is built with a **Spring Boot** backend for robust API handling and data synchronization, coupled with a **React.js** frontend for interactive data visualization.

---

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3.x, Hibernate (JPA)
* **Database:** MySQL 8.0 (Relational Data Storage)
* **Frontend:** React.js, Bootstrap 5, Recharts, Axios
* **Integration:** Shopify Admin REST API (2024-01)
* **Tools:** Maven, npm, VS Code

---

## 🏗️ Architecture
The application follows a standard MVC architecture with a dedicated Service layer for Shopify communication.

```mermaid
graph TD;
    Shopify[Shopify Store API] -->|JSON Data| JavaService[Spring Boot Service Layer];
    JavaService -->|Save/Update| MySQL[(MySQL Database)];
    MySQL -->|Fetch Data| JavaController[REST Controller];
    JavaController -->|JSON Response| React[React Frontend Dashboard];
````

-----

## 🚀 Setup & Installation

### 1\. Prerequisites

Ensure you have the following installed:

  * Java JDK 17 or higher
  * Node.js & npm
  * MySQL Server

### 2\. Database Setup

Create a local database named `xeno_shopify_db`.

```sql
CREATE DATABASE xeno_shopify_db;
```

*Note: Tables are automatically created by Hibernate on the first run.*

### 3\. Backend Configuration

1.  Navigate to `backend/demo/src/main/resources/`.
2.  Open `application.properties`.
3.  **IMPORTANT:** Update the file with your local MySQL password and Shopify credentials:
    ```properties
    spring.datasource.password=YOUR_MYSQL_PASSWORD
    shopify.store.url=[https://YOUR-STORE.myshopify.com](https://YOUR-STORE.myshopify.com)
    shopify.access.token=YOUR_SHOPIFY_ACCESS_TOKEN
    ```

### 4\. Running the Backend

Open a terminal in the `backend/demo` folder:

```bash
mvnw clean spring-boot:run
```

The server will start at `http://localhost:8080`.

### 5\. Running the Frontend

Open a new terminal in the `frontend` folder:

```bash
npm install
npm start
```

The dashboard will launch at `http://localhost:3000`.

-----

## 🔌 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/sync` | Triggers the `ShopifyService` to fetch and update data from Shopify to MySQL. |
| **GET** | `/api/dashboard-stats` | Returns total counts (Customers, Orders, Revenue) for the dashboard cards. |
| **GET** | `/api/orders` | Returns a list of orders for the visualization charts. |
| **GET** | `/api/customers` | Returns a list of all ingested customers. |

-----

## 🗄️ Database Schema

The solution implements a **Multi-Tenant** strategy where every table includes a `tenant_id` column to isolate store data.

  * **Customers Table:** Stores `shopify_id`, `email`, `first_name`, `total_spent`.
  * **Products Table:** Stores `shopify_product_id`, `title`, `price`, `vendor`.
  * **Orders Table:** Stores `shopify_order_id`, `total_price`, `financial_status`, and has a **Foreign Key** relationship with `Customers`.

-----

## 💡 Assumptions & Trade-offs

1.  **Polling vs Webhooks:** For this assignment, I implemented a "Pull" mechanism (Polling) via the Sync button. In a production environment, I would use Shopify Webhooks to receive real-time updates instantly.
2.  **Authentication:** The current solution allows open access (CORS allowed for localhost). Production deployment would require JWT authentication for secure tenant access.
3.  **Rate Limiting:** The sync logic handles basic data fetching. For large stores, pagination and rate-limit handling (Leaky Bucket algorithm) would be added to respect Shopify's API limits.

-----

## 📹 Video Demo

[Link to Demo Video Coming Soon]

````

***

### **Final Checklist Before You Submit:**
1.  **Add this file** to your project folder.
2.  **Push it to GitHub** using the commands we practiced:
    ```bash
    git add README.md
    git commit -m "Add documentation"
    git push
    ```
3.  **Important:** Since you removed the keys from the code to push it safely, make sure your **Video Demo** clearly shows the app working locally on your machine with the keys active! This proves it works.
````
