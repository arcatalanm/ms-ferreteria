-- liquibase formatted sql

-- changeset ferrefix:1
CREATE TABLE marcas (
    id_marca INT AUTO_INCREMENT PRIMARY KEY,
    nombre_marca VARCHAR(100) NOT NULL
);

-- changeset ferrefix:2
INSERT INTO marcas (nombre_marca) VALUES ('Bosch');
INSERT INTO marcas (nombre_marca) VALUES ('Stanley');
INSERT INTO marcas (nombre_marca) VALUES ('Makita');
INSERT INTO marcas (nombre_marca) VALUES ('DeWalt');
INSERT INTO marcas (nombre_marca) VALUES ('Black+Decker');
INSERT INTO marcas (nombre_marca) VALUES ('Milwaukee');
INSERT INTO marcas (nombre_marca) VALUES ('Hilti');
INSERT INTO marcas (nombre_marca) VALUES ('Truper');
INSERT INTO marcas (nombre_marca) VALUES ('Irwin');
INSERT INTO marcas (nombre_marca) VALUES ('Einhell');
