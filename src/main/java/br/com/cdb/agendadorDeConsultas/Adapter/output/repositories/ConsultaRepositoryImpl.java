package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultaRepositoryImpl implements ConsultaOutputPort {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Consulta save (Consulta consulta){return null;}
}
