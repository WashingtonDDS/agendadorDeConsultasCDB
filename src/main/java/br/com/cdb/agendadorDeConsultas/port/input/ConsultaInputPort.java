package br.com.cdb.agendadorDeConsultas.port.input;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetailsDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdateDTO;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface ConsultaInputPort {
    public Consulta createConsulta(ConsultaRequestDTO data);
    public List<ConsultaResponseDTO> getConsultas();
    public List<ConsultaResponseDTO> getUpcomingConsultas();
    public ConsultaDetailsDTO getConsultaDetails(@RequestParam("id") UUID id);
    public Consulta updateConsulta( UUID id, ConsultaUpdateDTO request);
    public Consulta canceledConsulta(UUID id);
    public void deleteConsulta(UUID id);
}
