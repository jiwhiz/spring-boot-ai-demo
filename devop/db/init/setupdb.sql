-- Setup for demo db
CREATE USER walle WITH PASSWORD 'walle';
CREATE DATABASE demodatabase WITH OWNER walle;
GRANT ALL PRIVILEGES ON DATABASE demodatabase TO walle;
