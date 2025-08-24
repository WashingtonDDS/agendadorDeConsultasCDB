package br.com.cdb.agendadorDeConsultas.port.output;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaOutputPort {
    Consulta save(Consulta consulta);
    List<Consulta> findAll();
    List<Consulta> findUpcomingConsultas(LocalDateTime now);
    Optional<Consulta> findById(UUID id);
    void delete(Consulta consulta);
}
