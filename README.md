# Auth Service - SmartBank Elite System

## Overview

The **Auth Service** is a critical microservice in the SmartBank Elite System responsible for user authentication, authorization, and JWT token generation. It serves as the security gateway for all other microservices in the system including Account Service, Transaction Service, and other dependent services.

## Features

✅ **User Authentication** - Secure login and registration for bank customers
✅ **JWT Token Generation** - Industry-standard token-based authentication
✅ **Spring Security Integration** - Comprehensive security configurations
✅ **Eureka Service Discovery** - Auto-registration and discovery in microservice ecosystem
✅ **JWT Authentication Filter** - Validates incoming requests from other services
✅ **User Management** - User registration and credential management
✅ **Exception Handling** - Global exception handling with meaningful error messages
✅ **Database Persistence** - MySQL database for user data storage

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     API GATEWAY (Port 8080)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│     ┌──────────────────────────────────────────────────────┐   │
│     │         Auth Service (Port 8085)                     │   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────┐        │   │
│     │  │      Authentication Controller           │        │   │
│     │  │  • POST /unsecure/signup                │        │   │
│     │  │  • POST /unsecure/login                 │        │   │
│     │  └─────────────────────────────────────────┘        │   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────┐        │   │
│     │  │   Authentication Service                │        │   │
│     │  │  • User Registration                    │        │   │
│     │  │  • User Authentication                  │        │   │
│     │  │  • Credential Validation                │        │   │
│     │  └─────────────────────────────────────────┘        │   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────┐        │   │
│     │  │   JWT Service                           │        │   │
│     │  │  • Token Generation                     │        │   │
│     │  │  • Token Validation                     │        │   │
│     │  │  • Token Extraction                     │        │   │
│     │  └─────────────────────────────────────────┘        │   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────┐        │   │
│     │  │   JWT Authentication Filter             │        │   │
│     │  │  • Request Interception                 │        │   │
│     │  │  • Token Validation                     │        │   │
│     │  │  • User Context Setting                 │        │   │
│     │  └─────────────────────────────────────────┘        │   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────┐        │   │
│     │  │   MySQL Database (authdb)               │        │   │
│     │  │  • User Credentials                     │        │   │
│     │  │  • User Information                     │        │   │
│     │  └─────────────────────────────────────────┘        │   │
│     │                                                      │   │
│     └──────────────────────────────────────────────────────┘   │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐ │
│  │  Account Service │  │ Transaction Srv  │  │Other Services│ │
│  │   (Port 8086)    │  │   (Port 8087)    │  │              │ │
│  └──────────────────┘  └──────────────────┘  └──────────────┘ │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│          Eureka Service Registry (Port 8761)                    │
└─────────────────────────────────────────────────────────────────┘
```

## Service Interactions

### 1. **Auth Service ↔ API Gateway**
- Auth Service registers with Eureka and is discovered by the API Gateway
- Gateway routes `/auth/*` requests to the Auth Service
- All authentication requests pass through the gateway

### 2. **Auth Service ↔ Account Service (Port 8086)**
- Account Service receives JWT tokens from Auth Service
- Uses JWT tokens to validate user identity for account operations
- Validates tokens using the JWT secret shared with Auth Service

### 3. **Auth Service ↔ Transaction Service (Port 8087)**
- Transaction Service validates JWT tokens for transaction authorization
- Ensures user identity is verified before processing transactions
- Token validation prevents unauthorized access

### 4. **Auth Service ↔ Eureka Service Registry (Port 8761)**
- Auto-registration of Auth Service on startup
- Other services discover Auth Service through Eureka
- Enables dynamic service discovery and load balancing

### 5. **Auth Service ↔ MySQL Database**
- Stores user credentials and profile information
- Persists authentication data for user verification
- Uses JPA/Hibernate for ORM

## API Endpoints

### Authentication Endpoints

#### 1. User Registration
```http
POST /unsecure/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "createdAt": "2026-03-01T10:30:00Z"
}
```

#### 2. User Login
```http
POST /unsecure/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

#### 3. Test Controller (Health Check)
```http
GET /secure/test
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "message": "Auth service is running"
}
```

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.5.11 | Application Framework |
| Spring Security | Latest | Authentication & Authorization |
| Spring Cloud | 2025.0.1 | Microservices Integration |
| JWT (jjwt) | 0.11.5 | Token Generation & Validation |
| Spring Data JPA | Latest | Database ORM |
| MySQL | 8.0+ | Primary Database |
| Eureka Client | Latest | Service Discovery |
| Lombok | Latest | Code Generation |
| Java | 21 | Programming Language |

## Project Structure

```
auth-server/
├── src/main/java/com/smartBankElite/authserver/
│   ├── AuthServerApplication.java          # Application Entry Point
│   ├── Configuration/
│   │   ├── ApplicationConfiguration.java   # Bean Configurations
│   │   ├── SecurityConfiguration.java      # Spring Security Setup
│   │   └── JwtAuthenticationFilter.java    # JWT Validation Filter
│   ├── Controller/
│   │   ├── AuthenticationController.java   # Auth Endpoints
│   │   └── TestController.java             # Health Check Endpoints
│   ├── Service/
│   │   ├── AuthenticationService.java      # Auth Business Logic Interface
│   │   └── JwtService.java                 # JWT Business Logic Interface
│   ├── ServiceImpl/
│   │   ├── AuthenticationServiceImpl.java   # Auth Implementation
│   │   └── JwtServiceImpl.java              # JWT Implementation
│   ├── DTO/
│   │   ├── LoginResponseDTO.java           # Login Response Model
│   │   ├── LoginUserDto.java               # Login Request Model
│   │   ├── RegisterUserDto.java            # Registration Request Model
│   │   └── CacheDTO.java                   # Cache Data Model
│   ├── Model/
│   │   └── User.java                       # User Entity
│   ├── Repositories/
│   │   └── UserRepository.java             # User Data Access
│   ├── ExceptionHandler/
│   │   └── GlobalExceptionHandler.java     # Exception Handling
│   └── Utils/
│       └── SmartBankEliteConstants.java    # Application Constants
├── src/main/resources/
│   └── application.properties               # Configuration Properties
├── pom.xml                                 # Maven Dependencies
└── README.md                               # This File
```

## Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/authdb
spring.datasource.username=root
spring.datasource.password=Anshu@123
spring.jpa.hibernate.ddl-auto=update
```

### JWT Configuration
```properties
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
security.jwt.expiration-time=3600000  # 1 hour in milliseconds
```

### Eureka Configuration
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
```

### Server Configuration
```properties
spring.application.name=auth-server
server.port=8085
```

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Eureka Server** running on port 8761
- **API Gateway** running on port 8080

## Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/singh-anshu-rajdev/auth-service.git
cd auth-server
```

### 2. Configure Database
Create MySQL database:
```sql
CREATE DATABASE authdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Update Configuration
Edit `src/main/resources/application.properties` with your configuration:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/authdb
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password
security.jwt.secret-key=your_secret_key_here
eureka.client.service-url.defaultZone=http://eureka-host:8761/eureka
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

Or using Java directly:
```bash
java -jar target/auth-server-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:8085`

## Security Features

### 1. **Password Encoding**
- Passwords are encoded using BCrypt
- Stored securely in the database
- Validated during authentication

### 2. **JWT Token Security**
- HS256 algorithm for token signing
- 1-hour expiration time (configurable)
- Secret key stored in secure properties

### 3. **Spring Security**
- Endpoint-level security configuration
- Role-based access control
- CORS configuration for cross-origin requests

### 4. **JWT Authentication Filter**
- Validates JWT tokens on incoming requests
- Sets user context for secured endpoints
- Handles token extraction and validation

## Error Handling

The service includes comprehensive exception handling:

| Exception | Status Code | Message |
|-----------|-------------|---------|
| Invalid Credentials | 401 | User not found or invalid password |
| User Already Exists | 409 | Email already registered |
| Invalid Token | 401 | Token is invalid or expired |
| Missing Token | 401 | Authorization header missing |
| Server Error | 500 | Internal server error |

## Testing

### Using Postman

1. **Sign Up**
   - Method: `POST`
   - URL: `http://localhost:8085/unsecure/signup`
   - Body:
     ```json
     {
       "email": "testuser@bank.com",
       "password": "Test@1234"
     }
     ```

2. **Login**
   - Method: `POST`
   - URL: `http://localhost:8085/unsecure/login`
   - Body:
     ```json
     {
       "email": "testuser@bank.com",
       "password": "Test@1234"
     }
     ```

3. **Test Secured Endpoint**
   - Method: `GET`
   - URL: `http://localhost:8085/secure/test`
   - Header: `Authorization: Bearer <token_from_login>`

## Integration with SmartBank Elite System

### Account Service Integration
- Account Service queries Auth Service for user validation
- Uses JWT tokens to identify logged-in users
- Token validation prevents unauthorized account operations

### Transaction Service Integration
- Transaction Service requires valid JWT tokens
- Validates transaction requests against authenticated users
- Ensures all transactions are authorized

### Gateway Integration
- All requests to microservices route through API Gateway
- Gateway validates JWT tokens using Auth Service
- Provides centralized authentication for the entire system

### Eureka Registration
```
┌─────────────────────────────┐
│   Eureka Service Registry   │
│  (Service Discovery)        │
│                             │
│  auth-server:8085          │
│  account-server:8086       │
│  transaction-server:8087   │
│  gateway:8080              │
└─────────────────────────────┘
```

## Development Workflow

### Adding New Endpoints
1. Create a new method in `AuthenticationController`
2. Implement business logic in `AuthenticationServiceImpl`
3. Add appropriate security annotations
4. Update API documentation

### Adding New Features
1. Modify relevant service interface and implementation
2. Update DTOs if new request/response models needed
3. Add exception handling for new error cases
4. Update test coverage

## Performance Considerations

- **JWT Token Caching**: Tokens are validated on each request
- **Database Connection Pooling**: Configured for optimal performance
- **Lazy Loading**: Relationships are lazily loaded
- **Stateless Authentication**: JWT tokens eliminate session storage

## Logging

The service implements comprehensive logging:
```properties
logging.level.org.springframework.security=INFO
```

All authentication attempts are logged for audit purposes.

## Monitoring & Metrics

- **Eureka Dashboard**: Monitor service health at `http://localhost:8761`
- **Application Logs**: Check logs for authentication events
- **Database Metrics**: Monitor active connections and query performance

## Troubleshooting

### Service Not Registering with Eureka
- Check if Eureka Server is running on port 8761
- Verify `eureka.client.register-with-eureka=true` in properties
- Check network connectivity

### Database Connection Issues
- Verify MySQL is running on localhost:3306
- Check credentials in `application.properties`
- Ensure `authdb` database exists

### JWT Token Validation Fails
- Verify secret key matches across services
- Check token expiration time
- Ensure token is not corrupted

### CORS Issues
- Check CORS configuration in `SecurityConfiguration`
- Verify allowed origins match client application

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is part of the SmartBank Elite System. Contact the development team for licensing information.

## Support & Contact

For issues, questions, or contributions:
- **Repository**: https://github.com/singh-anshu-rajdev/auth-service
- **Issues**: Report bugs via GitHub Issues
- **Email**: singh.anshu.rajdev@gmail.com

## Roadmap

- [ ] OAuth2 Integration
- [ ] Two-Factor Authentication (2FA)
- [ ] Social Login Integration
- [ ] Token Refresh Mechanism
- [ ] Audit Logging
- [ ] Rate Limiting
- [ ] API Versioning
- [ ] Swagger/OpenAPI Documentation

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: March 1, 2026  
**Maintained By**: SmartBank Elite Development Team

