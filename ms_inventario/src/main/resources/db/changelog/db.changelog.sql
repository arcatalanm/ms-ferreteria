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

--changeset ferrefix:4
INSERT INTO categoria_producto (nombre_categoria) VALUES 
('Herramientas Eléctricas'), ('Herramientas Manuales'), ('Obra Gruesa'), 
('Gasfitería'), ('Electricidad'), ('Pinturas'), ('Adhesivos y Sellantes'), 
('Fijaciones'), ('Maderas y Tableros'), ('Techumbre'), ('Cerámicas y Pisos'), 
('Aislantes'), ('Seguridad Industrial'), ('Jardinería'), ('Iluminación');

--changeset ferrefix:5
INSERT INTO unidad_medida (nombre_unidad_medida) VALUES 
('Unidad'), ('Par'), ('Caja (100u)'), ('Saco (25kg)'), ('Litro'), 
('Galón (3.78L)'), ('Tineta (15L)'), ('Metro'), ('Tira (3m)'), ('Pack (6u)');

--changeset ferrefix:6
INSERT INTO producto (id_categoria, id_unidad_medida, codigo_barras_producto, nombre_producto, stock_producto, precio_venta_producto) VALUES 
(1, 1, '780123450001', 'Taladro Percutor Makita 710W', 15, 65000),
(1, 1, '780123450002', 'Esmeril Angular Bosch 4 1/2', 20, 42000),
(2, 1, '780123450003', 'Martillo Carpintero Stanley 16oz', 45, 12500),
(2, 1, '780123450004', 'Huincha de Medir 5m', 100, 3500),
(3, 4, '780123450005', 'Cemento Melón Especial 25kg', 250, 4500),
(3, 4, '780123450006', 'Yeso Construcción 25kg', 120, 3800),
(4, 1, '780123450007', 'Llave de Paso Bola 1/2 SO', 80, 3200),
(4, 9, '780123450008', 'Tubo PVC Hidráulico 20mm', 150, 2100),
(5, 8, '780123450009', 'Cable THHN 2.5mm Rojo (Metro)', 500, 450),
(5, 1, '780123450010', 'Enchufe Doble Alveo Blanco', 60, 2500),
(6, 6, '780123450011', 'Esmalte al Agua Tricolor Blanco', 30, 18500),
(6, 7, '780123450012', 'Látex Constructor Ceresita', 15, 35000),
(7, 1, '780123450013', 'Silicona Transparente Agorex', 85, 4900),
(7, 1, '780123450014', 'Adhesivo Montaje No Mas Clavos', 50, 5500),
(8, 3, '780123450015', 'Tornillo Volcanita 1 5/8', 200, 2500),
(8, 3, '780123450016', 'Clavo Techo 2 1/2', 150, 3200),
(9, 1, '780123450017', 'Placa OSB 10mm', 90, 11500),
(9, 1, '780123450018', 'Terciado Estructural 15mm', 70, 18000),
(10, 1, '780123450019', 'Plancha Zinc Acanalado 0.35mm', 120, 8500),
(10, 1, '780123450020', 'Fieltro Asfáltico 10/40', 25, 14000),
(11, 3, '780123450021', 'Cerámica Piso 45x45 Madera', 40, 15000),
(11, 4, '780123450022', 'Bekron AC 25kg', 180, 5500),
(12, 1, '780123450023', 'Lana de Vidrio 40mm (Rollo)', 35, 22000),
(13, 2, '780123450024', 'Guantes Cabritilla Cortos', 150, 2500),
(13, 1, '780123450025', 'Casco Seguridad Blanco 3M', 45, 6500),
(14, 1, '780123450026', 'Manguera Riego 15m', 30, 12000),
(14, 1, '780123450027', 'Tijera Podadora Truper', 25, 14500),
(15, 1, '780123450028', 'Foco Proyector LED 50W', 40, 9500),
(15, 10, '780123450029', 'Ampolleta LED 9W Luz Fría', 100, 6000),
(1, 1, '780123450030', 'Sierra Circular DeWalt 7 1/4', 10, 89000);

