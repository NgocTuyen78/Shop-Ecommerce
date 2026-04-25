# Migration Guide: SQL Server → PostgreSQL

## Overview
This project has been successfully migrated from **Microsoft SQL Server** to **PostgreSQL**. All configuration files, dependencies, and schemas have been updated accordingly.

## Changes Made

### 1. **Maven Dependencies (pom.xml)**
- ❌ Removed: `com.microsoft.sqlserver:mssql-jdbc`
- ✅ Added: `org.postgresql:postgresql`

**Before:**
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

**After:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. **Application Configuration (application.properties & application-distributed.properties)**

#### Connection String
- **Before (SQL Server):**
  ```properties
  spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=computershop;encrypt=false
  spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
  ```

- **After (PostgreSQL):**
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/computershop
  spring.datasource.driver-class-name=org.postgresql.Driver
  ```

#### Hibernate Dialect
- **Before:** `org.hibernate.dialect.SQLServerDialect`
- **After:** `org.hibernate.dialect.PostgreSQLDialect`

#### Distributed Mode Ports
- **Before:** 
  - Primary DB: `localhost:1433`
  - Orders DB: `localhost:1434`
- **After:**
  - Primary DB: `localhost:5432`
  - Orders DB: `localhost:5433`

### 3. **Database Configuration (DatabaseConfig.java)**
- Changed JDBC driver class from `com.microsoft.sqlserver.jdbc.SQLServerDriver` to `org.postgresql.Driver` (2 locations)

### 4. **Docker Compose Files**

#### docker-compose.single.yml
- **Before:** Used `mcr.microsoft.com/mssql/server:2019-latest`
- **After:** Uses `postgres:16-alpine`
- Removed encryption parameters
- Changed health check command to PostgreSQL standard

#### docker-compose.distributed.yml
- **Before:** Two separate SQL Server containers (`mssql-main`, `mssql-orders`)
- **After:** Two separate PostgreSQL containers (`postgres-main`, `postgres-orders`)
- Updated connection strings in environment variables
- Changed port mapping from 1434 to 5433 for second database

### 5. **Database Schema Files**

#### SQL Syntax Conversions

| SQL Server | PostgreSQL |
|-----------|-----------|
| `GO` | Removed (end of statement) |
| `IDENTITY(1,1)` | `SERIAL` |
| `NVARCHAR` | `VARCHAR` or `TEXT` |
| `DATETIME` | `TIMESTAMP` |
| `GETDATE()` | `CURRENT_TIMESTAMP` |
| `BIT` | `BOOLEAN` |
| `IF OBJECT_ID(...)` | `DROP TABLE IF EXISTS` |
| `IF NOT EXISTS (SELECT name FROM sys.databases...)` | Handled externally (comments in script) |
| `dbo.` schema prefix | Removed |
| String concatenation with `+` | String concatenation with `\|\|` |

#### Files Updated
- `src/main/resources/db/schema-main.sql` - Converted to PostgreSQL syntax
- `src/main/resources/db/schema-orders.sql` - Converted to PostgreSQL syntax
- `database.sql` - Converted to PostgreSQL syntax with comprehensive comments

### Example Schema Changes

**Before (SQL Server):**
```sql
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL DROP TABLE dbo.users;
CREATE TABLE dbo.users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);
GO
```

**After (PostgreSQL):**
```sql
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Running the Application

### Prerequisites
- Java 17+
- PostgreSQL 16+ (or use Docker)
- Maven 3.9+

### Option 1: Local PostgreSQL (Manual Setup)

**1. Create databases and roles:**
```bash
# Connect as superuser (usually 'postgres')
psql -U postgres

# In PostgreSQL shell:
CREATE ROLE postgres WITH LOGIN PASSWORD 'postgres' SUPERUSER;
CREATE DATABASE computershop OWNER postgres;
CREATE DATABASE computershop_main OWNER postgres;
CREATE DATABASE computershop_orders OWNER postgres;
```

**2. Initialize schema:**
```bash
psql -U postgres -d computershop -f database.sql
```

**3. Run the application:**
```bash
./mvnw spring-boot:run
```

### Option 2: Docker Compose (Single Mode)

