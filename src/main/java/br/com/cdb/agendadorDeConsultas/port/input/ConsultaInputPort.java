package br.com.cdb.agendadorDeConsultas.port.input;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;

public interface ConsultaInputPort {
    public Consulta createConsulta(ConsultaRequestDTO data);
}
