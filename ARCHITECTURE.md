# Auth Service - Architectural Design Document

## Executive Summary

The Auth Service is a critical microservice component of the SmartBank Elite System that provides centralized authentication, authorization, and JWT token generation. It acts as the security backbone for the entire microservices ecosystem, enabling secure inter-service communication and user access control.

---

## 1. System Architecture Overview

### 1.1 High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        SmartBank Elite System                            │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                    API Gateway (Port 8080)                         │ │
│  │  • Request Routing                                                 │ │
│  │  • Load Balancing                                                  │ │
│  │  • Central Entry Point                                             │ │
│  └──────────────────────┬─────────────────────────────────────────────┘ │
│                         │                                               │
│        ┌────────────────┼────────────────┬─────────────────────┐       │
│        │                │                │                     │       │
│        ▼                ▼                ▼                     ▼       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  │   Auth       │  │   Account    │  │ Transaction  │  │   Other      │
│  │  Service     │  │  Service     │  │  Service     │  │  Services    │
│  │ (Port 8085)  │  │ (Port 8086)  │  │ (Port 8087)  │  │              │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
│        │                │                │                     │       │
│        └────────────────┼────────────────┴─────────────────────┘       │
│                         │                                               │
│  ┌──────────────────────┴─────────────────────────────────────────────┐ │
│  │      Eureka Service Discovery Registry (Port 8761)               │ │
│  │  • Service Registration                                          │ │
│  │  • Service Discovery                                             │ │
│  │  • Health Monitoring                                             │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                  Shared Infrastructure                            │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │ │
│  │  │   MySQL DB   │  │   Cache      │  │   Logs       │           │ │
│  │  │   (authdb)   │  │   (Redis)    │  │   (ELK)      │           │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘           │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Auth Service Internal Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    Auth Service (Port 8085)                      │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │         Presentation Layer (REST API)                      │ │
│  │ ┌──────────────────────────────────────────────────────┐  │ │
│  │ │   AuthenticationController                           │  │ │
│  │ │   • POST /unsecure/signup                            │  │ │
│  │ │   • POST /unsecure/login                             │  │ │
│  │ └──────────────────────────────────────────────────────┘  │ │
│  │ ┌──────────────────────────────────────────────────────┐  │ │
│  │ │   TestController                                      │  │ │
│  │ │   • GET /secure/test                                 │  │ │
│  │ └──────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              ▲                                   │
│                              │                                   │
│  ┌───────────────────────────┴──────────────────────────────┐  │
│  │        Security Layer (Spring Security)                  │  │
│  │ ┌──────────────────────────────────────────────────────┐ │  │
│  │ │   JwtAuthenticationFilter                            │ │  │
│  │ │   • Intercepts incoming requests                     │ │  │
│  │ │   • Extracts JWT tokens                              │ │  │
│  │ │   • Validates tokens                                 │ │  │
│  │ │   • Sets security context                            │ │  │
│  │ └──────────────────────────────────────────────────────┘ │  │
│  │ ┌──────────────────────────────────────────────────────┐ │  │
│  │ │   SecurityConfiguration                              │ │  │
│  │ │   • Configures security rules                        │ │  │
│  │ │   • Defines public/protected endpoints               │ │  │
│  │ │   • CORS configuration                               │ │  │
│  │ │   • Password encoding                                │ │  │
│  │ └──────────────────────────────────────────────────────┘ │  │
│  └────────────────────────────────────────────────────────────┘ │
│                              ▲                                   │
│                              │                                   │
│  ┌───────────────────────────┴──────────────────────────────┐  │
│  │      Business Logic Layer (Service)                      │  │
│  │ ┌──────────────────────────────────────────────────────┐ │  │
│  │ │   AuthenticationServiceImpl                           │ │  │
│  │ │   • User registration logic                          │ │  │
│  │ │   • User authentication logic                        │ │  │
│  │ │   • Password validation                              │ │  │
│  │ │   • User persistence                                 │ │  │
│  │ └──────────────────────────────────────────────────────┘ │  │
│  │ ┌──────────────────────────────────────────────────────┐ │  │
│  │ │   JwtServiceImpl                                       │ │  │
│  │ │   • Token generation                                 │ │  │
│  │ │   • Token validation                                 │ │  │
│  │ │   • Token extraction                                 │ │  │
│  │ │   • Claims extraction                                │ │  │
│  │ └──────────────────────────────────────────────────────┘ │  │
│  └────────────────────────────────────────────────────────────┘ │
│                              ▲                                   │
│                              │                                   │
│  ┌───────────────────────────┴──────────────────────────────┐  │
│  │      Data Access Layer (Repository)                      │  │
│  │ ┌──────────────────────────────────────────────────────┐ │  │
│  │ │   UserRepository (Spring Data JPA)                  │ │  │
│  │ │   • findByEmail(String email)                        │ │  │
│  │ │   • save(User user)                                 │ │  │
│  │ │   • findAll()                                        │ │  │
│  │ │   • Custom queries                                   │ │  │
│  │ └──────────────────────────────────────────────────────┘ │  │
│  └────────────────────────────────────────────────────────────┘ │
│                              ▲                                   │
│                              │                                   │
│  ┌───────────────────────────┴──────────────────────────────┐  │
│  │        Data Layer (Database)                             │  │
│  │ ┌──────────────────────────────────────────────────────┐ │  │
│  │ │   MySQL Database (authdb)                            │ │  │
│  │ │   Tables:                                             │ │  │
│  │ │   • users (id, email, password, created_at)         │ │  │
│  │ └──────────────────────────────────────────────────────┘ │  │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 2. Detailed Service Flow Diagrams

