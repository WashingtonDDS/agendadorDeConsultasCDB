CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE consulta (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    doctorName VARCHAR(100) NOT NULL,
    patientName VARCHAR(100) NOT NULL,
    patientNumber VARCHAR(20) NOT NULL,
    speciality VARCHAR(100) NOT NULL,
    description VARCHAR(300) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    consultationDateTime TIMESTAMP NOT NULL
);