## Auth API – Spring Boot JWT Authentication

This project is a simple **JWT-based authentication and user management REST API** built with **Spring Boot 3**, **Spring Security**, **Spring Data JPA**, and **MySQL**.

It exposes endpoints to **register users**, **log in**, and **access protected user data** using a stateless JWT mechanism.

---

### Tech stack

- **Backend**: Spring Boot 3 (Web, Security, Data JPA)
- **Auth**: JWT (JJWT library)
- **Database**: MySQL
- **Build tool**: Maven
- **Java version**: 17

---

### Features

- User registration with email/password
- Login with JWT token generation
- Stateless authentication using `Authorization: Bearer <token>` header
- Protected endpoints accessible only with a valid JWT
- CORS enabled for `http://localhost:8600`

---

### Project structure (high level)

- `AuthApiApplication` – Spring Boot entry point  
- `configs`
  - `SecurityConfiguration` – HTTP security, JWT filter, CORS config  
  - `JwtAuthenticationFilter` – extracts/validates JWT from requests  
  - `ApplicationConfiguration` – authentication provider and beans  
- `controllers`
  - `AuthenticationController` – `/auth/signup`, `/auth/login`  
  - `UserController` – `/users/me`, `/users/` (protected)  
- `dtos` – request DTOs for login/register  
- `entities` – `User` JPA entity  
- `repositories` – `UserRepository` (Spring Data JPA)  
- `services` – `AuthenticationService`, `JwtService`, `UserService`  
- `exceptions` – `GlobalExceptionHandler` for centralized error handling  

---

### Prerequisites

- Java **17**
- Maven (or use the provided `mvnw` / `mvnw.cmd`)
- MySQL running locally

---

### Configuration

Main application configuration is in `src/main/resources/application.properties`:

- **Server port**: `8600`
- **Database**:
  - `spring.datasource.url=jdbc:mysql://localhost:3306/taskdb?...`
  - `spring.datasource.username=root`
  - `spring.datasource.password=root`
- **JPA / Hibernate**:
  - `spring.jpa.hibernate.ddl-auto=update`
- **JWT**:
  - `security.jwt.secret-key=...` (Base64-encoded secret)
  - `security.jwt.expiration-time=3600000` (1 hour in ms)

> **Note:** For production, move secrets (DB password, JWT key) to environment variables or an external config store instead of committing them.

---

### Running the application

From the project root:

```bash
# Using Maven installed on your machine
mvn spring-boot:run

# Or using the Maven wrapper on Windows
mvnw.cmd spring-boot:run
```

The API will be available at: `http://localhost:8600`.

---

### API endpoints

#### Auth

- **POST** `/auth/signup`  
  - **Body (JSON)**:
    ```json
    {
      "email": "user@example.com",
      "password": "password123",
      "fullName": "Test User"
    }
    ```
  - **Response**: created `User` object.

- **POST** `/auth/login`  
  - **Body (JSON)**:
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```
  - **Response**:
    ```json
    {
      "token": "<jwt-token>",
      "expiresIn": 3600000
    }
    ```

#### Users (protected – requires JWT)

Include the header:

```http
Authorization: Bearer <jwt-token>
```

- **GET** `/users/me` – returns the currently authenticated user.  
- **GET** `/users/` – returns the list of all users.

---

### Testing with Postman

A Postman collection is included: `JWT.postman_collection.json`.  
Import it into Postman to try the signup, login, and protected endpoints quickly.

---

### JWT demo walkthrough (what this project shows)

This project is meant as a **hands-on demo of how JWT authentication works in a Spring Boot API**:

1. **User registration (`/auth/signup`)**
   - You send a JSON payload with `fullName`, `email`, and `password`.
   - `AuthenticationService.signup` hashes the password with a `PasswordEncoder` and saves a `User` entity to the `users` table using `UserRepository`.
   - The saved user (without exposing the raw password) is returned so you can see what is stored.

2. **User login and JWT issuance (`/auth/login`)**
   - You send `email` and `password` as JSON.
   - `AuthenticationService.authenticate` uses Spring Security’s `AuthenticationManager` to verify the credentials.
   - On success, `JwtService` generates a JWT containing the user’s email as the subject, signed with the secret key and an expiration time configured in `application.properties`.
   - The API returns a `LoginResponse` with the `token` and `expiresIn`, which you can copy for further requests.

3. **Attaching the JWT to protected requests**
   - For any protected endpoint (e.g. `/users/me`, `/users/`), you include the token in the `Authorization` header:
     ```http
     Authorization: Bearer <jwt-token>
     ```

4. **JWT validation on every request (`JwtAuthenticationFilter`)**
   - `JwtAuthenticationFilter` runs once per request and checks the `Authorization` header.
   - If a Bearer token is present, it:
     - Extracts the email from the token via `JwtService`.
     - Loads the corresponding `UserDetails` from the database.
     - Validates the token (signature and expiration).
     - If valid, it sets an authenticated `UsernamePasswordAuthenticationToken` in `SecurityContextHolder`, so the rest of the request sees the user as logged in.

5. **Accessing the authenticated user (`/users/me`)**
   - `UserController.authenticatedUser` reads the current `Authentication` from `SecurityContextHolder`.
   - It returns the `User` that was set by the JWT filter, showing how to access the current user inside a controller.

6. **Viewing all users (`/users/`)**
   - `UserController.allUsers` uses `UserService` to fetch all users from the database.
   - This endpoint is protected, so it demonstrates that **only requests with a valid JWT** can list users.

Overall, the demo walks through the **full JWT lifecycle**: register → login → receive token → send token in headers → filter validates token → controller accesses authenticated user.

---

### License

This project is provided as-is for learning and demo purposes. Adapt or add a license (e.g. MIT) as needed for your GitHub repository.

