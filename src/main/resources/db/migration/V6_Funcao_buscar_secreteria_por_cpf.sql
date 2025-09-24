CREATE OR REPLACE FUNCTION fn_find_secretaria_by_cpf(p_cpf VARCHAR)
RETURNS SETOF secretaria
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM secretaria
    WHERE cpf = p_cpf
    LIMIT 1;
END;
$$;