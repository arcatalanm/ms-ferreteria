-- liquibase formatted sql

-- changeset ferrefix:1
CREATE TABLE compras (
    id_compra BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_compra INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'SOLICITADO' NOT NULL
);

-- changeset ferrefix:2
CREATE TABLE detalles_compra (
    id_detalle_compra BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_compra_fk BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_compra_unitario INT NOT NULL,
    CONSTRAINT fk_compra_detalle FOREIGN KEY (id_compra_fk) REFERENCES compras(id_compra)
);
