# Shop-Ecommerce

E-commerce website for computer parts built with Spring Boot and Thymeleaf.

## Requirements

- Java 17+
- Maven (or use the included `./mvnw` wrapper)

## Quick Start (Any OS — Linux, macOS, Windows)

### Option A: Local Development (No Docker Needed) — Recommended

```bash
./mvnw spring-boot:run '-Dspring-boot.run.arguments=--spring.profiles.active=h2'
```

Access: **http://localhost:2345**

This uses H2 in-memory database — no installation required. Works out of the box on any OS.

### Option B: With SQL Server (Production-like)

1. Start SQL Server:
```bash
docker compose -f docker/docker-compose.single.yml up mssql --detach
```

2. Wait ~60s for SQL Server to be healthy, then:
```bash
./mvnw spring-boot:run
```

> **Docker Desktop users (Windows/macOS):** Docker handles networking automatically. App connects to `localhost:1433`.
>
> **Linux users:** Docker containers may not be reachable via `localhost`. Use Docker's `host.docker.internal` or run the app inside the Docker network.

Check DB is ready:
```bash
docker ps
# computershop-db   Up ... (healthy)   0.0.0.0:1433->1433/tcp
```

### Option C: Full Docker (App + DB in containers)

```bash
docker compose -f docker/docker-compose.single.yml up --build
```

Access: **http://localhost:2345**

### Option D: Distributed Mode (2 Databases)

```bash
docker compose -f docker/docker-compose.distributed.yml up --build
```

---

## Project Structure

```
Shop-Ecommerce/
├── src/main/java/com/computershop/
│   ├── config/           # Configuration
│   ├── controller/       # Controllers
│   │   ├── web/          # User controllers
│   │   └── admin/        # Admin controllers
│   ├── dto/              # Data Transfer Objects
│   ├── exception/        # Exception handling
│   ├── main/             # Entities, Repositories
│   ├── service/          # Business logic
│   │   ├── interface/
│   │   └── impl/
│   └── util/             # Utilities
├── docker/               # Docker configs
└── src/main/resources/
    ├── application.properties        # Default (SQL Server, localhost)
    ├── application-h2.properties    # H2 in-memory DB (local dev, cross-platform)
    └── application-distributed.properties
```

---

## Hot Reload

When running with Maven:

- Edit Java files → DevTools auto-restarts (~1-2 seconds)
- Edit HTML/CSS/JS → refresh browser, no restart needed

---

## Default Accounts

| Role    | Username  | Password  |
|---------|-----------|-----------|
| Admin   | `admin`   | `admin123`|
| Customer| `user`    | `user123` |
| Customer| `customer1`| `123456`|
| Customer| `customer2`| `123456`|

---

## Database Connections

### H2 Console (when using `--spring.profiles.active=h2`)

Browse at: **http://localhost:2345/h2-console**

| Field            | Value                          |
|------------------|--------------------------------|
| JDBC URL         | `jdbc:h2:mem:computershop`     |
| User Name        | `sa`                           |
| Password         | *(empty)*                      |

### DBeaver / SQL Client (SQL Server)

| Field                    | Value                    |
|--------------------------|--------------------------|
| Host                     | `localhost`              |
| Port                     | `1433`                   |
| Database                 | `computershop`           |
| Username                 | `sa`                     |
| Password                 | `YourStrong@Passw0rd`    |
| Encrypt                  | `true`                   |
| Trust Server Certificate | `true`                   |
