# microservice-exp

A hands-on Spring Boot microservices playground for experimenting with production-grade distributed-systems patterns — service discovery, API gateway routing, centralized configuration, event-driven communication with Kafka, and resilience patterns — wired together locally with Docker Compose.

The goal is not a product; it's a reference implementation. Each service is intentionally small so the *patterns* (not the business logic) stay in focus.
 
---

## Architecture

```
                        ┌──────────────────┐
                        │   API Gateway    │  :8080
                        │ (Spring Cloud GW)│
                        └────────┬─────────┘
                                 │  route + load balance
             ┌───────────────────┼───────────────────┐
             ▼                   ▼                   ▼
     ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐
     │ order-service│    │product-service│   │ notification-svc │
     │    :8081     │    │    :8082      │   │      :8083       │
     └──────┬───────┘    └──────┬────────┘   └────────▲─────────┘
            │                   │                     │
            │   publish         │                     │ consume
            └──────────► Kafka (order-events) ────────┘
                                 
     ┌──────────────┐    ┌──────────────┐
     │Eureka Server │    │Config Server │
     │    :8761     │    │    :8888     │
     └──────────────┘    └──────────────┘
        (discovery)      (centralized config)
```

## Services

| Service | Port | Responsibility |
|---|---|---|
| `api-gateway` | 8080 | Single entry point; routing, load balancing, cross-cutting filters |
| `discovery-server` | 8761 | Eureka service registry — services register and discover each other by name |
| `config-server` | 8888 | Centralized externalized configuration for all services |
| `order-service` | 8081 | Creates orders; publishes `order-created` events to Kafka |
| `product-service` | 8082 | Product catalog; called synchronously by order-service (OpenFeign) |
| `notification-service` | 8083 | Consumes order events from Kafka; simulates async notifications |

## Patterns demonstrated

- **Service discovery** — Eureka client/server; no hardcoded hosts, lookups by service name
- **API Gateway** — Spring Cloud Gateway routes with load-balanced (`lb://`) URIs
- **Centralized config** — Spring Cloud Config; per-service and shared properties
- **Sync inter-service calls** — OpenFeign client with client-side load balancing
- **Async event-driven flow** — Kafka producer/consumer; order lifecycle as events
- **Resilience** — Resilience4j circuit breaker + retry + fallback on the order → product call
- **Observability** — Micrometer tracing with Zipkin; correlated trace IDs across services
- **Containerization** — Dockerfile per service, one-command bring-up via Docker Compose
## Tech stack

| Layer | Technology |
|---|---|
| Language / Framework | Java 17, Spring Boot 3.x, Spring Cloud |
| Messaging | Apache Kafka |
| Discovery / Config | Netflix Eureka, Spring Cloud Config |
| Gateway | Spring Cloud Gateway |
| HTTP clients | OpenFeign |
| Resilience | Resilience4j |
| Tracing | Micrometer + Zipkin |
| Build / Runtime | Maven (multi-module), Docker, Docker Compose |

## Getting started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker + Docker Compose
### Run everything with Docker Compose

```bash
git clone <repo-url>
cd microservice-exp
 
# build all service images and start the full stack (Kafka, Zipkin included)
docker compose up --build
```

### Run locally (dev mode)

Start infrastructure first, then services in order:

```bash
# 1. infra only (Kafka, Zookeeper, Zipkin)
docker compose up -d kafka zookeeper zipkin
 
# 2. platform services
cd discovery-server && mvn spring-boot:run   # :8761
cd config-server    && mvn spring-boot:run   # :8888
 
# 3. business services (any order once the above are up)
cd product-service      && mvn spring-boot:run
cd order-service        && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway          && mvn spring-boot:run
```

### Verify

- Eureka dashboard: http://localhost:8761 — all services registered
- Zipkin traces: http://localhost:9411
## Try it

All calls go through the gateway:

```bash
# create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Mechanical Keyboard","price":4999,"stock":25}'
 
# place an order → sync call to product-service + Kafka event to notification-service
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
 
# fetch orders
curl http://localhost:8080/api/orders
```

Watch the `notification-service` logs to see the consumed Kafka event, and open Zipkin to see the full trace across gateway → order → product.

## Project structure

```
microservice-exp/
├── api-gateway/
├── config-server/
├── discovery-server/
├── order-service/
├── product-service/
├── notification-service/
├── docker-compose.yml
└── pom.xml            # parent (multi-module)
```

## Roadmap

- [ ] Saga pattern (choreography) for order → payment → inventory flow
- [ ] Outbox pattern for reliable event publishing
- [ ] Centralized auth at the gateway (JWT / Spring Security)
- [ ] Kubernetes manifests + Helm chart
- [ ] Contract testing with Spring Cloud Contract