### 2.1 User Registration Flow

```
User/Client
    │
    ├─► POST /unsecure/signup
    │   {email, password}
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    AuthenticationController                          │
│    ├─ Receives registration request                 │
│    └─ Calls AuthenticationService.signup()          │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    AuthenticationServiceImpl                          │
│    ├─ Validate input (email, password format)       │
│    ├─ Check if user already exists                  │
│    ├─ Encode password using BCrypt                  │
│    ├─ Create new User entity                        │
│    └─ Call UserRepository.save()                    │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    UserRepository (JPA)                              │
│    ├─ Save user to database                         │
│    └─ Return persisted user                         │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    MySQL Database (authdb)                           │
│    └─ Store user record in users table              │
    └─────────────────────────────────────────────────┘
    │
    ◄─ User entity returned
    │
    ▼
Client receives: {id, email, createdAt}
Status: 201 Created
```

### 2.2 User Login Flow

```
User/Client
    │
    ├─► POST /unsecure/login
    │   {email, password}
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    AuthenticationController                          │
│    ├─ Receives login request                        │
│    ├─ Calls AuthenticationService.authenticate()    │
│    └─ Calls JwtService.generateToken()              │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    AuthenticationServiceImpl                          │
│    ├─ Call UserRepository.findByEmail()             │
│    ├─ Validate password (BCrypt comparison)         │
│    ├─ If valid: return User entity                  │
│    └─ If invalid: throw AuthenticationException     │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    JwtServiceImpl                                     │
│    ├─ Extract user claims                           │
│    ├─ Set token expiration (1 hour)                 │
│    ├─ Sign with secret key (HS256)                  │
│    └─ Generate and return JWT token                 │
    └─────────────────────────────────────────────────┘
    │
    ◄─ JWT token + expiration time returned
    │
    ▼
Client receives: {token: "JWT_TOKEN", expiresIn: 3600000}
Status: 200 OK
```

### 2.3 Secured Request Flow (From Other Services)

```
Other Service (Account/Transaction)
    │
    ├─► GET /secure/endpoint
    │   Headers: {Authorization: Bearer JWT_TOKEN}
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    JwtAuthenticationFilter                           │
│    ├─ Intercept request                             │
│    ├─ Extract token from Authorization header       │
│    ├─ Call JwtService.validateToken()               │
│    ├─ Extract user email from token                 │
│    ├─ Set SecurityContext with user details         │
│    └─ Continue filter chain                         │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    SecurityConfiguration                             │
│    ├─ Check if endpoint is secured                  │
│    ├─ Verify user is authenticated                  │
│    └─ Allow/Deny access                             │
    └─────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────┐
│    Controller (TestController)                       │
│    └─ Process authenticated request                 │
    └─────────────────────────────────────────────────┘
    │
    ◄─ Response returned
    │
    ▼
Requesting Service receives: Response
Status: 200 OK
```

---

## 3. Component Architecture

### 3.1 Controller Layer

**AuthenticationController**
- Endpoint: `/unsecure/signup`
  - HTTP Method: POST
  - Request Body: RegisterUserDto
  - Response: User entity
  - Description: Registers new user

