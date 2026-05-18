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

--changeset ferrefix:3
INSERT INTO proveedor (rut_proveedor, dv_proveedor, nombre_proveedor, giro_proveedor, direccion_proveedor, telefono_proveedor, correo_proveedor) VALUES 
(76123456, '9', 'Comercializadora Makita Chile', 'Herramientas Eléctricas', 1, '+56221112222', 'ventas@makita.cl'),
(96765432, 'K', 'Cementos Bío Bío S.A.', 'Materiales de Construcción', 2, '+56223334444', 'contacto@cbb.cl'),
(77222333, '4', 'Pinturas Tricolor S.A.', 'Pinturas y Revestimientos', 3, '+56225556666', 'pedidos@tricolor.cl'),
(88555666, '0', 'Hoffens S.A.', 'Fijaciones y Plásticos', 4, '+56227778888', 'ventas@hoffens.cl'),
(99888777, '1', 'Nibsa S.A.', 'Grifería y Válvulas', 5, '+56229990000', 'comercial@nibsa.cl'),
(76555444, '3', 'Sodimac Mayorista', 'Ferretería General', 6, '+56221234567', 'empresas@sodimac.cl'),
(90111222, '5', 'CMPC Maderas S.A.', 'Maderas y Tableros', 7, '+56229876543', 'ventas@cmpc.cl'),
(81222333, '7', 'Cerámicas Cordillera', 'Revestimientos', 8, '+56223456789', 'ventas@cordillera.cl'),
(78444555, '2', 'Volcán S.A.', 'Yeso y Soluciones Constructivas', 9, '+56227654321', 'info@volcan.cl'),
(92333444, '8', 'Bosch Chile', 'Herramientas y Accesorios', 10, '+56225432198', 'ventas.herramientas@bosch.cl');