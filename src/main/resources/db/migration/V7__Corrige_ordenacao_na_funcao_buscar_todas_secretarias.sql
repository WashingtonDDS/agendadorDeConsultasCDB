CREATE OR REPLACE FUNCTION fn_BuscarTodasSecretarias()

RETURNS SETOF secretaria

LANGUAGE plpgsql

AS $$

BEGIN

    RETURN QUERY

    SELECT * FROM secretaria

    ORDER BY name ASC;

END;

$$;