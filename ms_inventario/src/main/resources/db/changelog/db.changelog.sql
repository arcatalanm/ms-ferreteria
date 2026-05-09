--liquibase formatted sql

-- changeset ferrefix:1
CREATE TABLE categoria_producto (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(50) NOT NULL UNIQUE
);

-- changeset ferrefix:2
CREATE TABLE unidad_medida (
    id_unidad_medida INT AUTO_INCREMENT PRIMARY KEY,
    nombre_unidad_medida VARCHAR(50) NOT NULL UNIQUE
);

-- changeset ferrefix:3
CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT NOT NULL,
    id_unidad_medida INT NOT NULL,
    codigo_barras_producto VARCHAR(50) UNIQUE,
    nombre_producto VARCHAR(100) NOT NULL,
    stock_producto INT NOT NULL,
    precio_venta_producto INT NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria_producto(id_categoria),
    CONSTRAINT fk_producto_unidad FOREIGN KEY (id_unidad_medida) REFERENCES unidad_medida(id_unidad_medida)
);

-- changeset ferrefix:4
    INSERT INTO categoria_producto (nombre_categoria) VALUES 
    ('Herramientas Manuales'),
    ('Materiales de Construcción'),
    ('Electricidad'),
    ('Plomería'),
    ('Pinturas');

-- changeset ferrefix:5
    INSERT INTO unidad_medida (nombre_unidad_medida) VALUES 
    ('Pieza'),
    ('Kilogramo'),
    ('Metro'),
    ('Litro'),
    ('Bolsa');

-- changeset ferrefix:6
    INSERT INTO producto (id_categoria, id_unidad_medida, codigo_barras_producto, nombre_producto, stock_producto, precio_venta_producto) VALUES 
    (1, 1, '7501234567890', 'Martillo de Uña 16oz', 50, 150),
    (2, 5, '7501234567891', 'Bulto de Cemento Gris 50kg', 100, 220),
    (3, 3, '7501234567892', 'Cable Eléctrico Calibre 12 (10m)', 30, 450),
    (4, 1, '7501234567893', 'Llave Mezcladora para Fregadero', 15, 890),
    (5, 4, '7501234567894', 'Pintura Vinílica Blanca 4L', 20, 560);

