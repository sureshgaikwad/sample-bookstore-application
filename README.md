# Bookstore Application

A simple RESTful bookstore application built with Spring Boot, Maven, and H2 database.

**Author:** Suresh Gaikwad  
**Version:** 1.0.0  
**License:** MIT  
**Last Updated:** 2025-09-15 - Webhook test commit

## Features

- **Web User Interface**: Complete web UI for browsing and managing books
- **Book Management**: Create, read, update, and delete books via web interface
- **Search Functionality**: Search books by title, author, or ISBN
- **Stock Management**: Track book inventory with visual indicators
- **RESTful API**: Full REST API with proper HTTP methods
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **In-Memory Database**: H2 database for easy development and testing
- **Data Validation**: Input validation using Bean Validation
- **Sample Data**: Pre-loaded with sample books

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **Maven**
- **Bean Validation**
- **Spring Boot Actuator** (for health checks and monitoring)
- **Thymeleaf** (for web UI templates)
- **Bootstrap 5** (for responsive design)
- **Docker** (for containerization)
- **Kubernetes** (for orchestration)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone or navigate to the project directory:
   ```bash
   cd /Users/sureshgaikwad/backup/ARO/java/test-java
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. The application will start on `http://localhost:8080`

### Accessing the Application

- **Web Interface**: http://localhost:8080/web/ (or just http://localhost:8080/)
- **REST API**: http://localhost:8080/api/books
- **Health Check**: http://localhost:8080/actuator/health

### Database Access

- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:bookstore`
  - Username: `sa`
  - Password: (leave empty)

## API Endpoints

### Book Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/isbn/{isbn}` | Get book by ISBN |
| POST | `/api/books` | Create a new book |
| PUT | `/api/books/{id}` | Update an existing book |
| DELETE | `/api/books/{id}` | Delete a book |

### Search Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books/search?q={searchTerm}` | Search books by title or author |
| GET | `/api/books/author/{author}` | Get books by author |
| GET | `/api/books/title/{title}` | Get books by title |
| GET | `/api/books/in-stock` | Get books with stock > 0 |

### Stock Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/api/books/{id}/stock?quantity={quantity}` | Update stock quantity |

## Sample API Usage

### Get All Books
```bash
curl -X GET http://localhost:8080/api/books
```

### Create a New Book
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "isbn": "978-0-547-92822-7",
    "price": 15.99,
    "publicationYear": 1937,
    "description": "A fantasy adventure novel",
    "stockQuantity": 12
  }'
