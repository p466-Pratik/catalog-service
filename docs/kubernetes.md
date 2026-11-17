
# Practicum 3 — Kubernetes

## Deploying Catalog Service

I deployed PostgreSQL and Catalog Service to a local Minikube cluster using Kubernetes Deployments and Services.

Catalog Service uses readiness and liveness probes to determine whether it can receive traffic and whether it needs to be restarted.

I verified that accessing Catalog Service through Kubernetes returned HTTP 200 and the sample books from PostgreSQL.

## Scaling and Self-Healing

I changed the Catalog Service Deployment from one replica to two replicas.

Kubernetes created a second Pod, and both became ready with status `1/1 Running`.

I then deleted one Catalog Service Pod to test self-healing. Kubernetes automatically created a replacement Pod to maintain the desired replica count.

The replacement became ready after its readiness probe succeeded.

Finally, I restored the Deployment to one replica.

## Graceful Shutdown

Catalog Service uses graceful shutdown with a 15-second shutdown-phase timeout.

The Kubernetes Deployment includes a 5-second preStop delay to allow Service routing changes to propagate before the application stops accepting connections.
