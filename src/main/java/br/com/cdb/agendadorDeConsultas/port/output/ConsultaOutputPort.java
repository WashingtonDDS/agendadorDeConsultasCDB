package br.com.cdb.agendadorDeConsultas.port.output;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;

public interface ConsultaOutputPort {
    Consulta save(Consulta consulta);
}
