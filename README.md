# Spring Security REST API - Complete Implementation

A production-ready Spring Boot REST API with comprehensive Spring Security implementation featuring JWT authentication, role-based authorization, and security best practices.

## 📋 Features

- ✅ JWT Authentication & Authorization
- ✅ Role-Based Access Control (RBAC)
- ✅ Method-Level Security with `@PreAuthorize`
- ✅ Password Encryption with BCrypt
- ✅ Refresh Token Support
- ✅ CORS Configuration
- ✅ Custom Exception Handling
- ✅ Input Validation
- ✅ JPA Auditing
- ✅ OpenAPI/Swagger Documentation
- ✅ PostgreSQL Database Integration
- ✅ Comprehensive Integration Tests
- ✅ Stateless Session Management

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (JJWT 0.12.3)**
- **Lombok**
- **Maven**
- **Springdoc OpenAPI**

## 📁 Project Structure

```
src/main/java/com/example/security/
├── config/
│   ├── SecurityConfig.java              # Main security configuration
│   ├── JwtAuthenticationEntryPoint.java # Unauthorized handler
│   ├── JpaAuditingConfig.java          # Audit configuration
│   ├── OpenApiConfig.java              # Swagger configuration
│   └── DataInitializer.java            # Database initialization
├── controller/
│   ├── AuthenticationController.java    # Auth endpoints
│   └── DemoController.java             # Sample protected endpoints
├── dto/
│   └── *.java                          # Request/Response DTOs
├── entity/
│   ├── User.java                       # User entity
│   ├── Role.java                       # Role entity
│   └── BaseEntity.java                 # Audit base entity
├── filter/
│   └── JwtAuthenticationFilter.java    # JWT filter
├── repository/
│   ├── UserRepository.java
│   └── RoleRepository.java
├── service/
│   ├── AuthenticationService.java      # Auth business logic
│   ├── CustomUserDetailsService.java   # User details service
│   └── JwtService.java                 # JWT operations
├── exception/
│   └── GlobalExceptionHandler.java     # Exception handling
└── util/
    └── SecurityUtils.java              # Security utilities
```

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE security_db;
```

2. Update `application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/security_db
    username: your_username
    password: your_password
```

### JWT Secret Key Generation

Generate a secure JWT secret key:
```bash
# Using OpenSSL
openssl rand -base64 32

# Or using Java
# Copy the generated key to application.yml
```

Update `application.yml`:
```yaml
application:
  security:
    jwt:
      secret-key: YOUR_GENERATED_SECRET_KEY
```

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run Maven build:
```bash
mvn clean install
```

4. Start the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Default Credentials

After initialization, a default admin account is created:
- **Email**: `admin@example.com`
- **Password**: `Admin@123`

⚠️ **IMPORTANT**: Change this password immediately in production!

## 📚 API Documentation

Access Swagger UI: `http://localhost:8080/swagger-ui.html`

### Authentication Endpoints

#### 1. Register New User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "username",
  "password": "Password@123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### 2. Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password@123"
}
```

#### 3. Refresh Token
```http
POST /api/v1/auth/refresh-token
Authorization: Bearer {refresh_token}
```

#### 4. Logout
```http
POST /api/v1/auth/logout
Authorization: Bearer {access_token}
```

### Protected Endpoints

#### Public Endpoint (No Auth Required)
```http
GET /api/v1/public/hello
```

#### User Endpoint (USER or ADMIN role)
```http
GET /api/v1/user/profile
Authorization: Bearer {access_token}
```

#### Admin Endpoint (ADMIN role only)
```http
GET /api/v1/admin/dashboard
Authorization: Bearer {access_token}
```

## 🔐 Security Features

### 1. Password Requirements
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character

### 2. Token Configuration
- **Access Token**: 1 hour expiration
- **Refresh Token**: 7 days expiration
- Algorithm: HS256
- Stateless session management

### 3. Role Hierarchy
- **ROLE_USER**: Basic user access
- **ROLE_MODERATOR**: Content moderation
- **ROLE_ADMIN**: Full system access

### 4. Method-Level Security Examples

```java
@PreAuthorize("hasRole('ADMIN')")
public void adminOnly() { }

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public void userOrAdmin() { }

@PreAuthorize("hasRole('ADMIN') and hasAuthority('ROLE_ADMIN')")
public void complexSecurity() { }
```

## 🧪 Testing

Run tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=SecurityIntegrationTest
```

### Test Configuration

Create `src/test/resources/application-test.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

## 📝 Usage Examples

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "Test@123456",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123456"
  }'
```

**Access Protected Resource:**
```bash
curl -X GET http://localhost:8080/api/v1/user/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Using Postman

1. Import the provided Postman collection
2. Set environment variables:
   - `baseUrl`: `http://localhost:8080`
   - `accessToken`: (auto-populated after login)
3. Test all endpoints

## 🔧 Configuration

### CORS Configuration
Update allowed origins in `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:3000",
    "http://localhost:4200",
    "https://your-domain.com"
));
```

### Token Expiration
Modify in `application.yml`:
```yaml
application:
  security:
    jwt:
      expiration: 3600000 # 1 hour
      refresh-token:
        expiration: 604800000 # 7 days
```

## 🛡️ Security Best Practices Implemented

1. ✅ Password hashing with BCrypt (strength: 12)
2. ✅ JWT token validation
3. ✅ CSRF protection disabled (stateless API)
4. ✅ CORS properly configured
5. ✅ Input validation
6. ✅ Exception handling
7. ✅ Secure headers
8. ✅ Stateless session management
9. ✅ Token expiration
10. ✅ Role-based authorization

## 🚨 Production Checklist

- [ ] Change default admin credentials
- [ ] Use strong JWT secret key (minimum 256 bits)
- [ ] Enable HTTPS/TLS
- [ ] Configure proper CORS origins
- [ ] Set up rate limiting
- [ ] Implement token blacklist for logout
- [ ] Add logging and monitoring
- [ ] Configure production database
- [ ] Set up environment variables
- [ ] Enable Spring Actuator security
- [ ] Implement account lockout mechanism
- [ ] Add email verification
- [ ] Set up backup and recovery
- [ ] Configure firewall rules

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    account_non_expired BOOLEAN DEFAULT true,
    account_non_locked BOOLEAN DEFAULT true,
    credentials_non_expired BOOLEAN DEFAULT true,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    last_login TIMESTAMP
);
```

### Roles Table
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL
);
```

### User_Roles Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

## 🐛 Troubleshooting

### Common Issues

**Issue**: "Failed to authenticate token"
- **Solution**: Ensure JWT secret key is properly configured

**Issue**: "Access Denied"
- **Solution**: Check user roles and endpoint permissions

**Issue**: "Database connection failed"
- **Solution**: Verify PostgreSQL is running and credentials are correct

## 📖 Additional Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [OWASP Security Guidelines](https://owasp.org/)

## 📄 License

This project is licensed under the Apache License 2.0

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Support

For issues and questions, please open an issue on GitHub.

---

**Built with ❤️ using Spring Boot and Spring Security**
