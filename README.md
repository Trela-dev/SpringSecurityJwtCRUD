# 🛡️ Spring Security JWT CRUD Application with Roles

## 🚀 Project Overview
This project demonstrates a secure RESTful API built with **Spring Boot** and **Spring Security**, implementing **JWT (JSON Web Token)** authentication.  
It is a complete **CRUD (Create, Read, Update, Delete) application** with **role-based authorization**, designed to showcase modern security practices in Java backend development.

## 🔐 Key Security Features
✅ **Robust Spring Security Implementation**  
✅ **JWT Authentication with Bearer tokens**  
✅ **Role-based authorization** (👤 USER, 🛠 MODERATOR, 👑 ADMIN)  
✅ **Secure password storage with 🔑 BCrypt hashing**  
✅ **Token validation with expiration (⏳ 10 minutes)**  
✅ **Custom security filters for JWT processing**  
✅ **Custom authentication provider integration**

## 🏗 Security Components
-  **JwtFilter** – Validates tokens on each request
-  **JWTCustomUsernamePasswordAuthenticationFilter** – Handles login and token generation
-  **JwtAuthenticationProvider** – Validates JWT tokens
-  **CustomUserDetailsService** – Integrates with Spring Security's authentication flow
-  **ProjectConfig** – Central security configuration
---
## 🔄 Permission Evaluation Logic

The `UserUpdatePermissionEvaluator` implements sophisticated business rules for user updates:

| Current Role | Target User | Action               | Result                                                                 |
|--------------|-------------|----------------------|------------------------------------------------------------------------|
| ADMIN        | Any user    | Update               | ✅ Allowed                                                             |
| MODERATOR    | Regular user| Update               | ✅ Allowed                                                             |
| MODERATOR    | ADMIN user  | Update               | ❌ Blocked (`AdminUpdateForbiddenException`)                           |
| MODERATOR    | Any user    | Assign ADMIN role    | ❌ Blocked (`AdminRoleAssignmentException`)                           |
| USER         | Any user    | Update               | ❌ Blocked (`AccessDeniedException`)                                  |

### Key Rules Explanation:
- **ADMIN** has unrestricted update privileges
- **MODERATOR** can only update non-admin users
- **MODERATOR** cannot promote users to ADMIN
- **USER** role has no update privileges
- Each violation throws specific exception for precise error handling

## 🔥 Security Highlights
-  **JWT authentication** with Bearer tokens
-  **Role-based access control** (👑 ADMIN > 🛠 MODERATOR > 👤 USER)
-  **Password hashing** with BCrypt
-  **Secure token handling** (10min expiration)
-  **CSRF protection disabled** for API (as per JWT best practices)

## 🛠 Technical Stack
-  **Java 17** with **Spring Boot 3.x**
-  **Spring Security** with **JWT support**
-  **JPA/Hibernate** for data persistence
-  **Flyway** for database migrations
-  **PostgreSQL** (or any compatible database)
-  **Docker**

## 📝 API Endpoints
| 🌍 Endpoint      |  Method |  Description                 |  Access                |
|----------------|---------|-----------------------------|------------------------|
| `/register`  | POST    | Register new user           | 🌎 Public               |
| `/login`     | POST    | Authenticate and get JWT    | 🌎 Public               |
| `/users`     | GET     | Get all users               | 👤 USER, 🛠 MODERATOR, 👑 ADMIN |
| `/users/{id}`| PUT     | Update user                 | 🛠 MODERATOR, 👑 ADMIN   |
| `/users/{id}`| DELETE  | Delete user                 | 👑 ADMIN only           |

## 🏁 Setup Instructions

### 🏗 Step 1: Clone the Repository
```bash
git clone https://github.com/Trela-dev/SpringSecurityJwtCRUD.git
cd SpringSecurityJwtCRUD
```

### 🐳 Step 2: Start PostgreSQL Database in Docker
Navigate to project folder(where the pom.xml file is) and run following commadns
Run the following command in the project directory to start the database container:

```bash
docker-compose up -d
```

### 🔨 Step 3: Build and Run the Application

```bash
mvn clean install
java -jar target/SpringSecurityJwtCRUD-0.0.1-SNAPSHOT.jar
```

The application should now be running on [http://localhost:8080](http://localhost:8080).

### 📡 Step 4: Test the API with Postman
Use Postman to test endpoints:

#### 📥 User Registration
**POST** [http://localhost:8080/register](http://localhost:8080/register)
```json
{
  "username": "your_username",
  "password": "your_password"
}
```

#### 🔑 User Login
**POST** [http://localhost:8080/login](http://localhost:8080/login)
```json
{
  "username": "your_username",
  "password": "your_password"
}
```

#### 👥 Retrieve All Users
**GET** [http://localhost:8080/users](http://localhost:8080/users)

#### 🗑 Delete a User
**DELETE** [http://localhost:8080/users/3](http://localhost:8080/users/3)

#### ✏ Update a User
**PUT** [http://localhost:8080/users/3](http://localhost:8080/users/3)
```json
{
  "username": "new_username",
  "password": "new_password"
}
```

### 🔄 Default Users (Admin & Moderator)
| Role      | Username | Password |
|-----------|---------|----------|
| 👑 Admin  | admin   | admin    |
| 🛠 Moderator | moderator | moderator |

Newly registered users are assigned the **👤 USER** role by default.

## ⚙ Configuration
Modify settings like JWT secret or database details in `application.properties`.

## 🚀 Future Improvements
-  Add refresh tokens
-  Implement password reset
-  Write integration tests

## 💼 Why This Project Matters
- 🏗 **Production-ready security**
- 🏛 **Clean architecture** (Separation of concerns, proper layer isolation)
- 🔍 **Follows RESTful best practices**
- 🐳 **Ready for Docker deployment**
