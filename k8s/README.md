# Kubernetes Deployment — Minikube (Step 12)

This directory contains all Kubernetes manifests to run the AI agent stack locally on Minikube.

## Architecture

```
[User] → port-forward → [ai-agent:8080] → ClusterIP → [github-mcp-server:3333]
                                  │                              │
                              OpenAI API                   DinD sidecar
                          (external HTTPS)          (docker run github-mcp-server)
```

| Manifest | Resource | Description |
|---|---|---|
| `configmap.yaml` | ConfigMap `agent-config` | Non-secret config: `GITHUB_OWNER`, `GITHUB_REPO` |
| `github-mcp.yaml` | Deployment + Service | Node.js MCP HTTP wrapper + Docker-in-Docker sidecar |
| `ai-agent.yaml` | Deployment + Service | Spring Boot AI agent |

---

## Prerequisites

- `minikube` installed and running
- `kubectl` installed
- `docker` installed

---

## Step-by-Step Deployment

### 1. Start Minikube

```powershell
minikube start --cpus=4 --memory=8192
```

### 2. Point your shell at Minikube's Docker daemon

```powershell
# PowerShell
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

### 3. Build images inside Minikube

Run from the **repository root** (one level above this `k8s/` folder):

```powershell
# Spring Boot AI agent
docker build -t ai-agent:dev .

# Node.js GitHub MCP wrapper
docker build -t github-mcp-server:dev ./mcp-github-http-wrapper
```

### 4. Create namespace

```powershell
kubectl create ns lab-agent
```

### 5. Edit ConfigMap

Open `k8s/configmap.yaml` and set your values:

```yaml
GITHUB_OWNER: "your-github-username"
GITHUB_REPO:  "your-repo-name"
```

### 6. Create Kubernetes Secret

> ⚠️ The secret uses `OPENAI_API_KEY` (not Anthropic) and `GITHUB_PERSONAL_ACCESS_TOKEN` (not `GITHUB_TOKEN`).

```powershell
kubectl -n lab-agent create secret generic agent-secrets `
  --from-literal=OPENAI_API_KEY="$env:OPENAI_API_KEY" `
  --from-literal=GITHUB_PERSONAL_ACCESS_TOKEN="$env:GITHUB_PERSONAL_ACCESS_TOKEN"
```

### 7. Apply all manifests

```powershell
kubectl apply -f k8s/
```

### 8. Check pod status

```powershell
kubectl -n lab-agent get pods
kubectl -n lab-agent get svc
```

Wait until both pods show `Running` and `READY`.

> The `github-mcp-server` pod starts two containers (`dind` + `github-mcp-server`).  
> Check readiness: `READY 2/2`.

### 9. Follow logs

```powershell
# MCP wrapper (check Docker/GitHub connection)
kubectl -n lab-agent logs deploy/github-mcp-server -c github-mcp-server -f

# AI agent
kubectl -n lab-agent logs deploy/ai-agent -f
```

### 10. Port-forward and test

```powershell
# Terminal 1: keep this running
kubectl -n lab-agent port-forward svc/ai-agent 8080:8080

# Terminal 2: send a request
curl http://localhost:8080/api/run `
  -H "Content-Type: application/json" `
  -d '"Create a GitHub task to add OpenTelemetry with OTLP exporter."'
```

---

## Troubleshooting

| Symptom | Likely Cause | Fix |
|---|---|---|
| `github-mcp-server` pod stuck in `Init` / `0/2 Running` | DinD sidecar not ready | Wait ~30s; check `kubectl logs ... -c dind` |
| `Connection refused` on MCP call | `github-mcp-server` service not reachable | Verify `kubectl -n lab-agent get svc` shows port 3333 |
| `403 Forbidden` from GitHub API | Token missing or lacks `Issues: Write` permission | Recreate secret with correct Fine-grained PAT |
| No tool calls by agent | System prompt too weak | Strengthen prompt in `BacklogAgent.java` |
| `GITHUB_PERSONAL_ACCESS_TOKEN` missing | Secret key mismatch | Confirm secret key is `GITHUB_PERSONAL_ACCESS_TOKEN` (not `GITHUB_TOKEN`) |

---

## Cleanup

```powershell
kubectl delete ns lab-agent
```