- Endpoint: `/unsecure/login`
  - HTTP Method: POST
  - Request Body: LoginUserDto
  - Response: LoginResponseDTO (token + expiration)
  - Description: Authenticates user and returns JWT token

**TestController**
- Endpoint: `/secure/test`
  - HTTP Method: GET
  - Response: Test message
  - Description: Verifies secured endpoint access

### 3.2 Service Layer

**AuthenticationService (Interface)**
```java
User signup(RegisterUserDto registerUserDto);
User authenticate(LoginUserDto loginUserDto);
```

**JwtService (Interface)**
```java
String generateToken(User user);
long getExpirationTime();
boolean validateToken(String token);
String extractUserEmail(String token);
```

### 3.3 Repository Layer

**UserRepository (Spring Data JPA)**
```java
Optional<User> findByEmail(String email);
User save(User user);
```

### 3.4 Security Layer

**JwtAuthenticationFilter**
- Extends `OncePerRequestFilter`
- Intercepts all incoming requests
- Validates JWT tokens
- Sets SecurityContext for authenticated users

**SecurityConfiguration**
- Configures Spring Security
- Defines public endpoints (`/unsecure/**`)
- Defines protected endpoints (`/secure/**`)
- Configures password encoder (BCrypt)
- Registers JWT filter

---

## 4. Data Models

### 4.1 User Entity
```
User
├── id: Long (Primary Key)
├── email: String (Unique)
├── password: String (Encoded with BCrypt)
└── createdAt: LocalDateTime
```

### 4.2 DTOs

**LoginUserDto**
```
LoginUserDto
├── email: String
└── password: String
```

**RegisterUserDto**
```
RegisterUserDto
├── email: String
└── password: String
```

**LoginResponseDTO**
```
LoginResponseDTO
├── token: String
└── expiresIn: long
```

**CacheDTO**
```
CacheDTO
├── key: String
└── value: Object
```

---

## 5. Integration Points

### 5.1 Integration with API Gateway (Port 8080)

```
Request Flow:
┌──────────────────────────────────────┐
│     Client Request                   │
└──────────────────────────┬───────────┘
                           │
                           ▼
                   ┌──────────────────┐
                   │   API Gateway    │
                   │   (Port 8080)    │
                   └────────┬─────────┘
                            │
                ┌───────────┴────────────┐
                │                       │
                ▼                       ▼
        ┌──────────────────┐    ┌─────────────────┐
        │  Auth Service    │    │ Other Services  │
        │  (Port 8085)     │    │                 │
        └──────────────────┘    └─────────────────┘
```

**How it Works:**
1. Client sends request to API Gateway
2. Gateway routes `/auth/*` to Auth Service
3. Gateway routes `/account/*` to Account Service (with JWT validation)
4. Auth Service returns tokens
5. Other services use tokens for inter-service communication

### 5.2 Integration with Account Service (Port 8086)

```
Account Service Flow:
┌────────────────────────────────────────────┐
│    Client Request to Account Service       │
│    (with JWT token in header)              │
└────────────┬─────────────────────────────┘
             │
             ▼
    ┌────────────────────────┐
    │   API Gateway          │
    │   (Port 8080)          │
    └────────────┬───────────┘
                 │
    ┌────────────▼─────────────┐
    │  Account Service         │
    │  (Port 8086)             │
    │                          │
    │  ┌────────────────────┐  │
    │  │ Validate JWT Token │  │
    │  │ (from header)      │  │
    │  └────────┬───────────┘  │
    │           │              │
    │           ▼              │
    │  ┌────────────────────┐  │
    │  │ Extract User ID    │  │
    │  │ from Token Claims  │  │
    │  └────────┬───────────┘  │
    │           │              │
    │           ▼              │
    │  ┌────────────────────┐  │
    │  │ Fetch User's       │  │
    │  │ Accounts           │  │
    │  └────────────────────┘  │
    │                          │
    └────────────────────────┘
```

**JWT Token Usage:**
- Token is validated on Account Service side
- User identity is extracted from token claims
- Account operations are authorized based on user ID

### 5.3 Integration with Transaction Service (Port 8087)

