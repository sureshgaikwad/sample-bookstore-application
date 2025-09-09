# Troubleshooting Guide

**Author:** Suresh Gaikwad  
**Application:** Bookstore Application  
**Version:** 1.0.0

## Common Issues and Solutions

### Issue: Whitelabel Error Page

**Symptom:** When accessing the application route, you see a "Whitelabel Error Page" instead of the expected content.

**Causes and Solutions:**

#### 1. Accessing Wrong URL
**Problem:** Accessing root path (`/`) instead of API endpoints.

**Solution:** Use the correct API endpoints:

```bash
# Get application info (NEW - handles root path)
curl http://your-route-url/

# Get all books
curl http://your-route-url/api/books

# Get application health
curl http://your-route-url/actuator/health

# Search books
curl http://your-route-url/api/books/search?q=gatsby
```

#### 2. Application Not Started Properly
**Problem:** Application failed to start or is in error state.

**Solution:** Check OpenShift logs:

```bash
# Check pod status
oc get pods -n bookstore

# View application logs
oc logs -f deployment/bookstore-app -n bookstore

# Check events
oc get events -n bookstore --sort-by='.lastTimestamp'
```

#### 3. Database Connection Issues
**Problem:** H2 database initialization failed.

**Solution:** Check logs for database errors:

```bash
# Look for H2 or JPA errors in logs
oc logs deployment/bookstore-app -n bookstore | grep -i "error\|exception\|failed"
```

#### 4. Port Configuration Issues
**Problem:** Application not listening on the expected port.

**Solution:** Verify port configuration:

```bash
# Check if application is listening on port 8080
oc port-forward deployment/bookstore-app 8080:8080 -n bookstore

# Test locally
curl http://localhost:8080/api/books
```

#### 5. Profile Configuration Issues
**Problem:** Wrong Spring profile or missing configuration.

**Solution:** Check environment variables:

```bash
# Check deployment environment variables
oc describe deployment bookstore-app -n bookstore

# Verify SPRING_PROFILES_ACTIVE is set to 'openshift'
oc set env deployment/bookstore-app SPRING_PROFILES_ACTIVE=openshift -n bookstore
```

## Diagnostic Commands

### Check Application Status

```bash
# Pod status
oc get pods -l app=bookstore-app -n bookstore

# Service status
oc get svc bookstore-app -n bookstore

# Route status
oc get route bookstore-app -n bookstore

# Deployment status
oc rollout status deployment/bookstore-app -n bookstore
```

### View Logs

```bash
# Current logs
oc logs deployment/bookstore-app -n bookstore

# Follow logs
oc logs -f deployment/bookstore-app -n bookstore

# Previous container logs (if pod restarted)
oc logs deployment/bookstore-app -n bookstore --previous
```

### Test Endpoints

```bash
# Get your route URL
ROUTE_URL=$(oc get route bookstore-app -n bookstore -o jsonpath='{.spec.host}')

# Test root endpoint (should show application info)
curl -v http://$ROUTE_URL/

# Test API endpoint
curl -v http://$ROUTE_URL/api/books

# Test health endpoint
curl -v http://$ROUTE_URL/actuator/health

# Test with detailed health info
curl -v http://$ROUTE_URL/actuator/health/liveness
curl -v http://$ROUTE_URL/actuator/health/readiness
```

## Expected API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Application info and available endpoints |
| `/health` | GET | Simple health check |
| `/api/books` | GET | Get all books |
| `/api/books/{id}` | GET | Get book by ID |
| `/api/books/isbn/{isbn}` | GET | Get book by ISBN |
| `/api/books` | POST | Create new book |
| `/api/books/{id}` | PUT | Update book |
| `/api/books/{id}` | DELETE | Delete book |
| `/api/books/search?q={term}` | GET | Search books |
| `/api/books/author/{author}` | GET | Get books by author |
| `/api/books/title/{title}` | GET | Get books by title |
| `/api/books/in-stock` | GET | Get books in stock |
| `/actuator/health` | GET | Detailed health check |
| `/actuator/info` | GET | Application information |

## Sample API Calls

### Get Application Information
```bash
curl http://your-route-url/
```

**Expected Response:**
```json
{
  "application": "Bookstore Application",
  "version": "1.0.0",
  "author": "Suresh Gaikwad",
  "status": "UP",
  "message": "Welcome to the Bookstore API!",
  "endpoints": {
    "books": "/api/books",
    "health": "/actuator/health",
    "info": "/actuator/info",
    "search": "/api/books/search?q={searchTerm}",
    "by-author": "/api/books/author/{author}",
    "by-title": "/api/books/title/{title}",
    "in-stock": "/api/books/in-stock"
  },
  "examples": {
    "Get all books": "GET /api/books",
    "Get book by ID": "GET /api/books/1",
    "Search books": "GET /api/books/search?q=gatsby",
    "Create book": "POST /api/books (with JSON body)"
  }
}
```

### Get All Books
```bash
curl http://your-route-url/api/books
```

**Expected Response:**
```json
[
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
]
```

## Configuration Verification

### Check Environment Variables
```bash
oc exec deployment/bookstore-app -n bookstore -- env | grep -E "(SPRING|JAVA)"
```

### Check Application Properties
```bash
# View ConfigMap
oc get configmap bookstore-config -n bookstore -o yaml

# Check if application is using correct profile
oc logs deployment/bookstore-app -n bookstore | grep -i "active profile"
```

## Performance Issues

### Memory Issues
```bash
# Check resource usage
oc top pods -n bookstore

# Increase memory if needed
oc set resources deployment/bookstore-app --limits=memory=1Gi --requests=memory=512Mi -n bookstore
```

### Slow Response
```bash
# Check JVM settings
oc logs deployment/bookstore-app -n bookstore | grep -i "jvm\|heap\|gc"

# Scale up if needed
oc scale deployment/bookstore-app --replicas=2 -n bookstore
```

## Recovery Actions

### Restart Application
```bash
# Rolling restart
oc rollout restart deployment/bookstore-app -n bookstore

# Force restart (delete pods)
oc delete pods -l app=bookstore-app -n bookstore
```

### Reset Configuration
```bash
# Update ConfigMap and restart
oc apply -f k8s/configmap.yaml
oc rollout restart deployment/bookstore-app -n bookstore
```

### Redeploy Application
```bash
# Trigger new build
oc start-build bookstore-app -n bookstore

# Or redeploy completely
./scripts/deploy-openshift.sh
```

## Getting Help

If you're still experiencing issues:

1. **Check the logs** first - most issues are visible in the application logs
2. **Verify the URL** - make sure you're accessing `/api/books` not just `/`
3. **Test health endpoints** - `/actuator/health` should always work
4. **Check OpenShift events** - `oc get events` shows cluster-level issues

**Contact:**
- **Author**: Suresh Gaikwad
- **Email**: suresh.gaikwad@example.com

---

**Troubleshooting Guide created by Suresh Gaikwad**
