# Inventory Management System

A production-quality full-stack web application designed for managing product catalogs, tracking stock level adjustments, and auditing operations.

## Architecture & Tech Stack

*   **Backend**: Java EE (Servlets, Filter-based Auth, CORS middleware)
*   **ORM**: JPA 3.1 (EclipseLink 4.0.2)
*   **Database**: MySQL 8.0
*   **Frontend**: React.js SPA (Vite/CRA) styled using custom premium vanilla CSS
*   **Security**: BCrypt password hashing, session-based (HttpSession) cookie validation, role-based resource protection

---

## Getting Started

### 1. Database Setup

1.  Create and seed the MySQL database by running:
    ```bash
    # Create the schema and tables
    mysql -u root -p < database/schema.sql

    # Insert mock seed data (21 products, 18 stock logs, admin/staff credentials)
    mysql -u root -p < database/seed.sql
    ```

### 2. Backend Build & Deployment

1.  Verify database credentials in `backend/src/main/resources/META-INF/persistence.xml`.
2.  Package the project into a WAR archive:
    ```bash
    cd backend
    mvn clean package
    ```
3.  Deploy to Apache Tomcat:
    *   Copy `backend/target/inventory.war` to your Tomcat `webapps/` directory.
    *   Set the `JAVA_HOME` and `CATALINA_HOME` environment variables, then run:
        ```bash
        # Start Tomcat
        bin/catalina.bat run
        ```
    *   *Note*: The Tomcat server port has been configured to **`8085`** in `conf/server.xml` to avoid conflicts with Oracle Database listener services.

### 3. Frontend Run

1.  Install dependencies and start the React development server:
    ```bash
    cd frontend
    npm install
    npm start
    ```
2.  The application will open automatically at [http://localhost:3000](http://localhost:3000).

---

## Default Accounts

| Role | Username | Password |
|---|---|---|
| **Administrator** | `admin` | `Admin@123` |
| **Staff Member** | `staff` | `Staff@123` |
