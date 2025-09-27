
# 🛡️ Spring Security JWT CRUD Application with Roles

## 🚀 Project Overview

This project demonstrates a secure RESTful API built with **Spring Boot** and **Spring Security**, implementing **JWT (JSON Web Token)** authentication.
It is a complete **CRUD (Create, Read, Update, Delete) application** with **role-based authorization**, designed to showcase modern security practices in Java backend development.

🔐 Key Security Features

✅ Robust Spring Security Implementation
✅ JWT Authentication with Bearer tokens
✅ Role-based authorization (👤 USER, 🛠 MODERATOR, 👑 ADMIN)
✅ Secure password storage with 🔑 BCrypt hashing
✅ Token validation with expiration (⏳ 10 minutes)
✅ Refresh tokens for session renewal without re-login
✅ Secure logout endpoint for invalidating refresh tokens
✅ Custom security filters for JWT processing
✅ Custom authentication provider integration
✅ Advanced authorization logic with 🔍 PermissionEvaluator for fine-grained access control


## 📌 Technologies Used

* **Java 21** – main programming language
* **Spring Boot** – backend application framework
* **Spring Security** – authentication and authorization
* **JWT (JSON Web Token)** – token-based authentication
* **JPA (Hibernate)** – object-relational mapping
* **PostgreSQL** – relational database
* **Flyway** – database schema versioning and migrations
* **Maven** – dependency management and build automation
* **Docker** + **Docker Compose** – containerization and database setup
* **REST API** – client-server communication architecture

## 🏗 Security Components

* **JwtFilter** – Validates tokens on each request
* **JWTCustomUsernamePasswordAuthenticationFilter** – Handles login and token generation
* **JwtAuthenticationProvider** – Validates JWT tokens
* **CustomUserDetailsService** – Integrates with Spring Security's authentication flow
* **ProjectConfig** – Central security configuration
* **CustomPermissionEvaluator** – Enables fine-grained, method-level authorization logic based on permissions

---

## 🔄 Permission Evaluation Logic

The `UserUpdatePermissionEvaluator` implements sophisticated business rules for user updates:

| Current Role | Target User  | Action            | Result                                      |
| ------------ | ------------ | ----------------- | ------------------------------------------- |
| ADMIN        | Any user     | Update            | ✅ Allowed                                   |
| MODERATOR    | Regular user | Update            | ✅ Allowed                                   |
| MODERATOR    | ADMIN user   | Update            | ❌ Blocked (`AdminUpdateForbiddenException`) |
| MODERATOR    | Any user     | Assign ADMIN role | ❌ Blocked (`AdminRoleAssignmentException`)  |
| USER         | Any user     | Update            | ❌ Blocked (`AccessDeniedException`)         |

---

## 📝 API Endpoints

| 🌍 Endpoint          | Method | Description                     | Access                    |
| -------------------- | ------ | ------------------------------- | ------------------------- |
| `/api/auth/register` | POST   | Register new user               | 🌎 Public                 |
| `/api/auth/login`    | POST   | Authenticate and get JWT        | 🌎 Public                 |
| `/api/auth/logout`   | DELETE | Invalidate current JWT          | 👤 USER, MOD, ADMIN       |
| `/api/auth/refresh`  | POST   | Refresh JWT using refresh token | 👤 USER, MOD, ADMIN       |
| `/api/users`         | GET    | Get all users                   | 👤 USER, 🛠 MOD, 👑 ADMIN |
| `/api/users/{id}`    | PUT    | Update user                     | 🛠 MODERATOR, 👑 ADMIN    |
| `/api/users/{id}`    | DELETE | Delete user                     | 👑 ADMIN only             |

---

## 🏁 Setup Instructions

### 🏗 Step 1: Clone the Repository

```bash
git clone https://github.com/Trela-dev/SpringSecurityJwtCRUD.git
cd SpringSecurityJwtCRUD
```

### 🐳 Step 2: Start PostgreSQL Database in Docker

```bash
docker-compose up -d
```

### 🔨 Step 3: Build and Run the Application

```bash
mvn clean install
java -jar target/SpringSecurityJwtCRUD-0.0.1-SNAPSHOT.jar
```

App will be running on [http://localhost:8080](http://localhost:8080).

---

## 📡 Test the API with Postman

### 📥 User Registration

**POST** `http://localhost:8080/api/auth/register`

```json
{
  "username": "your_username",
  "password": "your_password"
}
```

### 🔑 User Login

**POST** `http://localhost:8080/api/auth/login`

```json
{
  "username": "your_username",
  "password": "your_password"
}
```

Response includes JWT in the `Authorization` header:
`Authorization: Bearer your_token_here`

---

### 🚪 User Logout

**DELETE** `http://localhost:8080/api/auth/logout`
Invalidates the current JWT (server-side refresh token cleanup if implemented).

---

### ♻ Refresh JWT

**POST** `http://localhost:8080/api/auth/refresh`

```json
{
  "refreshToken": "your_refresh_token_here"
}
```

---

### 👥 Retrieve All Users

**GET** `http://localhost:8080/api/users`

### 🗑 Delete a User

**DELETE** `http://localhost:8080/api/users/3`

### ✏ Update a User

**PUT** `http://localhost:8080/api/users/3`

```json
{
  "username": "new_username",
  "password": "new_password"
}
```

---

## 🔄 Default Users

| Role         | Username    | Password    |
| ------------ | ----------- | ----------- |
| 👑 Admin     | `admin`     | `admin`     |
| 🛠 Moderator | `moderator` | `moderator` |
| 👤 User      | `user1`     | `user1`     |
| 👤 User      | `user2`     | `user2`     |
| 👤 User      | `user3`     | `user3`     |
| 👤 User      | `user4`     | `user4`     |
| 👤 User      | `user5`     | `user5`     |

Newly registered users are assigned the **👤 USER** role by default.

---

## ⚙ Configuration

Adjust JWT secret, expiration time, and DB settings in `application.properties`.

---

## 📦 Postman Collection

A ready-to-use **Postman collection** with all API endpoints is available in the **`Postman`** folder of this repository.
You can import it directly into Postman to quickly start testing the authentication flow (register, login, refresh, logout) and user management endpoints.

---
