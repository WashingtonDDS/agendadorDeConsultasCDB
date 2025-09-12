package br.com.cdb.agendadorDeConsultas.port.output;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;

import java.util.List;
import java.util.UUID;

public interface SecretariaOutputPort {
    Secretaria save(Secretaria secretaria);
    List<Secretaria>findAll();
    Secretaria findById(Long id);
    void delete(UUID id);
    Secretaria update(UUID id, Secretaria secretaria);
}
