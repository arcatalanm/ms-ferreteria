--liquibase formatted sql

--changeset ferrefix:1
CREATE TABLE tipo_pago (
    id_tipo_pago INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo_pago VARCHAR(50) NOT NULL UNIQUE
);

--changeset ferrefix:2
CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_venta DATETIME NOT NULL,
    total_venta INT NOT NULL,
    run_cliente_fk INT NOT NULL,
    run_empleado_fk INT NOT NULL,
    id_tipo_pago_fk INT NOT NULL,
    CONSTRAINT fk_venta_tipo_pago FOREIGN KEY (id_tipo_pago_fk) REFERENCES tipo_pago(id_tipo_pago)
);

--changeset ferrefix:3
CREATE TABLE detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto_fk BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario_historico INT NOT NULL,
    id_venta BIGINT NOT NULL,
    CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);

--changeset ferrefix:4
INSERT INTO tipo_pago (nombre_tipo_pago) VALUES 
('Efectivo'), ('Tarjeta de Débito'), ('Tarjeta de Crédito'), ('Transferencia Bancaria'), ('Crédito Ferretería');

INSERT INTO venta (fecha_venta, total_venta, run_cliente_fk, run_empleado_fk, id_tipo_pago_fk) VALUES 
('2023-12-01 10:15:00', 87500, 66666666, 15300004, 2),
('2023-12-02 11:30:00', 25000, 12222222, 15400005, 1),
('2023-12-03 14:45:00', 135000, 13333333, 15500006, 3),
('2023-12-04 09:20:00', 18500, 14444444, 16100012, 2),
('2023-12-05 16:10:00', 3200, 66666666, 15300004, 1),
('2023-12-06 12:05:00', 45000, 16666777, 15400005, 4),
('2023-12-07 15:50:00', 11500, 17777888, 15500006, 2),
('2023-12-08 10:40:00', 210000, 18888999, 16100012, 3),
('2023-12-09 13:25:00', 6500, 19999000, 15300004, 1),
('2023-12-10 17:00:00', 74000, 20111222, 15400005, 2),
('2023-12-11 09:50:00', 2500, 66666666, 15500006, 1),
('2023-12-12 11:15:00', 42000, 22333444, 16100012, 2),
('2023-12-13 14:30:00', 12500, 23444555, 15300004, 1),
('2023-12-14 16:45:00', 8500, 24555666, 15400005, 2),
('2023-12-15 10:05:00', 150000, 25555555, 15500006, 4),
('2023-12-16 12:20:00', 4900, 66666666, 16100012, 1),
('2023-12-17 15:35:00', 22000, 12222222, 15300004, 2),
('2023-12-18 09:10:00', 35000, 13333333, 15400005, 3),
('2023-12-19 14:00:00', 5500, 66666666, 15500006, 1),
('2023-12-20 16:55:00', 89000, 15555555, 16100012, 3);

INSERT INTO detalle_venta (id_producto_fk, cantidad, precio_unitario_historico, id_venta) VALUES 
(1, 1, 65000, 1), (23, 1, 22000, 1),
(15, 10, 2500, 2),
(5, 30, 4500, 3),
(11, 1, 18500, 4),
(16, 1, 3200, 5),
(21, 3, 15000, 6),
(17, 1, 11500, 7),
(30, 1, 89000, 8), (2, 1, 42000, 8), (12, 1, 35000, 8), (23, 2, 22000, 8),
(25, 1, 6500, 9),
(18, 4, 18000, 10), (7, 1, 2000, 10),
(4, 1, 3500, 11),
(2, 1, 42000, 12),
(3, 1, 12500, 13),
(19, 1, 8500, 14),
(21, 10, 15000, 15),
(13, 1, 4900, 16),
(23, 1, 22000, 17),
(12, 1, 35000, 18),
(14, 1, 5500, 19),
(30, 1, 89000, 20);