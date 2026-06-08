-- liquibase formatted sql

-- changeset ferrefix:1
CREATE TABLE sugerencias (
    id_sugerencia BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenido_mensaje VARCHAR(1000) NOT NULL,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- changeset ferrefix:2
INSERT INTO sugerencias (contenido_mensaje) VALUES ('Excelente servicio en el mesón de pinturas.');
INSERT INTO sugerencias (contenido_mensaje) VALUES ('Falta más personal en el área de herramientas eléctricas los fines de semana.');
INSERT INTO sugerencias (contenido_mensaje) VALUES ('Sugerencia: Implementar un sistema de puntos para clientes frecuentes.');