```
Transaction Service Flow:

┌─────────────────────────────────────────────┐
│   Client Transaction Request                │
│   (with JWT token in header)                │
└────────────┬────────────────────────────────┘
             │
             ▼
    ┌──────────────────────────┐
    │   API Gateway            │
    │   (Port 8080)            │
    └────────────┬─────────────┘
                 │
    ┌────────────▼──────────────┐
    │  Transaction Service      │
    │  (Port 8087)              │
    │                           │
    │  ┌──────────────────────┐ │
    │  │ JWT Token Validation │ │
    │  └──────────┬───────────┘ │
    │             │             │
    │             ▼             │
    │  ┌──────────────────────┐ │
    │  │ Extract User Details │ │
    │  │ from Token           │ │
    │  └──────────┬───────────┘ │
    │             │             │
    │             ▼             │
    │  ┌──────────────────────┐ │
    │  │ Authorize Transaction│ │
    │  │ for User             │ │
    │  └──────────┬───────────┘ │
    │             │             │
    │             ▼             │
    │  ┌──────────────────────┐ │
    │  │ Process Transaction  │ │
    │  │ (debit/credit)       │ │
    │  └──────────────────────┘ │
    │                           │
    └────────────────────────────┘
```

**Authorization Flow:**
1. User logs in via Auth Service → Gets JWT token
2. Token includes user ID and email claims
3. User makes transaction request with token
4. Transaction Service validates token and extracts user ID
5. Ensures user can only access their own transactions

### 5.4 Integration with Eureka Service Registry (Port 8761)

```
Service Discovery Flow:

┌──────────────────────────────────────────┐
│     Auth Service Startup                 │
└────────────┬─────────────────────────────┘
             │
             ▼
    ┌──────────────────────────┐
    │ Register with Eureka:    │
    │ - Service Name: auth-srv │
    │ - Host: localhost        │
    │ - Port: 8085             │
    │ - Status: UP             │
    └────────────┬─────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │   Eureka Service Registry    │
    │   (Port 8761)                │
    │                              │
    │   Registered Services:       │
    │   ├─ auth-server:8085        │
    │   ├─ account-server:8086     │
    │   ├─ transaction-server:8087 │
    │   └─ api-gateway:8080        │
    │                              │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │  Service Discovery           │
    │  - Other services lookup     │
    │  - Auth Service location     │
    │  - Load balancing            │
    │  - Health monitoring         │
    └──────────────────────────────┘
```

**Service Discovery Benefits:**
- Services don't need hardcoded URLs
- Automatic load balancing
- Health checking
- Dynamic scaling

---

## 6. Security Architecture

### 6.1 Authentication Flow

```
┌─────────────────────────────────────────────────────┐
│              Authentication Process                  │
└─────────────────────────────────────────────────────┘

Step 1: User Credentials
┌──────────────────────────┐
│ Email: user@bank.com     │
│ Password: SecurePass123  │
└────────────┬─────────────┘
             │
             ▼
Step 2: Password Encoding
┌──────────────────────────┐
│ Encode with BCrypt       │
│ $2a$10$NjN$Kd$...       │
└────────────┬─────────────┘
             │
             ▼
Step 3: Compare with Stored Hash
┌──────────────────────────┐
│ Match against DB         │
│ password_hash column     │
└────────────┬─────────────┘
             │
      ┌──────┴──────┐
      │             │
      ▼             ▼
    Match      No Match
      │             │
      ▼             ▼
   Valid        Invalid
   User         Auth
   Found      Exception
      │
      ▼
Step 4: Generate JWT Token
┌──────────────────────────┐
│ Header: {alg: HS256}     │
│ Payload: {email, id}     │
│ Signature: HMAC(secret)  │
│                          │
│ Result: eyJhbGc...      │
└──────────────────────────┘
```

### 6.2 Authorization Flow

```
┌──────────────────────────────────────────────────────┐
│         Authorization Process                        │
└──────────────────────────────────────────────────────┘

Step 1: Client Sends Secured Request
┌──────────────────────────────────────┐
│ GET /secure/endpoint                 │
│ Authorization: Bearer JWT_TOKEN      │
└────────────┬─────────────────────────┘
             │
             ▼
Step 2: JWT Filter Intercepts
┌──────────────────────────────────────┐
│ Extract token from header            │
│ "Bearer eyJhbGc..."                  │
│ Remove "Bearer " prefix              │
└────────────┬─────────────────────────┘
             │
             ▼
Step 3: Validate Token
┌──────────────────────────────────────┐
│ Check signature with secret key      │
│ Verify expiration time               │
│ Parse claims (email, id)             │
└────────────┬─────────────────────────┘
             │
      ┌──────┴──────┐
      │             │
      ▼             ▼
   Valid       Invalid/Expired
   Token         Token
      │             │
      ▼             ▼
   Extract      Return 401
   User         Unauthorized
   Details
      │
      ▼
Step 4: Set Security Context
┌──────────────────────────────────────┐
│ Create UsernamePasswordToken         │
│ Set user details in context          │
│ Mark as authenticated                │
└────────────┬─────────────────────────┘
             │
             ▼
Step 5: Allow Request to Proceed
┌──────────────────────────────────────┐
│ Spring Security grants access        │
│ Route to controller method           │
│ User context available in handler    │
└──────────────────────────────────────┘
```

