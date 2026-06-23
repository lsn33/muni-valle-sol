CREATE TABLE brigada (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
    tipo VARCHAR(50) NOT NULL,
    latitud FLOAT,
    longitud FLOAT,
    email_responsable VARCHAR(150) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);