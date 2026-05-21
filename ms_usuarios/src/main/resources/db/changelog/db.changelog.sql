--liquibase formatted sql

--changeset ferrefix:1
CREATE TABLE cargo (
    id_cargo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cargo VARCHAR(100) NOT NULL UNIQUE
);

--changeset ferrefix:2
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

--changeset ferrefix:3
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

--changeset ferrefix:4
INSERT INTO cargo (nombre_cargo) VALUES
('Jefe de Local'), ('Vendedor Mesón'), ('Cajero'), ('Operario de Bodega'), ('Servicio Técnico');

INSERT INTO cliente (run_cliente, dv_cliente, pnombre_cliente, snombre_cliente, appaterno_cliente, apmaterno_cliente, fecha_nacimiento_cliente, email_cliente, contrasena_cliente, telefono_cliente, fecha_registro_cliente, id_direccion_fk) VALUES 
(11111111, '1', 'Juan', 'Carlos', 'Pérez', 'Soto', '1980-05-15', 'juan.perez@mail.cl', 'hash1', '911111111', '2023-01-10', 1),
(12222222, '2', 'María', 'José', 'González', 'Tapia', '1985-08-20', 'maria.g@mail.cl', 'hash2', '922222222', '2023-01-15', 2),
(13333333, '3', 'Pedro', NULL, 'Rojas', 'Díaz', '1990-11-10', 'projas@mail.cl', 'hash3', '933333333', '2023-02-20', 3),
(14444444, '4', 'Camila', 'Andrea', 'Morales', 'Molina', '1995-04-05', 'cmorales@mail.cl', 'hash4', '944444444', '2023-03-05', 4),
(15555555, '5', 'Luis', 'Alberto', 'Castro', 'Ruiz', '1978-02-28', 'lcastro@mail.cl', 'hash5', '955555555', '2023-03-12', 5),
(16666777, 'K', 'Ana', 'Belen', 'Silva', 'Cortes', '1988-07-14', 'asilva@mail.cl', 'hash6', '966666666', '2023-04-18', 6),
(17777888, '6', 'Diego', 'Ignacio', 'Tapia', 'Vera', '1992-09-30', 'dtapia@mail.cl', 'hash7', '977777777', '2023-05-22', 7),
(18888999, '7', 'Laura', 'Isabel', 'Guzmán', 'Pinto', '1982-12-12', 'lguzman@mail.cl', 'hash8', '988888888', '2023-06-10', 8),
(19999000, '8', 'Felipe', 'Andrés', 'Poblete', 'Salazar', '1975-03-25', 'fpoblete@mail.cl', 'hash9', '999999999', '2023-07-05', 9),
(20111222, '9', 'Daniela', 'Paz', 'Herrera', 'Muñoz', '1998-01-18', 'dherrera@mail.cl', 'hash10', '900000001', '2023-08-14', 10),
(21222333, '0', 'Rodrigo', 'Esteban', 'Araya', 'Lagos', '1986-06-08', 'raraya@mail.cl', 'hash11', '900000002', '2023-09-02', 11),
(22333444, '1', 'Valentina', 'Sofía', 'Navarro', 'Ríos', '2000-10-22', 'vnavarro@mail.cl', 'hash12', '900000003', '2023-09-15', 12),
(23444555, '2', 'Matías', 'Alejandro', 'Vidal', 'Bravo', '1994-05-05', 'mvidal@mail.cl', 'hash13', '900000004', '2023-10-20', 13),
(24555666, '3', 'Javiera', 'Ignacia', 'Garrido', 'Méndez', '1997-08-11', 'jgarrido@mail.cl', 'hash14', '900000005', '2023-11-05', 14),
(25555555, '4', 'Sebastián', NULL, 'Paredes', 'Orellana', '1983-02-19', 'sparedes@mail.cl', 'hash15', '900000006', '2023-12-01', 15),
-- REGLA DE NEGOCIO: RUT 66666666-6 SE OCUPA PARA USUARIOS NO REGISTRADOS (EVITAR NULLs)
-- REGLA DE NEGOCIO: EL USUARIO COMUN POR DEFECTO OCUPARA LA DIRECCION DE LA FERRETERIA (POR DEFAUTL)
(66666666, '6', 'Consumidor', 'Final', 'Mesón', 'Ferretería', '2000-01-01', 'consumidor.final@mail.cl', 'no_auth_pass', '900000000', '2023-01-01', 1);

INSERT INTO empleado (run_empleado, dv_empleado, pnombre_empleado, snombre_empleado, appaterno_empleado, apmaterno_empleado, email_empleado, contrasena_empleado, sueldo_base_empleado, fecha_contratacion_empleado, telefono_empleado, activo_empleado, id_cargo_fk) VALUES 
(13444555, '6', 'Felipe', 'Ignacio', 'Garrido', 'Vidal', 'fgarrido@ferrefix.cl', 'emp1', 950000, '2021-03-01', '966778899', TRUE, 1),
(18555666, '7', 'Andrea', 'Carolina', 'Molina', 'Bravo', 'amolina@ferrefix.cl', 'emp2', 550000, '2022-06-15', '977889900', TRUE, 2),
(16666777, '8', 'Sebastián', NULL, 'Paredes', 'Navarro', 'sparedes@ferrefix.cl', 'emp3', 500000, '2023-01-10', '988990011', TRUE, 3),
(19777888, '9', 'Daniela', 'Andrea', 'Castro', 'Pinto', 'dcastro@ferrefix.cl', 'empabc', 480000, '2023-08-05', '999001122', TRUE, 4),
(14888999, '0', 'Hugo', 'Alberto', 'Reyes', 'Silva', 'hreyes@ferrefix.cl', 'empdef', 600000, '2020-11-20', '900112233', TRUE, 5);