### 6.3 Password Security

- **Algorithm**: BCrypt with configurable strength
- **Salt**: Automatically generated per password
- **Storage**: Never store plaintext passwords
- **Validation**: Constant-time comparison to prevent timing attacks

---

## 7. Database Schema

### 7.1 Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);
```

### 7.2 Data Relationships

```
User Entity
├── id: Unique identifier
├── email: User email (unique constraint)
├── password: BCrypt hashed password
└── createdAt: Account creation timestamp
```

---

## 8. Configuration Management

### 8.1 Application Properties

```properties
# Server Configuration
spring.application.name=auth-server
server.port=8085

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/authdb
spring.datasource.username=root
spring.datasource.password=Anshu@123
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
security.jwt.expiration-time=3600000  # 1 hour in milliseconds

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true

# Logging Configuration
logging.level.org.springframework.security=INFO
```

### 8.2 Environment-Specific Configuration

**Development (application-dev.properties)**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/authdb_dev
logging.level.org.springframework.security=DEBUG
```

**Production (application-prod.properties)**
```properties
spring.datasource.url=jdbc:mysql://prod-db-server:3306/authdb
spring.jpa.show-sql=false
logging.level.org.springframework.security=WARN
security.jwt.secret-key=${JWT_SECRET_KEY}  # From environment variable
```

---

## 9. Error Handling Architecture

### 9.1 Exception Handling Hierarchy

```
Exception
├── AuthenticationException
│   └── Custom: UserNotAuthenticatedException
├── EntityExistsException
│   └── Custom: UserAlreadyExistsException
├── IllegalArgumentException
│   └── Custom: InvalidTokenException
└── RuntimeException
    └── Custom: ServiceException
```

### 9.2 Global Exception Handler

**GlobalExceptionHandler**
- Catches all exceptions globally
- Returns consistent error responses
- Includes error code and message
- Logs errors for audit trail

**Error Response Format**
```json
{
  "status": 401,
  "message": "Invalid credentials",
  "timestamp": "2026-03-01T10:30:00Z",
  "path": "/unsecure/login"
}
```

---

## 10. Performance Considerations

### 10.1 Database Performance

- **Connection Pooling**: Configured via Hibernate
- **Query Optimization**: 
  - Index on email column for findByEmail()
  - Lazy loading of relationships
  - Avoid N+1 query problems

### 10.2 JWT Performance

- **Token Size**: Minimal claims (email, id)
- **Stateless**: No server-side session storage
- **Caching**: Token validation is fast (no DB lookup)

### 10.3 Scaling Considerations

```
Single Instance:
┌──────────────────────┐
│  Auth Service        │
│  (Port 8085)         │
│  Single Instance     │
└──────────────────────┘

Scaled with Load Balancer:
        ┌─────────────────┐
        │ Load Balancer   │
        │ (Port 8085)     │
        └────────┬────────┘
                 │
      ┌──────────┼──────────┐
      │          │          │
      ▼          ▼          ▼
  ┌────────┐ ┌────────┐ ┌────────┐
  │ Auth #1│ │ Auth #2│ │ Auth #3│
  │ 8085   │ │ 8085   │ │ 8085   │
  └────────┘ └────────┘ └────────┘
      │          │          │
      └──────────┼──────────┘
                 │
                 ▼
            ┌─────────────┐
            │ MySQL DB    │
            │ (Shared)    │
            └─────────────┘
```

---

## 11. Monitoring and Observability

### 11.1 Health Checks

**Eureka Health Monitoring**
- Service status continuously monitored
- Health endpoint: `/actuator/health`
- Automatic deregistration if unhealthy

### 11.2 Logging Strategy

**Request Logging**
```
2026-03-01 10:30:15 INFO AuthenticationFilter: Token validated for user: user@bank.com
2026-03-01 10:30:16 INFO AuthenticationServiceImpl: User registered: user@bank.com
2026-03-01 10:30:17 INFO JwtServiceImpl: Token generated for user: user@bank.com
```