```

### Search Books
```bash
curl -X GET "http://localhost:8080/api/books/search?q=Gatsby"
```

### Update Stock
```bash
curl -X PATCH "http://localhost:8080/api/books/1/stock?quantity=50"
```

## Book Model

```json
{
  "id": 1,
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "isbn": "978-0-7432-7356-5",
  "price": 12.99,
  "publicationYear": 1925,
  "description": "A classic American novel set in the Jazz Age",
  "stockQuantity": 25,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

## Sample Data

The application comes pre-loaded with 5 sample books:
- The Great Gatsby by F. Scott Fitzgerald
- To Kill a Mockingbird by Harper Lee
- 1984 by George Orwell
- Pride and Prejudice by Jane Austen
- The Catcher in the Rye by J.D. Salinger

## Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/bookstore/
│   │       ├── BookstoreApplication.java
│   │       ├── config/
│   │       │   └── DataInitializer.java
│   │       ├── controller/
│   │       │   └── BookController.java
│   │       ├── model/
│   │       │   └── Book.java
│   │       ├── repository/
│   │       │   └── BookRepository.java
│   │       └── service/
│   │           └── BookService.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

### Building for Production

```bash
mvn clean package
java -jar target/bookstore-app-1.0.0.jar
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Kubernetes Deployment

This application is designed to be deployed on Kubernetes with full cloud-native support.

### Prerequisites for Kubernetes

- Docker
- kubectl
- A running Kubernetes cluster (local or cloud)

### Quick Deployment

Use the provided deployment script:

```bash
# Make scripts executable
chmod +x scripts/*.sh

# Build and deploy to Kubernetes
./scripts/build-and-deploy.sh
```

### Manual Deployment Steps

1. **Build the Docker image:**
   ```bash
   mvn clean package -DskipTests
   docker build -t bookstore-app:1.0.0 .
   ```

2. **Deploy to Kubernetes:**
   ```bash
   # Create namespace
   kubectl apply -f k8s/namespace.yaml
   
   # Apply all Kubernetes manifests
   kubectl apply -f k8s/configmap.yaml
   kubectl apply -f k8s/deployment.yaml
   kubectl apply -f k8s/service.yaml
   kubectl apply -f k8s/ingress.yaml
   kubectl apply -f k8s/hpa.yaml
   kubectl apply -f k8s/networkpolicy.yaml
   ```

3. **Verify deployment:**
   ```bash
   kubectl get pods -n bookstore
   kubectl get services -n bookstore
   ```

### Kubernetes Resources Created

- **Namespace:** `bookstore` - Isolated environment for the application
- **Deployment:** `bookstore-app` - Manages 3 replicas with health checks
- **Service:** `bookstore-service` - ClusterIP service for internal communication
- **Service:** `bookstore-nodeport` - NodePort service for external access
- **ConfigMap:** `bookstore-config` - Application configuration
- **Ingress:** `bookstore-ingress` - External access routing
- **HPA:** `bookstore-hpa` - Horizontal Pod Autoscaler (2-10 replicas)
- **NetworkPolicy:** `bookstore-network-policy` - Network security rules

### Accessing the Application in Kubernetes

- **NodePort Access:** http://localhost:30080/api/books
- **Health Check:** http://localhost:30080/actuator/health
- **Application Info:** http://localhost:30080/actuator/info
- **Metrics:** http://localhost:30080/actuator/metrics

### Ingress Access (if configured)

Add to your `/etc/hosts` file:
```
127.0.0.1 bookstore.local
127.0.0.1 bookstore-api.local
```

Then access:
- **Main API:** http://bookstore.local/api/books
- **API Only:** http://bookstore-api.local/api/books

### Monitoring and Health Checks

The application includes comprehensive health checks:

- **Liveness Probe:** `/actuator/health` - Checks if the application is running
- **Readiness Probe:** `/actuator/health` - Checks if the application is ready to serve traffic
- **Startup Probe:** Automatic with Spring Boot Actuator

### Scaling

The application supports horizontal scaling:

```bash
# Manual scaling
kubectl scale deployment bookstore-app --replicas=5 -n bookstore

# Auto-scaling is configured via HPA:
# - Min replicas: 2
# - Max replicas: 10
# - CPU threshold: 70%
# - Memory threshold: 80%
```

### Configuration

Application configuration is managed via ConfigMap. To update:

1. Edit `k8s/configmap.yaml`
2. Apply changes: `kubectl apply -f k8s/configmap.yaml`
3. Restart pods: `kubectl rollout restart deployment/bookstore-app -n bookstore`

### Cleanup

To remove all Kubernetes resources:

```bash
./scripts/cleanup.sh
```

Or manually:

```bash
kubectl delete namespace bookstore
```

## Docker Support

### Building the Docker Image

```bash
docker build -t bookstore-app:1.0.0 .
```

### Running with Docker

```bash
# Run the container
docker run -p 8080:8080 bookstore-app:1.0.0

# Run with Kubernetes profile
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=kubernetes bookstore-app:1.0.0
```

### Docker Image Features

- **Multi-stage build** for smaller image size
- **Upstream images** for maximum compatibility (Alpine & Ubuntu options)
- **Non-root user** for security
- **OpenShift compatible** with random UID support
- **Health check** included
- **Optimized JVM settings** for containers
- **Multiple image options** (see DOCKER_IMAGES.md)

## Project Structure

```
/
├── src/
│   ├── main/
│   │   ├── java/com/bookstore/
│   │   │   ├── BookstoreApplication.java
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java
│   │   │   ├── controller/
│   │   │   │   └── BookController.java
│   │   │   ├── model/
│   │   │   │   └── Book.java
│   │   │   ├── repository/
│   │   │   │   └── BookRepository.java
│   │   │   └── service/
│   │   │       └── BookService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-kubernetes.properties
├── k8s/
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── hpa.yaml
│   └── networkpolicy.yaml
├── scripts/
│   ├── build-and-deploy.sh
│   └── cleanup.sh
├── Dockerfile
├── pom.xml
└── README.md
```

## License

This project is open source and available under the MIT License.

---

**Created by Suresh Gaikwad**
# Test webhook trigger Mon Sep 15 12:26:57 IST 2025
# Test pipeline fix Mon Sep 15 12:31:00 IST 2025
# Test Quay.io authentication fix - commit 5
# Test TLS_VERIFY parameter fix - commit 6
# Test Docker config auth field fix - commit 7
