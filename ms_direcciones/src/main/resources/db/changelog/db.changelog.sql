--liquibase formatted sql

--changeset ferrefix:1
CREATE TABLE direccion (
    id_direccion BIGINT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    departamento VARCHAR(20),
    comuna VARCHAR(50) NOT NULL,
    ciudad VARCHAR(50) NOT NULL
);

--changeset ferrefix:2
-- Datos iniciales para pruebas y para calzar con los IDs que pusimos en ms_usuarios
INSERT INTO direccion (calle, numero, departamento, comuna, ciudad) VALUES 
('Av. Providencia', 1234, 'Depto 402', 'Providencia', 'Santiago'),
('Los Leones', 567, NULL, 'Providencia', 'Santiago'),
('Gran Avenida', 7890, 'Casa 3', 'San Miguel', 'Santiago'),
('Vicuña Mackenna', 100, 'Depto 12', 'Santiago Centro', 'Santiago'),
('Av. Pajaritos', 4500, NULL, 'Maipú', 'Santiago');