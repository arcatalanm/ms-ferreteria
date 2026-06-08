-- liquibase formatted sql

-- changeset ferrefix:1
CREATE TABLE maquinas_arriendo (
    id_equipo INT AUTO_INCREMENT PRIMARY KEY,
    codigo_interno VARCHAR(50) NOT NULL UNIQUE,
    nombre_maquina VARCHAR(100) NOT NULL,
    precio_por_dia INT NOT NULL,
    estado VARCHAR(30) DEFAULT 'DISPONIBLE' NOT NULL,
    run_cliente INT NULL,
    fecha_devolucion_pactada DATE NULL
);

-- changeset ferrefix:2
INSERT INTO maquinas_arriendo (codigo_interno, nombre_maquina, precio_por_dia, estado) 
VALUES ('BET-001', 'Betonera 130 Lts', 15000, 'DISPONIBLE');

INSERT INTO maquinas_arriendo (codigo_interno, nombre_maquina, precio_por_dia, estado) 
VALUES ('ROT-001', 'Rotomartillo Bosch', 8000, 'DISPONIBLE');

INSERT INTO maquinas_arriendo (codigo_interno, nombre_maquina, precio_por_dia, estado) 
VALUES ('GEN-001', 'Generador 2500W', 25000, 'DISPONIBLE');
