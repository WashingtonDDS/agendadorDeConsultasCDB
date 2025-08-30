package br.com.cdb.agendadorDeConsultas.port.input;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface ConsultaInputPort {
    public Consulta createConsulta(ConsultaRequest data);
    public List<Consulta> getConsultas();
    public List<Consulta> getUpcomingConsultas();
    public Consulta getConsultaDetails(@RequestParam("id") UUID id);
    public Consulta updateConsulta( UUID id, ConsultaUpdate request);
    public Consulta canceledConsulta(UUID id);
    public void deleteConsulta(UUID id);
}
