CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE consulta (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    doctorName VARCHAR(100) NOT NULL,
    patientName VARCHAR(100) NOT NULL,
    PatientNumber VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(300) NOT NULL,
    date TIMESTAMP NOT NULL
);