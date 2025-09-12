CREATE OR REPLACE PROCEDURE pr_upsert_secretaria(
    p_id UUID,
    p_nome VARCHAR,
    p_cpf VARCHAR,
    p_email VARCHAR,
    p_password_hash VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO secretaria (id, nome, cpf, email, password)
    VALUES (p_id, p_nome, p_cpf, p_email, p_password_hash)
    ON CONFLICT (id) DO UPDATE SET
        nome = p_nome,
        email = p_email;
END;
$$;

CREATE OR REPLACE FUNCTION fn_BuscarTodasSecretarias()
RETURNS SETOF secretaria
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM secretaria ORDER BY nome ASC;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_secretaria_by_id(p_id UUID)
RETURNS SETOF secretaria
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM secretaria WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE pr_delete_secretaria(p_id UUID)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM secretaria WHERE id = p_id;
END;
$$;