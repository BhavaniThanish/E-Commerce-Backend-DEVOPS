<img width="1024" height="160" alt="image" src="https://github.com/user-attachments/assets/94b0c824-8be8-42e7-9086-c31f076d69fe" />



This module implements a secure, stateless authentication system using Spring Boot, Spring Security, and JSON Web Tokens (JWT). It handles user registration, secure credential storage in MongoDB, and token-based authorization.

🚀 Features
Stateless Authentication: Utilizes JWT to manage user sessions without server-side state.

Secure Password Storage: Implements BCryptPasswordEncoder to ensure user passwords are encrypted before persistence.

Role-Based Access Control: Configured global security filters to protect sensitive API endpoints.

Database Integration: Full CRUD support for User entities using MongoDB.

🏗️ Project Structure & Component Breakdown
The module is organized following a clean, layered architecture:

1. Model Layer (/model)
User.java: Defines the MongoDB document schema, including fields for username, email, and password.

2. Repository Layer (/repository)
UserRepository.java: Interface extending MongoRepository to handle database communication and custom queries like findByUsername.

3. Security & Utility Layer (/security)
JwtUtil.java: The core utility for:

Generating secure access tokens.

Extracting claims (username, expiration).

Validating tokens against the UserDetails.

SecurityConfig.java: Central security configuration that manages the filter chain, CSRF settings, and public vs. private route permissions.

4. Service Layer (/service)
UserService.java: Contains business logic for user registration, mapping DTOs to entities, and password encoding.

🛠️ Tech Stack
Java 21: Core language.

Spring Boot 3.x: Application framework.

Spring Security: For authentication and authorization.

MongoDB: NoSQL database for user persistence.

JWT (JJWT Library): For generating and parsing tokens.

Maven: Dependency management.

📝 How to Use
Register a User: Send a POST request to /api/auth/register with user credentials.

Login: Send a POST request to /api/auth/login. On success, the server returns a JWT.

Authorized Requests: Include the JWT in the Authorization header as a Bearer <token> for any protected routes.

👤 Contributor
<img width="1024" height="250" alt="image" src="https://github.com/user-attachments/assets/c96fcd7e-af55-4fc8-b573-38ca5cb3f0c6" />
Lead Developer for Authentication & Security Module
