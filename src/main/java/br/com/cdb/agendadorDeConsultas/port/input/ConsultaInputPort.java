package br.com.cdb.agendadorDeConsultas.port.input;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;


import java.util.List;
import java.util.UUID;

public interface ConsultaInputPort {
    public Consulta createConsulta(UUID secretariaId,Consulta Consulta);
    public List<Consulta> getConsultas();
    public List<Consulta> getUpcomingConsultas();
    public Consulta getConsultaDetails( UUID id);
    public Consulta updateConsulta( UUID secretariaId,UUID id, ConsultaUpdate request);
    public Consulta canceledConsulta(UUID secretariaId,UUID id);
    public void deleteConsulta(UUID secretariaId,UUID id);
    public Consulta createFollowUpConsulta(UUID secretariaId, UUID originalConsultaId);
}
