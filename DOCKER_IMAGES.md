# Docker Image Choices for OpenShift Compatibility

**Author:** Suresh Gaikwad  
**Application:** Bookstore Application  
**Version:** 1.0.0

## Overview

This document explains the Docker image choices made to ensure compatibility with OpenShift and address the build issues encountered with the "Import from Git" feature.

## Image Options

### Option 1: Alpine-based (Default - Dockerfile)
**Builder:** `maven:3.9.4-eclipse-temurin-17-alpine`  
**Runtime:** `eclipse-temurin:17-jre-alpine`

**Advantages:**
- Smaller image size (~150MB vs ~300MB)
- Faster download and startup
- Eclipse Temurin is the official OpenJDK distribution
- Alpine Linux is widely used in containers

**Considerations:**
- Uses musl libc instead of glibc (rarely an issue for Java apps)
- Some native libraries might need special handling

### Option 2: Ubuntu-based (Alternative - Dockerfile.ubuntu)
**Builder:** `maven:3.9.4-openjdk-17-slim`  
**Runtime:** `openjdk:17-jre-slim`

**Advantages:**
- Uses standard glibc (maximum compatibility)
- Debian/Ubuntu base is very common
- Official OpenJDK images
- Better compatibility with some enterprise tools

**Considerations:**
- Larger image size
- Longer download times

## Why These Images?

### Previous Issue
The original Dockerfile used `maven:3.9-openjdk-17-slim` which doesn't exist in Docker Hub or other registries, causing the build failures you experienced.

### Solution
Both new options use **verified, upstream images** that are:
1. **Available in Docker Hub** - No registry access issues
2. **Officially maintained** - Regular security updates
3. **OpenShift compatible** - Work with random UIDs
4. **Production ready** - Used by thousands of applications

## OpenShift S2I Recommendation

For OpenShift's "Import from Git" feature, use:
- **Builder Image:** `java:17` or `java:openjdk-17`
- These are OpenShift's built-in Java builders that handle Maven automatically

## File Structure

```
├── Dockerfile              # Alpine-based (recommended)
├── Dockerfile.ubuntu       # Ubuntu-based (alternative)
├── .s2i/environment        # S2I build configuration
└── openshift/deployment.yaml # OpenShift template
```

## Usage Instructions

### For Docker Build
```bash
# Use Alpine version (default)
docker build -t bookstore-app:1.0.0 .

# Use Ubuntu version
docker build -f Dockerfile.ubuntu -t bookstore-app:1.0.0 .
```

### For OpenShift Import from Git
1. Use the repository URL in OpenShift console
2. Select `java:17` as builder image
3. OpenShift will automatically detect and build the Maven project

### For OpenShift CLI
```bash
# Using S2I with upstream Java builder
oc new-app java:17~<your-git-repo-url> --name=bookstore-app
```

## Security Features

Both Dockerfiles include:
- Non-root user execution
- Proper file permissions for OpenShift random UIDs
- Minimal package installation
- Security labels and metadata

## Performance Optimizations

JVM flags included:
- `-XX:+UseContainerSupport` - Container-aware JVM
- `-XX:MaxRAMPercentage=75.0` - Use 75% of container memory
- `-XX:+UseG1GC` - G1 garbage collector for better performance
- `-XX:+UseStringDeduplication` - Reduce memory usage

## Testing

Both images have been designed to work with:
- Local Docker environments
- OpenShift 4.x clusters
- Kubernetes clusters
- CI/CD pipelines

## Troubleshooting

If you still encounter issues:

1. **Check available builder images:**
   ```bash
   oc get imagestreams -n openshift | grep java
   ```

2. **Use specific image version:**
   ```bash
   oc new-app java:openjdk-17-ubi8~<repo-url>
   ```

3. **Check build logs:**
   ```bash
   oc logs -f bc/bookstore-app
   ```

## Recommendations

- **For Production:** Use the Alpine version (Dockerfile) for smaller footprint
- **For Enterprise:** Use the Ubuntu version (Dockerfile.ubuntu) for maximum compatibility
- **For OpenShift S2I:** Let OpenShift handle the build with `java:17` builder

---

**Docker Images Guide created by Suresh Gaikwad**
