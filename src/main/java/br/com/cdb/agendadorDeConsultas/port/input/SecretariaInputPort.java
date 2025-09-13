package br.com.cdb.agendadorDeConsultas.port.input;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;

import java.util.List;
import java.util.UUID;

public interface SecretariaInputPort {

    Secretaria create(Secretaria secretaria);
    List<Secretaria> findAll();
    Secretaria findById(UUID id);
    void delete(UUID id);
    Secretaria update(UUID id, SecretariaUpdate secretaria);
}
