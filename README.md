# 🛒 E-Commerce Backend — DevOps Edition

A production-ready **Spring Boot REST API** for an e-commerce platform, featuring JWT-based authentication, MongoDB persistence, Dockerized deployment, Kubernetes orchestration, and a fully automated GitHub Actions CI/CD pipeline.

---

## 📌 Table of Contents

- [Tech Stack](#-tech-stack)
- [Core Concepts Used](#-core-concepts-used)
- [Project Structure](#-project-structure)
- [Source File Flow](#-source-file-flow)
- [API Endpoints](#-api-endpoints)
- [DevOps Pipeline](#-devops-pipeline)
- [Environment Setup](#-environment-setup)
- [Running Locally](#-running-locally)
- [Docker](#-docker)
- [Kubernetes](#-kubernetes)
- [Team Contributions](#-team-contributions)

---

## 🧰 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.4.5 |
| Database | MongoDB Atlas | Cloud |
| Security | Spring Security + JWT (JJWT) | 0.12.6 |
| Validation | Jakarta Bean Validation | Built-in |
| API Docs | Springdoc OpenAPI (Swagger UI) | 2.8.8 |
| Build Tool | Apache Maven | 3.x |
| Containerization | Docker | Multi-stage build |
| Orchestration | Kubernetes (kubectl) | k8s manifests |
| CI/CD | GitHub Actions | Automated pipeline |

---

## 🧠 Core Concepts Used

### 1. 🔐 JWT Authentication (Stateless Security)
- **JSON Web Tokens (JWT)** are used for stateless authentication — no server-side sessions.
- On login, the server signs a token with a secret key using **HMAC-SHA** algorithm.
- Every subsequent request must carry this token in the `Authorization: Bearer <token>` header.
- The `JwtAuthenticationFilter` intercepts every request and validates the token before allowing access.

### 2. 🏗️ Layered Architecture (MVC Pattern)
The project follows a strict **Controller → Service → Repository** separation:
- **Controller** — Receives HTTP requests, validates input, returns responses
- **Service** — Contains all business logic
- **Repository** — Interfaces with MongoDB using Spring Data

### 3. 📦 DTO Pattern (Data Transfer Objects)
- `ProductDto` is used as the request/response contract for the API.
- The internal `Product` model (database entity) is never directly exposed to clients.
- Manual mapping between DTO ↔ Entity is handled inside the controller.

### 4. 🔑 Spring Security Filter Chain
- CSRF is disabled (stateless REST API doesn't need it).
- Session management is set to `STATELESS`.
- Public routes: `POST /api/auth/**` and `GET /api/products/**`
- Protected routes: `POST/PUT/DELETE /api/products/**`

### 5. 🐳 Containerization with Docker
- Multi-stage Dockerfile: **Build stage** (Maven) → **Run stage** (JRE only).
- Final image is lightweight — only the compiled JAR runs in JRE 21.

### 6. ☸️ Kubernetes Orchestration
- `deployment.yaml` — Defines the pod spec, resource limits, and secret bindings.
- `service.yaml` — Exposes the deployment as a `LoadBalancer` service on port 80 → 8080.
- Secrets (`MONGODB_URI`, `JWT_SECRET`) are injected via Kubernetes Secrets, not hardcoded.

### 7. ⚙️ GitHub Actions CI/CD Pipeline
Three-stage automated pipeline:
1. **Build & Test** — Runs on every push/PR to any branch
2. **Docker Build & Push** — Runs only on `develop` and `main` branches
3. **Deploy to Kubernetes** — Runs only on `main` branch

---

## 📁 Project Structure

```
ecommerce-backend/
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml               # GitHub Actions CI/CD pipeline
│
├── k8s/
│   ├── deployment.yaml             # Kubernetes Deployment manifest
│   └── service.yaml                # Kubernetes Service (LoadBalancer)
│
├── src/main/java/com/example/ecommerce/
│   │
│   ├── EcommerceApplication.java   # Spring Boot entry point (@SpringBootApplication)
│   │
│   ├── controller/
│   │   └── ProductController.java  # REST endpoints for products
│   │
│   ├── dto/
│   │   └── ProductDto.java         # Request/Response DTO with validation annotations
│   │
│   ├── model/
│   │   ├── Product.java            # MongoDB document — products collection
│   │   └── User.java               # MongoDB document — users collection
│   │
│   ├── repository/
│   │   ├── ProductRepository.java  # Spring Data MongoDB interface for Product
│   │   └── UserRepository.java     # Spring Data MongoDB interface for User
│   │
│   ├── security/
│   │   ├── JwtUtil.java                  # Token generation, parsing, validation
│   │   ├── JwtAuthenticationFilter.java  # Per-request JWT validation filter
│   │   └── SecurityConfig.java           # Spring Security config, CORS, filter chain
│   │
│   └── service/
│       ├── ProductService.java           # Product business logic (CRUD)
│       ├── UserService.java              # User registration logic
│       └── CustomUserDetailsService.java # Loads user from DB for Spring Security
│
├── .env.example                    # Template for environment variables
├── .gitignore                      # Git ignore rules
├── Dockerfile                      # Multi-stage Docker build
└── pom.xml                         # Maven dependencies and build config
```

---

## 🔄 Source File Flow

### 🔐 Authentication Flow

```
Client (POST /api/auth/login)
    │
    ▼
AuthController  ──calls──▶  UserService  ──calls──▶  UserRepository (MongoDB)
    │                            │
    │                      PasswordEncoder
    │                      (BCrypt verify)
    │
    ▼
JwtUtil.generateToken(username)
    │
    ▼
Response: { "token": "eyJ..." }
```

---

### 🛍️ Product API Flow (with Auth)

```
Client (GET/POST/PUT/DELETE /api/products)
    │
    ▼
JwtAuthenticationFilter
    ├── Reads "Authorization: Bearer <token>" header
    ├── JwtUtil.getUsernameFromToken(token)
    ├── JwtUtil.validateToken(token)
    └── Sets SecurityContext authentication
    │
    ▼
SecurityConfig (checks route permissions)
    │
    ▼
ProductController
    ├── Validates @RequestBody using ProductDto (Bean Validation)
    ├── Converts DTO → Entity (toEntity method)
    │
    ▼
ProductService
    ├── findAll()   → productRepository.findAll()
    ├── findById()  → productRepository.findById()
    ├── save()      → productRepository.save()
    └── delete()    → productRepository.deleteById()
    │
    ▼
ProductRepository (MongoRepository<Product, String>)
    │
    ▼
MongoDB Atlas (products collection)
    │
    ▼
ProductController converts Entity → DTO
    │
    ▼
ResponseEntity<ProductDto> sent back to Client
```

---

### 🔒 Security Filter Chain Order

```
Incoming HTTP Request
    │
    ▼
[1] JwtAuthenticationFilter (OncePerRequestFilter)
    │   Extracts & validates JWT from Authorization header
    ▼
[2] UsernamePasswordAuthenticationFilter
    │   (bypassed for JWT-based auth)
    ▼
[3] SecurityFilterChain rules (SecurityConfig)
    │   /api/auth/**         → permitAll
    │   GET /api/products/** → permitAll
    │   POST/PUT/DELETE /api/products/** → authenticated
    ▼
[4] Controller Method Execution
```

---

## 📡 API Endpoints

### Auth Endpoints (`/api/auth`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Login and receive JWT token |

#### Register Request Body:
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

#### Login Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Product Endpoints (`/api/products`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/products` | Public | Get all products |
| `GET` | `/api/products/{id}` | Public | Get product by ID |
| `POST` | `/api/products` | 🔐 JWT Required | Create a new product |
| `PUT` | `/api/products/{id}` | 🔐 JWT Required | Update a product |
| `DELETE` | `/api/products/{id}` | 🔐 JWT Required | Delete a product |

#### Product Request Body:
```json
{
  "name": "Wireless Headphones",
  "description": "Noise-cancelling Bluetooth headphones",
  "price": 2999.99,
  "stock": 50,
  "imageUrl": "https://example.com/headphones.jpg",
  "category": "Electronics"
}
```

#### Protected Endpoint Header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### Swagger UI
Once the app is running, visit:
```
http://localhost:8080/swagger-ui/index.html
```

---

## ⚙️ DevOps Pipeline

### GitHub Actions — 3-Stage CI/CD

```
Push to any branch / Pull Request
          │
          ▼
┌─────────────────────────┐
│  Stage 1: Build & Test  │  ← Runs on ALL branches
│  • Checkout code         │
│  • Setup JDK 21          │
│  • mvn clean package     │
│  • mvn test              │
│  • Upload JAR artifact   │
└─────────────┬───────────┘
              │ (on develop or main only)
              ▼
┌──────────────────────────────┐
│  Stage 2: Docker Build&Push  │
│  • Build JAR                  │
│  • Login to Docker Hub        │
│  • Tag: latest / develop      │
│  • Push image to Docker Hub   │
└──────────────┬───────────────┘
               │ (on main only)
               ▼
┌──────────────────────────────┐
│  Stage 3: Deploy to K8s      │
│  • Install kubectl            │
│  • Configure kubeconfig       │
│  • kubectl apply -f k8s/      │
│  • Verify rollout status      │
└──────────────────────────────┘
```

### Branching Strategy (GitFlow)
| Branch | Purpose |
|---|---|
| `main` | Production — triggers full deploy |
| `develop` | Integration — triggers Docker build |
| `feature/*` | Feature work — triggers build & test only |

---

## 🔧 Environment Setup

1. Copy the environment template:
```bash
cp .env.example .env
```

2. Fill in your actual values in `.env`:
```env
MONGODB_URI=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/ecommercedb
JWT_SECRET=your-super-secret-jwt-key-at-least-64-characters-long
JWT_EXPIRATION=86400000
PORT=8080
```

3. Add GitHub Secrets (for CI/CD):
   - `MONGODB_URI` — Your MongoDB Atlas connection string
   - `JWT_SECRET` — Your JWT signing secret
   - `DOCKERHUB_USERNAME` — Your Docker Hub username
   - `DOCKERHUB_TOKEN` — Your Docker Hub access token
   - `KUBE_CONFIG_DATA` — Base64 encoded kubeconfig (for K8s deploy)

---

## 🚀 Running Locally

**Prerequisites:** Java 21, Maven, MongoDB Atlas account

```bash
# Clone the repository
git clone https://github.com/BhavaniThanish/E-Commerce-Backend-DEVOPS.git
cd E-Commerce-Backend-DEVOPS

# Set environment variables
export MONGODB_URI="your-mongodb-uri"
export JWT_SECRET="your-jwt-secret"
export JWT_EXPIRATION=86400000

# Build and run
mvn clean package -DskipTests
java -jar target/ecommerce-backend-1.0.0.jar

# Or run directly with Maven
mvn spring-boot:run
```

App starts at: `http://localhost:8080`

---

## 🐳 Docker

```bash
# Build the image
docker build -t ecommerce-backend:latest .

# Run the container
docker run -p 8080:8080 \
  -e MONGODB_URI="your-mongodb-uri" \
  -e JWT_SECRET="your-jwt-secret" \
  -e JWT_EXPIRATION=86400000 \
  ecommerce-backend:latest
```

### Multi-Stage Dockerfile Summary:
| Stage | Base Image | Purpose |
|---|---|---|
| Stage 1 (build) | `maven:3.9.6-eclipse-temurin-21` | Compiles the JAR |
| Stage 2 (run) | `eclipse-temurin:21-jre-alpine` | Runs the lightweight JAR |

---

## ☸️ Kubernetes

```bash
# Create secrets first
kubectl create secret generic ecommerce-secrets \
  --from-literal=MONGODB_URI="your-mongodb-uri" \
  --from-literal=JWT_SECRET="your-jwt-secret"

# Deploy to Kubernetes
kubectl apply -f k8s/

# Check deployment status
kubectl rollout status deployment/ecommerce-backend

# Get service external IP
kubectl get service ecommerce-backend
```

### K8s Resource Summary:
| Resource | File | Details |
|---|---|---|
| Deployment | `k8s/deployment.yaml` | 1 replica, 256Mi–512Mi RAM, 250m–500m CPU |
| Service | `k8s/service.yaml` | LoadBalancer, port 80 → 8080 |

---

## 👥 Team Contributions

| Member | Module | Files |
|---|---|---|
| **BhavaniThanish** | Authentication & Security | `JwtUtil.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java`, `UserService.java`, `CustomUserDetailsService.java`, `User.java`, `UserRepository.java` |
| **BhavaniThanish** | Product API | `ProductController.java`, `ProductService.java`, `ProductRepository.java`, `Product.java`, `ProductDto.java` |
| **BhavaniThanish** | DevOps & CI/CD | `Dockerfile`, `k8s/deployment.yaml`, `k8s/service.yaml`, `.github/workflows/ci-cd.yml`, `.env.example` |

---

## 📄 License

This project is built for educational purposes as part of a DevOps-focused backend development course.