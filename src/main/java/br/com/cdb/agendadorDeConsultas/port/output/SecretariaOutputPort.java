package br.com.cdb.agendadorDeConsultas.port.output;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecretariaOutputPort {
    Secretaria save(Secretaria secretaria);
    List<Secretaria>findAll();
    Secretaria findById(UUID id);
    void delete(Secretaria secretaria);
    Optional<Secretaria> findByEmail(String email);
    Optional<Secretaria> findByCpf(String cpf);

}
