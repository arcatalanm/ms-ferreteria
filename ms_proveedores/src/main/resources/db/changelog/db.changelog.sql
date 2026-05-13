--liquibase formatted sql

--changeset ferrefix:1
CREATE TABLE proveedor (
    id_proveedor INT AUTO_INCREMENT NOT NULL,
    rut_proveedor INT NOT NULL,
    dv_proveedor CHAR(1) NOT NULL,
    nombre_proveedor VARCHAR(100) NOT NULL,
    giro_proveedor VARCHAR(100) NOT NULL,
    direccion_proveedor BIGINT NOT NULL,
    telefono_proveedor VARCHAR(15),
    correo_proveedor VARCHAR(50),
    CONSTRAINT PK_PROVEEDOR PRIMARY KEY (id_proveedor),
    CONSTRAINT UK_RUT_PROVEEDOR UNIQUE (rut_proveedor)
);

--changeset ferrefix:2
INSERT INTO proveedor (rut_proveedor, dv_proveedor, nombre_proveedor, giro_proveedor, direccion_proveedor, telefono_proveedor, correo_proveedor) 
VALUES (12345678, '9', 'Ferretería Central', 'Venta de materiales', 1, '+56911111111', 'contacto@central.com');

INSERT INTO proveedor (rut_proveedor, dv_proveedor, nombre_proveedor, giro_proveedor, direccion_proveedor, telefono_proveedor, correo_proveedor) 
VALUES (87654321, 'K', 'Distribuidora Norte', 'Herramientas industriales', 2, '+56922222222', 'ventas@norte.cl');

INSERT INTO proveedor (rut_proveedor, dv_proveedor, nombre_proveedor, giro_proveedor, direccion_proveedor, telefono_proveedor, correo_proveedor) 
VALUES (11222333, '4', 'Maderas del Sur', 'Explotación forestal', 3, '+56933333333', 'info@maderasur.com');

INSERT INTO proveedor (rut_proveedor, dv_proveedor, nombre_proveedor, giro_proveedor, direccion_proveedor, telefono_proveedor, correo_proveedor) 
VALUES (44555666, '0', 'Pinturas Color', 'Químicos y revestimientos', 4, '+56944444444', 'admin@pcolor.cl');

INSERT INTO proveedor (rut_proveedor, dv_proveedor, nombre_proveedor, giro_proveedor, direccion_proveedor, telefono_proveedor, correo_proveedor) 
VALUES (99888777, '1', 'Metales S.A.', 'Fundición y forja', 5, '+56955555555', 'logistica@metales.com');