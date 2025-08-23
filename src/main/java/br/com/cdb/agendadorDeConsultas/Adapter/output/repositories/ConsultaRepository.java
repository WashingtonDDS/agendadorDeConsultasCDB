package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.adapter.output.entity.ConsultaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaRepository {
    ConsultaEntity save(ConsultaEntity consultaEntity);

    Optional<ConsultaEntity> findById(UUID uuid);

    List<ConsultaEntity> findAll();

    void deleteById(UUID uuid);
}
