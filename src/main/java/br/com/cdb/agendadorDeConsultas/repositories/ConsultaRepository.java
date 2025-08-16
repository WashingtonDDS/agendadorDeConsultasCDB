package br.com.cdb.agendadorDeConsultas.repositories;

import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {
}
