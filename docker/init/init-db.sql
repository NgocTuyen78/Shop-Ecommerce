IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'computershop')
BEGIN
    CREATE DATABASE computershop;
END
GO

-- Run the full schema + seed data script
USE computershop;
GO
:r /init/database.sql
GO
