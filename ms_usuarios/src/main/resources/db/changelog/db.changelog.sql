--liquibase formatted sql

--changeset ferrefix:1
-- Creación de tabla Cargo
CREATE TABLE cargo (
    id_cargo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cargo VARCHAR(100) NOT NULL UNIQUE
);

-- Creación de tabla Cliente (extra id_direccion_fk)
CREATE TABLE cliente (
    run_cliente INT PRIMARY KEY,
    dv_cliente CHAR(1) NOT NULL,
    pnombre_cliente VARCHAR(50) NOT NULL,
    snombre_cliente VARCHAR(50),
    appaterno_cliente VARCHAR(50) NOT NULL,
    apmaterno_cliente VARCHAR(50) NOT NULL,
    fecha_nacimiento_cliente DATE NOT NULL,
    email_cliente VARCHAR(255) NOT NULL UNIQUE,
    contrasena_cliente VARCHAR(255) NOT NULL,
    telefono_cliente VARCHAR(9),
    fecha_registro_cliente DATE NOT NULL,
    id_direccion_fk BIGINT NOT NULL
);

-- Creación de tabla Empleado
CREATE TABLE empleado (
    run_empleado INT PRIMARY KEY,
    dv_empleado CHAR(1) NOT NULL,
    pnombre_empleado VARCHAR(50) NOT NULL,
    snombre_empleado VARCHAR(50),
    appaterno_empleado VARCHAR(50) NOT NULL,
    apmaterno_empleado VARCHAR(50) NOT NULL,
    email_empleado VARCHAR(255) NOT NULL UNIQUE,
    contrasena_empleado VARCHAR(255) NOT NULL,
    sueldo_base_empleado INT NOT NULL,
    fecha_contratacion_empleado DATE NOT NULL,
    telefono_empleado VARCHAR(9),
    activo_empleado BOOLEAN NOT NULL DEFAULT TRUE,
    id_cargo_fk INT NOT NULL,
    CONSTRAINT fk_empleado_cargo FOREIGN KEY (id_cargo_fk) REFERENCES cargo(id_cargo)
);

--changeset ferrefix:2
-- Insertar datos en tabla Cargo
INSERT INTO cargo (nombre_cargo) VALUES 
('Administrador de Sistema'),
('Gerente de Operaciones'),
('Ejecutivo de Ventas'),
('Encargado de Logística'),
('Asesor de Atención al Cliente');

-- Insertar datos en tabla Cliente (Ahora insertamos también un ID de dirección lógico del 1 al 5)
INSERT INTO cliente (run_cliente, dv_cliente, pnombre_cliente, snombre_cliente, appaterno_cliente, apmaterno_cliente, fecha_nacimiento_cliente, email_cliente, contrasena_cliente, telefono_cliente, fecha_registro_cliente, id_direccion_fk) VALUES 
(12345678, '5', 'Juan', 'Pablo', 'Perez', 'Soto', '1990-01-15', 'juan.perez@test.cl', 'pass123', '912345678', '2023-01-01', 1),
(23456789, 'K', 'Maria', 'Jose', 'Gonzalez', 'Tapia', '1985-06-20', 'maria.g@test.cl', 'pass456', '923456789', '2023-02-15', 2),
(34567890, '1', 'Carlos', NULL, 'Rojas', 'Silva', '1982-11-10', 'carlos.r@test.cl', 'pass789', '934567890', '2023-03-20', 3),
(45678901, '2', 'Camila', 'Andrea', 'Morales', 'Diaz', '1995-04-05', 'camila.m@test.cl', 'passabc', '945678901', '2023-04-10', 4),
(56789012, '3', 'Luis', 'Alberto', 'Castro', 'Ruiz', '1978-08-30', 'luis.c@test.cl', 'passdef', '956789012', '2023-05-05', 5);

-- Insertar datos en tabla Empleado
INSERT INTO empleado (run_empleado, dv_empleado, pnombre_empleado, snombre_empleado, appaterno_empleado, apmaterno_empleado, email_empleado, contrasena_empleado, sueldo_base_empleado, fecha_contratacion_empleado, telefono_empleado, activo_empleado, id_cargo_fk) VALUES 
(15666777, '1', 'Carlos', 'Eduardo', 'Martinez', 'Rios', 'carlos.m@ferrefix.cl', 'hash123', 800000, '2022-01-15', '987654321', TRUE, 1),
(18999000, '2', 'Ana', 'Belen', 'Silva', 'Molina', 'ana.s@ferrefix.cl', 'hash456', 500000, '2023-03-10', '912341234', TRUE, 2),
(17555444, 'K', 'Diego', NULL, 'Tapia', 'Vergara', 'diego.t@ferrefix.cl', 'hash789', 650000, '2023-05-20', '955555555', TRUE, 3),
(19888777, '3', 'Laura', 'Isabel', 'Guzman', 'Pinto', 'laura.g@ferrefix.cl', 'hashabc', 550000, '2023-08-01', '944444444', TRUE, 4),
(16222333, '4', 'Felipe', 'Andres', 'Poblete', 'Salazar', 'felipe.p@ferrefix.cl', 'hashdef', 900000, '2021-11-11', '977777777', TRUE, 5);