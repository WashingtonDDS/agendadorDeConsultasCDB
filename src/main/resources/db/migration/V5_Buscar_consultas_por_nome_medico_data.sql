
CREATE OR REPLACE FUNCTION fn_find_consultas_by_doctor_and_datetime(
    p_doctor_name VARCHAR,
    p_datetime TIMESTAMP
)
RETURNS SETOF consulta

LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM consulta
    WHERE doctorName = p_doctor_name AND consultationDateTime = p_datetime;
END;
$$;