
ALTER TABLE secretaria RENAME COLUMN nome TO name;

CREATE OR REPLACE PROCEDURE pr_upsert_secretaria(
    p_id UUID,
    p_name VARCHAR,
    p_cpf VARCHAR,
    p_email VARCHAR,
    p_password_hash VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO secretaria (id, name, cpf, email, password)
    VALUES (p_id, p_name, p_cpf, p_email, p_password_hash)
    ON CONFLICT (id) DO UPDATE SET
        name = p_name,
        email = p_email;
END;
$$;