```bash
# Start PostgreSQL container
docker compose -f docker/docker-compose.single.yml up postgres -d

# Wait for container to be healthy (~60s), then run:
./mvnw spring-boot:run
```

### Option 3: Docker Compose (Distributed Mode)

```bash
# Build and start all services
docker compose -f docker/docker-compose.distributed.yml up --build

# App will automatically initialize databases on startup
```

## Configuration Profiles

### Active Profiles

| Profile | Use Case | Database |
|---------|----------|----------|
| `default` (none) | Production/Single DB Mode | PostgreSQL on `localhost:5432` |
| `distributed` | Multi-database setup | PostgreSQL on ports 5432 & 5433 |
| `h2` | Development/Testing | H2 in-memory (unchanged) |

### Running with Specific Profile

```bash
# Single mode (default)
./mvnw spring-boot:run

# Distributed mode
./mvnw spring-boot:run -Dspring.profiles.active=distributed

# H2 mode (for development)
./mvnw spring-boot:run -Dspring.profiles.active=h2
```

## Default Credentials

After running the database initialization scripts, use these credentials:

- **Admin Account**
  - Username: `admin`
  - Password: `admin123`

- **User Account**
  - Username: `user`
  - Password: `user123`

## Connection String Examples

### Application Properties
```properties
# Single database
spring.datasource.url=jdbc:postgresql://localhost:5432/computershop
spring.datasource.username=postgres
spring.datasource.password=postgres

# Distributed mode - Primary DB
spring.datasource.url=jdbc:postgresql://localhost:5432/computershop_main
spring.datasource.username=postgres
spring.datasource.password=postgres

# Distributed mode - Orders DB
spring.datasource.orders.url=jdbc:postgresql://localhost:5433/computershop_orders
spring.datasource.orders.username=postgres
spring.datasource.orders.password=postgres
```

### Environment Variables
```bash
# For Docker/remote deployment
export SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/computershop
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=<password>
```

## Testing

### Run Unit Tests
```bash
# Uses H2 in-memory database (unchanged)
./mvnw test
```

### Test Database Profile
The `application-test.properties` remains unchanged as it uses H2 for integration tests.

## Troubleshooting

### Connection Issues

**Error: "Connection refused"**
- Ensure PostgreSQL is running: `docker ps` or `brew services list` (macOS)
- Check port mappings in docker-compose files
- Verify credentials in properties files

**Error: "Database does not exist"**
- Run initialization scripts: `psql -U postgres -d computershop -f database.sql`
- For distributed mode, initialize both databases separately

### Data Type Compatibility

If you have custom code referring to SQL Server specific features:
- Review and update any stored procedures (convert to PostgreSQL functions/procedures)
- Check any manual SQL queries for T-SQL syntax
- Update transaction isolation levels if needed

## Rollback to SQL Server

If you need to revert to SQL Server, follow these steps:

1. Restore from backup: `git checkout HEAD -- pom.xml docker/ src/main/resources/`
2. Update configuration profiles back to SQL Server connection strings
3. Restore SQL Server database from backup
4. Reinstall dependencies: `./mvnw clean install`

## Migration Notes

- PostgreSQL uses lowercase for identifiers by default (unless quoted)
- SERIAL has a max value of 2,147,483,647 (same as SQL Server INT)
- BIGSERIAL is available if you need larger sequences
- CASCADE option on DROP TABLE automatically drops dependent objects
- BOOLEAN accepts true/false, 't'/'f', 'yes'/'no', etc.
- TIMESTAMP and TIMESTAMP WITH TIME ZONE are different; current setup uses TIMESTAMP

## Performance Considerations

- PostgreSQL uses SERIAL which internally creates sequences
- Indexes and query optimization may differ from SQL Server
- Consider benchmarking critical queries
- PostgreSQL's ANALYZE command can optimize query plans

## Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Data JPA with PostgreSQL](https://spring.io/guides/gs/accessing-data-jpa/)
- [Hibernate PostgreSQL Dialect](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [Docker PostgreSQL Image](https://hub.docker.com/_/postgres)

---

**Migration Date:** April 2026  
**Status:** ✅ Complete
