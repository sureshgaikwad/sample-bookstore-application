# OpenShift Deployment Guide

**Author:** Suresh Gaikwad  
**Application:** Bookstore Application  
**Version:** 1.0.0

## Overview

This guide provides step-by-step instructions for deploying the Bookstore Application on OpenShift using the "Import from Git" feature and other deployment methods.

## Prerequisites

- Access to an OpenShift cluster
- OpenShift CLI (`oc`) installed and configured
- Git repository with the application code

## Deployment Methods

### Method 1: Import from Git (Web Console) - RECOMMENDED

This is the easiest method and addresses the build issues you encountered.

#### Step 1: Access OpenShift Web Console
1. Log in to your OpenShift web console
2. Navigate to the **Developer** perspective
3. Click **+Add** from the left sidebar

#### Step 2: Import from Git
1. Select **Import from Git**
2. Enter your Git repository URL
3. OpenShift will automatically detect it's a Java application

#### Step 3: Configure Build
1. **Builder Image**: Select `java:17` or `java:openjdk-17` (upstream Java image)
2. **Application Name**: `bookstore-app`
3. **Name**: `bookstore-app`
4. **Resources**: Select **Deployment**
5. **Create a route**: Check this option

#### Step 4: Advanced Options
Click **Show advanced Git options** and configure:
- **Git Reference**: `main` (or your branch name)
- **Context Dir**: Leave empty (if app is in root)

Click **Show advanced Routing options** and configure:
- **Hostname**: Leave empty for auto-generation
- **Path**: Leave empty
- **Target Port**: `8080`

#### Step 5: Environment Variables
In the **Advanced options** section, add:
- `SPRING_PROFILES_ACTIVE` = `openshift`

#### Step 6: Resource Limits
Set resource limits:
- **CPU Request**: `250m`
- **CPU Limit**: `500m`
- **Memory Request**: `256Mi`
- **Memory Limit**: `512Mi`

#### Step 7: Deploy
Click **Create** to start the deployment.

### Method 2: Command Line Deployment

```bash
# Login to OpenShift
oc login --server=<your-openshift-server>

# Create new project
oc new-project bookstore --display-name="Bookstore by Suresh Gaikwad"

# Create application using S2I
oc new-app java:17~<your-git-repo-url> \
    --name=bookstore-app \
    --env SPRING_PROFILES_ACTIVE=openshift

# Expose the service
oc expose svc/bookstore-app

# Set resource limits
oc set resources dc/bookstore-app \
    --requests=memory=256Mi,cpu=250m \
    --limits=memory=512Mi,cpu=500m

# Configure health checks
oc set probe dc/bookstore-app \
    --liveness \
    --get-url=http://:8080/actuator/health/liveness \
    --initial-delay-seconds=60

oc set probe dc/bookstore-app \
    --readiness \
    --get-url=http://:8080/actuator/health/readiness \
    --initial-delay-seconds=30
```

### Method 3: Using Deployment Script

Use the provided script:

```bash
./scripts/deploy-openshift.sh <your-git-repo-url> <project-name>
```

## Troubleshooting Build Issues

### Issue: Maven Image Not Found
**Problem**: The original Dockerfile used `maven:3.9-openjdk-17-slim` which doesn't exist.

**Solution**: Updated Dockerfile to use upstream images for better compatibility:
- Builder: `maven:3.9.4-eclipse-temurin-17-alpine`
- Runtime: `eclipse-temurin:17-jre-alpine`

Alternative Dockerfile (`Dockerfile.ubuntu`) uses:
- Builder: `maven:3.9.4-openjdk-17-slim`
- Runtime: `openjdk:17-jre-slim`

### Issue: S2I Build Failing
**Problem**: Source-to-Image build might fail with Maven dependencies.

**Solutions**:
1. Use the `.s2i/environment` file to set Maven options
2. Ensure `pom.xml` is in the repository root
3. Use the correct builder image: `java:17` or `java:openjdk-17`

### Issue: Permission Denied
**Problem**: OpenShift runs containers with random UIDs.

**Solution**: The updated Dockerfile sets proper permissions:
```dockerfile
RUN chmod -R g+w /deployments && \
    chmod g+x /deployments
```

## Configuration Files

### `.s2i/environment`
Contains S2I build configuration:
```
MAVEN_ARGS=clean package -DskipTests -B
MAVEN_OPTS=-Xmx1024m
SPRING_PROFILES_ACTIVE=openshift
```

### `application-openshift.properties`
OpenShift-specific configuration with:
- Disabled H2 console
- Proper logging configuration
- Health check endpoints enabled
- OpenShift-specific settings

## Monitoring and Health Checks

### Health Endpoints
- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`
- **General Health**: `/actuator/health`
- **Application Info**: `/actuator/info`

### Viewing Application Status

```bash
# Check pod status
oc get pods

# View logs
oc logs -f dc/bookstore-app

# Check route
oc get route bookstore-app

# Monitor deployment
oc rollout status dc/bookstore-app
```

## Scaling

```bash
# Manual scaling
oc scale dc/bookstore-app --replicas=3

# Auto-scaling (HPA)
oc autoscale dc/bookstore-app --min=2 --max=5 --cpu-percent=70
```

## Accessing the Application

### Get Application URL
```bash
# Get route URL
oc get route bookstore-app -o jsonpath='{.spec.host}'
```

### Test Endpoints
```bash
# Replace <route-url> with your actual route URL
curl http://<route-url>/api/books
curl http://<route-url>/actuator/health
curl http://<route-url>/actuator/info
```

## Security Considerations

### OpenShift Security Context Constraints (SCC)
The application is designed to work with OpenShift's default `restricted` SCC:
- Runs with random UID
- No root privileges required
- Proper file permissions set

### Network Policies
OpenShift provides built-in network isolation between projects.

## Cleanup

### Delete Application
```bash
# Delete all resources
oc delete all -l app=bookstore-app

# Delete project
oc delete project bookstore
```

## Best Practices for OpenShift

1. **Use Red Hat UBI Images**: More secure and supported
2. **Set Resource Limits**: Prevent resource starvation
3. **Configure Health Checks**: Enable proper lifecycle management
4. **Use ConfigMaps/Secrets**: For configuration management
5. **Enable Monitoring**: Use OpenShift's built-in monitoring
6. **Follow Security Guidelines**: Use minimal privileges

## Common Issues and Solutions

### Build Timeout
If the build times out, increase the build timeout:
```bash
oc patch bc/bookstore-app -p '{"spec":{"completionDeadlineSeconds":1200}}'
```

### Memory Issues
If pods are killed due to memory, increase limits:
```bash
oc set resources dc/bookstore-app --limits=memory=1Gi
```

### Slow Startup
If health checks fail due to slow startup, increase initial delay:
```bash
oc set probe dc/bookstore-app --liveness --initial-delay-seconds=120
```

## Support

For issues or questions:
- **Author**: Suresh Gaikwad
- **Email**: suresh.gaikwad@example.com

## Additional Resources

- [OpenShift Documentation](https://docs.openshift.com/)
- [Source-to-Image (S2I)](https://docs.openshift.com/container-platform/4.12/builds/understanding-image-builds.html)
- [Java on OpenShift](https://docs.openshift.com/container-platform/4.12/openshift_images/using_images/using-s21-images.html)

---

**OpenShift Deployment Guide created by Suresh Gaikwad**
