--liquibase formatted sql

--changeset ferrefix:1
CREATE TABLE direccion (
    id_direccion BIGINT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    departamento VARCHAR(20),
    comuna VARCHAR(50) NOT NULL,
    ciudad VARCHAR(50) NOT NULL
);

--changeset ferrefix:2
INSERT INTO direccion (calle, numero, departamento, comuna, ciudad) VALUES 
('Av. Los Libertadores', 1204, 'Local Ferrefix', 'Rancagua', 'Rancagua'),
('Gran Avenida', 5530, NULL, 'San Miguel', 'Santiago'),
('Av. Los Pajaritos', 3100, 'Bodega 4B', 'Maipú', 'Santiago'),
('Av. Concha y Toro', 1050, NULL, 'Puente Alto', 'Santiago'),
('Av. Providencia', 2550, 'Oficina 102', 'Providencia', 'Santiago'),
('Av. Apoquindo', 4500, 'Depto 33', 'Las Condes', 'Santiago'),
('Av. Irarrázaval', 2100, NULL, 'Ñuñoa', 'Santiago'),
('Av. Macul', 3300, 'Casa 4', 'Macul', 'Santiago'),
('Av. La Florida', 7800, NULL, 'La Florida', 'Santiago'),
('Av. Ossa', 1500, 'Depto 12C', 'La Reina', 'Santiago'),
('Av. Independencia', 850, NULL, 'Independencia', 'Santiago'),
('Av. Recoleta', 1100, 'Local 5', 'Recoleta', 'Santiago'),
('Av. Américo Vespucio', 150, NULL, 'Cerrillos', 'Santiago'),
('Av. Pedro de Valdivia', 3200, 'Casa 10', 'Ñuñoa', 'Santiago'),
('Av. Las Condes', 8900, 'Oficina 401', 'Las Condes', 'Santiago');