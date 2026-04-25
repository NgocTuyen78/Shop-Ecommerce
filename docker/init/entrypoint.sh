#!/bin/bash
set -e

# Wait for SQL Server to start and be ready
echo "============================================"
echo "Starting SQL Server..."
echo "============================================"
/opt/mssql/bin/sqlservr &

echo "Waiting for SQL Server to be ready..."
for i in $(seq 1 60); do
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -Q "SELECT 1" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "SQL Server is ready (attempt $i)."
        break
    fi
    if [ $i -eq 60 ]; then
        echo "ERROR: SQL Server failed to start after 60 attempts."
        exit 1
    fi
    sleep 2
done

# Run the init script to create database + seed data
echo "Running init script..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -i /init/init-db.sql

echo "============================================"
echo "Init completed. SQL Server is running."
echo "============================================"

# Keep container alive
wait
