# GitOps Integration Setup

The CI/CD pipeline has been successfully integrated with OpenShift GitOps (ArgoCD). The pipeline now updates the GitOps repository instead of directly deploying to OpenShift.

## Architecture

1. **Source Repository**: https://github.com/sureshgaikwad/sample-bookstore-application.git  
   - Contains application source code
   - Triggers CI/CD pipeline via GitHub webhook

2. **GitOps Repository**: https://github.com/sureshgaikwad/gitops-catalog.git  
   - Contains deployment manifests in `application/book-app/`
   - Updated by CI/CD pipeline with new image references
   - Monitored by ArgoCD for automatic deployment

3. **CI/CD Pipeline Flow**:
   - Code push → GitHub webhook → Tekton pipeline
   - Build application → Push to Quay.io
   - Update GitOps repository with new image tag
   - ArgoCD detects changes → Deploy to OpenShift

## Setup GitHub Authentication

To enable the pipeline to push changes to the GitOps repository, you need to configure GitHub authentication:

### Step 1: Create GitHub Personal Access Token

1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with the following permissions:
   - `repo` (Full control of private repositories)
   - `workflow` (Update GitHub Action workflows)
3. Copy the generated token

### Step 2: Create the Secret

Replace `YOUR_GITHUB_TOKEN_HERE` in the secret file and apply it:

```bash
# Edit the token in the file
vim tekton/06-github-auth-secret.yaml

# Apply the secret
oc apply -f tekton/06-github-auth-secret.yaml
```

### Step 3: Update Pipeline Runs to Include GitOps Auth

When running the pipeline manually, include the gitops-auth workspace:

```yaml
apiVersion: tekton.dev/v1beta1
kind: PipelineRun
metadata:
  name: bookstore-pipeline-run-test
  namespace: sgaikwad
spec:
  pipelineRef:
    name: bookstore-ci-pipeline
  params:
  - name: git-url
    value: https://github.com/sureshgaikwad/sample-bookstore-application.git
  - name: git-revision
    value: main
  - name: image-name
    value: quay.io/sureshgaikwad/app
  - name: image-tag
    value: latest
  workspaces:
  - name: shared-workspace
    volumeClaimTemplate:
      spec:
        accessModes:
        - ReadWriteOnce
        resources:
          requests:
            storage: 1Gi
  - name: dockerconfig-secret
    secret:
      secretName: quay-secret
  - name: gitops-auth
    secret:
      secretName: github-auth-secret
```

## Current Status

✅ **CI/CD Pipeline**: Fully functional  
✅ **GitHub Webhooks**: Working  
✅ **Container Build/Push**: Working (Quay.io)  
✅ **OpenShift GitOps**: Installed and configured  
✅ **ArgoCD Application**: Created for bookstore-app  
✅ **GitOps Update Task**: Implemented  

🔄 **Pending**: GitHub authentication setup for GitOps repository updates

## Verification Steps

After setting up the GitHub token:

1. **Test the Pipeline**: Push changes to the source repository
2. **Check GitOps Repository**: Verify image references are updated
3. **Validate ArgoCD**: Ensure application is automatically synchronized
4. **Verify Deployment**: Check if the new version is deployed to OpenShift

## Components Created

- **GitOps Operator**: `gitops/01-gitops-operator.yaml`
- **ArgoCD Application**: `gitops/02-bookstore-application.yaml`  
- **RBAC Configuration**: `gitops/03-argocd-rbac.yaml`
- **GitOps Update Task**: `tekton/05-update-gitops-task.yaml`
- **GitHub Auth Secret**: `tekton/06-github-auth-secret.yaml`
- **Updated Pipeline**: `tekton/03-pipeline.yaml`

## ArgoCD Dashboard

Access the ArgoCD dashboard:
```bash
oc get route argocd-server -n openshift-gitops
```

Login with your OpenShift credentials or admin account.

## Troubleshooting

1. **Pipeline succeeds but no GitOps updates**: Check GitHub authentication
2. **ArgoCD not syncing**: Verify application health in ArgoCD dashboard  
3. **Image pull errors**: Ensure image exists in Quay.io registry
4. **Webhook issues**: Check EventListener and TriggerBinding logs
