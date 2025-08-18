package br.com.cdb.agendadorDeConsultas.repositories;

import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {
    @Query("SELECT e FROM Consulta e WHERE e.consultationDateTime >= :currentDate")
    public List<Consulta> findUpcomingConsultas(@Param("currentDate") LocalDateTime currentDate);
}
