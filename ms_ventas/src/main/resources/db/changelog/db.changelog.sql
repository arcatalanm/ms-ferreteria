--liquibase formatted sql

--changeset ferrefix:1
-- Creación de tabla TipoPago (Sin dependencias)
CREATE TABLE tipo_pago (
    id_tipo_pago INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo_pago VARCHAR(50) NOT NULL UNIQUE
);

-- Creación de tabla Venta (Depende de TipoPago)
CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_venta DATETIME NOT NULL,
    total_venta INT NOT NULL,
    run_cliente_fk INT NOT NULL,
    run_empleado_fk INT NOT NULL,
    id_tipo_pago_fk INT NOT NULL,
    CONSTRAINT fk_venta_tipo_pago FOREIGN KEY (id_tipo_pago_fk) REFERENCES tipo_pago(id_tipo_pago)
);

-- Creación de tabla DetalleVenta (Depende de Venta)
CREATE TABLE detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto_fk BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario_historico INT NOT NULL,
    id_venta BIGINT NOT NULL,
    CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);

--changeset ferrefix:2
--Inserts para tabla TipoPago
INSERT INTO tipo_pago (nombre_tipo_pago) VALUES 
('Efectivo'),
('Tarjeta de Débito'),
('Tarjeta de Crédito'),
('Transferencia Bancaria'),
('Cheque a fecha');

--Inserts para tabla Venta
--(Se asume que los id_tipo_pago 1 al 5 fueron creados por el insert anterior)
INSERT INTO venta (fecha_venta, total_venta, run_cliente_fk, run_empleado_fk, id_tipo_pago_fk) VALUES 
('2023-10-01 10:30:00', 15000, 11111111, 15666777, 1),
('2023-10-02 14:45:00', 45000, 22222222, 18999000, 2),
('2023-10-03 09:15:00', 8990, 33333333, 17555444, 1),
('2023-10-04 16:20:00', 125000, 44444444, 19888777, 3),
('2023-10-05 11:05:00', 32000, 55555555, 16222333, 4);

--Inserts para tabla DetalleVenta
--(Se asume que los id_venta 1 al 5 fueron generados por los inserts de venta)
--Se cuadra la (cantidad * precio_unitario_historico) con el total_venta de arriba
INSERT INTO detalle_venta (id_producto_fk, cantidad, precio_unitario_historico, id_venta) VALUES 
(1001, 2, 7500, 1),  -- 2 x 7500 = 15000 (Venta 1)
(1002, 1, 45000, 2), -- 1 x 45000 = 45000 (Venta 2)
(1003, 1, 8990, 3),  -- 1 x 8990 = 8990 (Venta 3)
(1004, 5, 25000, 4), -- 5 x 25000 = 125000 (Venta 4)
(1005, 4, 8000, 5);  -- 4 x 8000 = 32000 (Venta 5)