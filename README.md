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
✅ **Advanced authorization logic with 🔍 `PermissionEvaluator` for fine-grained access control**


## 📌 Technologies Used

- **Java 21** – main programming language
- **Spring Boot** – backend application framework
- **Spring Security** – authentication and authorization
- **JWT (JSON Web Token)** – token-based authentication
- **JPA (Hibernate)** – object-relational mapping
- **PostgreSQL** – relational database
- **Flyway** – database schema versioning and migrations
- **Maven** – dependency management and build automation
- **JUnit 5** + **MockMvc** – unit and integration testing
- **Mockito** – mocking dependencies in tests
- **Docker** + **Docker Compose** – containerization and database setup
- **REST API** – client-server communication architecture




## 🏗 Security Components
-  **JwtFilter** – Validates tokens on each request
-  **JWTCustomUsernamePasswordAuthenticationFilter** – Handles login and token generation
-  **JwtAuthenticationProvider** – Validates JWT tokens
-  **CustomUserDetailsService** – Integrates with Spring Security's authentication flow
-  **ProjectConfig** – Central security configuration
-  **CustomPermissionEvaluator** – Enables fine-grained, method-level authorization logic based on permissions

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


## 📝 API Endpoints
| 🌍 Endpoint       |  Method |  Description                 |  Access                |
|-------------------|---------|-----------------------------|------------------------|
| `/register`       | POST    | Register new user           | 🌎 Public               |
| `/login`          | POST    | Authenticate and get JWT    | 🌎 Public               |
| `/api/users`      | GET     | Get all users               | 👤 USER, 🛠 MODERATOR, 👑 ADMIN |
| `/api/users/{id}` | PUT     | Update user                 | 🛠 MODERATOR, 👑 ADMIN   |
| `/api/users/{id}` | DELETE  | Delete user                 | 👑 ADMIN only           |

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
> After logging in, you will receive a **JWT token** in the `Authorization` header of the response.  
> Copy the token and use it in the `Authorization` header for all endpoints **other than** `/login` and `/register`.
>
> Format:  
> `Authorization: Bearer your_token_here`
>
> In Postman, go to the **Authorization** tab, choose **Bearer Token**, and paste the token into the **Token** field.



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
---
# 🔄 Default Users (Admin, Moderator & Users)

| Role        | Username   | Password   |
|-------------|------------|------------|
| 👑 Admin     | `admin`     | `admin`     |
| 🛠 Moderator | `moderator` | `moderator` |
| 👤 User      | `user1`     | `user1`     |
| 👤 User      | `user2`     | `user2`     |
| 👤 User      | `user3`     | `user3`     |
| 👤 User      | `user4`     | `user4`     |
| 👤 User      | `user5`     | `user5`     |

Newly registered users are assigned the **👤 USER** role by default.

---
## ✅ Testing

The project includes a comprehensive test suite written in **JUnit 5**, using:

- **Spring's WebMvcTest** – for controller-level integration tests
- **MockMvc** – to simulate HTTP requests and test response handling
- **Mockito** – to mock service and repository layers
- **Custom Mock Repositories** – in-memory implementations for `UserRepository` and `RoleRepository`

### Covered Test Cases

- ✅ Successful and failed login scenarios with JWT token verification
- ✅ Successful and failed user registration
- ✅ Protected endpoints access with valid/invalid tokens
- ✅ Role-based access control (e.g., only admins can update/delete other admins)
- ✅ Conflict scenarios like registering an already existing user
- ✅ Forbidden actions (e.g., moderators trying to assign admin roles)

---

## ⚙ Configuration
Modify settings like JWT secret or database details in `application.properties`.

## 🚀 Future Improvements
-  Add refresh tokens
-  Implement password reset

## 💼 Why This Project Matters
- 🏗 **Production-ready security**
- 🏛 **Clean architecture** (Separation of concerns, proper layer isolation)
- 🔍 **Follows RESTful best practices**
- 🐳 **Ready for Docker deployment**
