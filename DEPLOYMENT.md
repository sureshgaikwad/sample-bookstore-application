# Kubernetes Deployment Guide

**Author:** Suresh Gaikwad  
**Application:** Bookstore Application  
**Version:** 1.0.0

## Overview

This guide provides detailed instructions for deploying the Bookstore Application on Kubernetes. The application is designed with cloud-native principles and includes comprehensive monitoring, scaling, and security features.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                   │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   Pod 1     │  │   Pod 2     │  │   Pod 3     │     │
│  │ Bookstore   │  │ Bookstore   │  │ Bookstore   │     │
│  │    App      │  │    App      │  │    App      │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
│         │                │                │             │
│  ┌─────────────────────────────────────────────────────┐ │
│  │                Service                              │ │
│  │          (Load Balancer)                           │ │
│  └─────────────────────────────────────────────────────┘ │
│                         │                               │
│  ┌─────────────────────────────────────────────────────┐ │
│  │                 Ingress                            │ │
│  │          (External Access)                         │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Prerequisites

### Required Tools
- **Docker** (version 20.0+)
- **kubectl** (version 1.20+)
- **Maven** (version 3.6+)
- **Java** (version 17+)

### Kubernetes Cluster
- Local: Docker Desktop, Minikube, or Kind
- Cloud: EKS, GKE, AKS, or any managed Kubernetes service
- On-premises: Kubeadm, Rancher, or OpenShift

### Verify Prerequisites

```bash
# Check Docker
docker --version
docker info

# Check kubectl
kubectl version --client
kubectl cluster-info

# Check Maven
mvn --version

# Check Java
java --version
```

## Deployment Steps

### Step 1: Build the Application

```bash
# Navigate to project directory
cd /path/to/bookstore-app

# Clean and build
mvn clean package -DskipTests

# Verify JAR file
ls -la target/bookstore-app-1.0.0.jar
```

### Step 2: Build Docker Image

```bash
# Build the image
docker build -t bookstore-app:1.0.0 .

# Verify image
docker images | grep bookstore-app

# Test the image locally (optional)
docker run -p 8080:8080 bookstore-app:1.0.0
```

### Step 3: Deploy to Kubernetes

#### Option A: Automated Deployment

```bash
# Make script executable
chmod +x scripts/build-and-deploy.sh

# Run deployment script
./scripts/build-and-deploy.sh
```

#### Option B: Manual Deployment

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMap
kubectl apply -f k8s/configmap.yaml

# Deploy the application
kubectl apply -f k8s/deployment.yaml

# Create services
kubectl apply -f k8s/service.yaml

# Apply ingress (optional)
kubectl apply -f k8s/ingress.yaml

# Apply HPA (optional)
kubectl apply -f k8s/hpa.yaml

# Apply Network Policy (optional)
kubectl apply -f k8s/networkpolicy.yaml
```

### Step 4: Verify Deployment

```bash
# Check namespace
kubectl get namespaces

# Check pods
kubectl get pods -n bookstore

# Check services
kubectl get services -n bookstore

# Check deployment status
kubectl rollout status deployment/bookstore-app -n bookstore

# View logs
kubectl logs -f deployment/bookstore-app -n bookstore
```

## Configuration

### Environment-Specific Configuration

The application supports multiple profiles:

- **default**: Development profile with H2 console enabled
- **kubernetes**: Production profile optimized for Kubernetes

### ConfigMap Configuration

Edit `k8s/configmap.yaml` to modify application settings:

```yaml
data:
  application.properties: |
    # Your custom configuration here
    server.port=8080
    spring.datasource.url=jdbc:h2:mem:bookstore
    # ... more properties
```

Apply changes:
```bash
kubectl apply -f k8s/configmap.yaml
kubectl rollout restart deployment/bookstore-app -n bookstore
```

## Monitoring and Health Checks

### Health Endpoints

- **Liveness:** `/actuator/health/liveness`
- **Readiness:** `/actuator/health/readiness`
- **General Health:** `/actuator/health`
- **Application Info:** `/actuator/info`
- **Metrics:** `/actuator/metrics`

### Monitoring Commands

```bash
# Check pod health
kubectl get pods -n bookstore

# View detailed pod information
kubectl describe pod <pod-name> -n bookstore