**Error Logging**
```
2026-03-01 10:31:00 ERROR GlobalExceptionHandler: Authentication failed for user: invalid@bank.com
2026-03-01 10:31:01 ERROR GlobalExceptionHandler: User already exists: duplicate@bank.com
```

---

## 12. Deployment Architecture

### 12.1 Containerized Deployment (Docker)

```dockerfile
FROM openjdk:21-slim
COPY target/auth-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8085
```

### 12.2 Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: smartbank/auth-service:1.0
        ports:
        - containerPort: 8085
        env:
        - name: EUREKA_URL
          value: http://eureka:8761
        - name: DB_URL
          valueFrom:
            configMapKeyRef:
              name: db-config
              key: url
```

---

## 13. Sequence Diagrams

### 13.1 Complete Login and Request Sequence

```
User          Gateway        Auth Service    Account Service   Database
  │              │                │                  │              │
  ├─Login────────>               │                  │              │
  │              │                │                  │              │
  │              ├─POST /login───>│                  │              │
  │              │                │                  │              │
  │              │                ├─findByEmail────────────────────>│
  │              │                │<─User entity──────────────────┤
  │              │                │                  │              │
  │              │                ├─validatePassword─────────────┐  │
  │              │                │<─true───────────────────────┘  │
  │              │                │                  │              │
  │              │                ├─generateToken──┐ │              │
  │              │                │<─JWT token─────┘ │              │
  │              │                │                  │              │
  │              │<─Token────────┤                  │              │
  │<─Token───────┤                │                  │              │
  │              │                │                  │              │
  ├─Request──────────────────────────────────────────>              │
  │  (with Token)
  │              │                │                  │              │
  │              ├─GET /account───────────────────────>             │
  │              │  (with Token)  │                  │              │
  │              │                │                  │              │
  │              │                │    ├─validateToken─────────┐   │
  │              │                │    │<─valid─────────────┘   │
  │              │                │    │                        │
  │              │                │    ├─extractUserEmail──┐    │
  │              │                │    │<─user@bank.com──┘      │
  │              │                │    │                        │
  │              │                │    ├─getAccounts───────────>│
  │              │                │    │<─Accounts─────────────┘
  │              │                │    │                        │
  │<─Accounts─────────────────────────┤                         │
  │              │                │                  │              │
```

---

## 14. API Contract

### 14.1 Request/Response Examples

**Signup Request/Response**
```
Request:
POST /unsecure/signup HTTP/1.1
Content-Type: application/json

{
  "email": "newuser@bank.com",
  "password": "Secure@Pass123"
}

Response: 201 Created
{
  "id": 1,
  "email": "newuser@bank.com",
  "createdAt": "2026-03-01T10:30:00Z"
}
```

**Login Request/Response**
```
Request:
POST /unsecure/login HTTP/1.1
Content-Type: application/json

{
  "email": "user@bank.com",
  "password": "Secure@Pass123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

**Secured Request/Response**
```
Request:
GET /secure/test HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response: 200 OK
{
  "message": "Auth service is running"
}
```

---

## 15. Future Enhancements

### 15.1 Planned Features

- **OAuth2 Integration**: Support third-party authentication
- **Two-Factor Authentication (2FA)**: SMS/Email OTP verification
- **Token Refresh**: Implement refresh token mechanism
- **Social Login**: Google, Facebook, GitHub authentication
- **Rate Limiting**: Prevent brute force attacks
- **Audit Logging**: Track all authentication attempts
- **API Versioning**: Support multiple API versions
- **OpenAPI/Swagger**: Auto-generated API documentation

### 15.2 Security Enhancements

- **HTTPS/TLS**: Encrypted communication
- **Certificate Pinning**: Prevent MITM attacks
- **IP Whitelisting**: Restrict service-to-service communication
- **API Key Management**: Alternative authentication method
- **Encrypted Storage**: Encrypt sensitive data at rest

---

## 16. Support & Contact

- **Repository**: https://github.com/singh-anshu-rajdev/auth-service
- **Issues**: Report via GitHub Issues
- **Documentation**: See README.md
- **Team**: SmartBank Elite Development Team

---

**Document Version**: 1.0  
**Last Updated**: March 1, 2026  
**Architecture Pattern**: Microservices with Spring Cloud  
**Security Level**: High (BCrypt + JWT + Spring Security)

