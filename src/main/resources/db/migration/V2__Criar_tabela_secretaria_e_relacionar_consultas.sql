CREATE TABLE secretaria (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);


ALTER TABLE consulta
ADD COLUMN secretaria_id UUID,
ADD CONSTRAINT fk_consulta_secretaria
    FOREIGN KEY (secretaria_id)
    REFERENCES secretaria(id);


CREATE OR REPLACE PROCEDURE pr_upsert_consulta(
    IN p_id UUID,
    IN p_doctorname VARCHAR,
    IN p_patientname VARCHAR,
    IN p_patientnumber VARCHAR,
    IN p_speciality VARCHAR,
    IN p_description VARCHAR,
    IN p_status VARCHAR,
    IN p_consultationdatetime TIMESTAMP,
    IN p_secretaria_id UUID
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO consulta (
        id, doctorname, patientname, patientnumber, speciality, description, status, consultationdatetime, secretaria_id
    ) VALUES (
        p_id, p_doctorname, p_patientname, p_patientnumber, p_speciality, p_description, p_status, p_consultationdatetime, p_secretaria_id
    )
    ON CONFLICT (id) DO UPDATE SET
        doctorname = p_doctorname,
        patientname = p_patientname,
        patientnumber = p_patientnumber,
        speciality = p_speciality,
        description = p_description,
        status = p_status,
        consultationdatetime = p_consultationdatetime,
        secretaria_id = p_secretaria_id;
END;
$$;


CREATE OR REPLACE PROCEDURE pr_delete_consulta(p_id UUID)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM consulta WHERE id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_consulta_by_id(p_id UUID)
RETURNS SETOF consulta
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM consulta WHERE id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_upcoming_consultas(p_now TIMESTAMP)
RETURNS SETOF consulta
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM consulta WHERE consultationdatetime > p_now ORDER BY consultationdatetime ASC;
END;
$$;