# Check health endpoint
kubectl port-forward service/bookstore-service 8080:80 -n bookstore
curl http://localhost:8080/actuator/health
```

## Scaling

### Manual Scaling

```bash
# Scale to 5 replicas
kubectl scale deployment bookstore-app --replicas=5 -n bookstore

# Check scaling status
kubectl get pods -n bookstore
```

### Horizontal Pod Autoscaler (HPA)

The HPA is configured with:
- **Min replicas:** 2
- **Max replicas:** 10
- **CPU threshold:** 70%
- **Memory threshold:** 80%

```bash
# Check HPA status
kubectl get hpa -n bookstore

# View HPA details
kubectl describe hpa bookstore-hpa -n bookstore
```

## Networking

### Service Types

1. **ClusterIP** (`bookstore-service`): Internal cluster communication
2. **NodePort** (`bookstore-nodeport`): External access via node ports

### Access Methods

#### NodePort Access
```bash
# Get NodePort
kubectl get service bookstore-nodeport -n bookstore

# Access application
curl http://localhost:30080/api/books
```

#### Port Forwarding
```bash
# Forward port
kubectl port-forward service/bookstore-service 8080:80 -n bookstore

# Access application
curl http://localhost:8080/api/books
```

#### Ingress Access (if configured)
```bash
# Add to /etc/hosts
echo "127.0.0.1 bookstore.local" >> /etc/hosts

# Access via ingress
curl http://bookstore.local/api/books
```

## Security

### Network Policies

The application includes network policies that:
- Allow ingress traffic on port 8080
- Allow egress for DNS resolution
- Allow egress for HTTP/HTTPS traffic

### Pod Security

- Runs as non-root user (UID 1000)
- Read-only root filesystem (where applicable)
- Drops all capabilities
- Prevents privilege escalation

## Troubleshooting

### Common Issues

#### Pods Not Starting

```bash
# Check pod status
kubectl get pods -n bookstore

# View pod logs
kubectl logs <pod-name> -n bookstore

# Describe pod for events
kubectl describe pod <pod-name> -n bookstore
```

#### Image Pull Issues

```bash
# Check if image exists
docker images | grep bookstore-app

# For local clusters, ensure image is available
# For Minikube:
eval $(minikube docker-env)
docker build -t bookstore-app:1.0.0 .
```

#### Service Not Accessible

```bash
# Check service endpoints
kubectl get endpoints -n bookstore

# Check service configuration
kubectl describe service bookstore-service -n bookstore

# Test internal connectivity
kubectl run test-pod --image=curlimages/curl -i --tty --rm -- sh
# Inside pod: curl http://bookstore-service.bookstore.svc.cluster.local/api/books
```

### Debug Commands

```bash
# Get all resources in namespace
kubectl get all -n bookstore

# View events
kubectl get events -n bookstore --sort-by='.lastTimestamp'

# Check resource usage
kubectl top pods -n bookstore
kubectl top nodes
```

## Backup and Recovery

### Configuration Backup

```bash
# Export current configuration
kubectl get all -n bookstore -o yaml > bookstore-backup.yaml

# Export ConfigMap
kubectl get configmap bookstore-config -n bookstore -o yaml > configmap-backup.yaml
```

### Recovery

```bash
# Restore from backup
kubectl apply -f bookstore-backup.yaml
```

## Cleanup

### Automated Cleanup

```bash
./scripts/cleanup.sh
```

### Manual Cleanup

```bash
# Delete all resources in namespace
kubectl delete all --all -n bookstore

# Delete ConfigMaps
kubectl delete configmaps --all -n bookstore

# Delete namespace
kubectl delete namespace bookstore
```

## Production Considerations

### Resource Limits

Adjust resource requests and limits in `k8s/deployment.yaml`:

```yaml
resources:
  requests:
    memory: "512Mi"    # Increase for production
    cpu: "500m"        # Increase for production
  limits:
    memory: "1Gi"      # Increase for production
    cpu: "1000m"       # Increase for production
```

### Database

For production, consider:
- External database (PostgreSQL, MySQL)
- Persistent volumes for data storage
- Database connection pooling
- Backup strategies

### Monitoring

Integrate with:
- Prometheus for metrics
- Grafana for dashboards
- ELK stack for logging
- Jaeger for tracing

### Security

- Use secrets for sensitive data
- Implement RBAC
- Regular security scans
- Network segmentation
- TLS/SSL certificates

---

**Deployment Guide created by Suresh Gaikwad**  
**For questions or support, contact: suresh.gaikwad@example.com**
