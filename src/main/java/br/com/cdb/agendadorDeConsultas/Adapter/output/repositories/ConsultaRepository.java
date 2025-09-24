package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.ConsultaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConsultaRepository implements ConsultaOutputPort {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ConsultaRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ConsultaMapper consultaMapper;

    private final RowMapper<Consulta> consultaRowMapper = (rs, rowNum) -> {
        Consulta consulta = new Consulta();
        consulta.setId(UUID.fromString(rs.getString("id")));
        consulta.setDoctorName(rs.getString("doctorname"));
        consulta.setPatientName(rs.getString("patientname"));
        consulta.setPatientNumber(rs.getString("patientnumber"));
        consulta.setSpeciality(rs.getString("speciality"));
        consulta.setDescription(rs.getString("description"));
        consulta.setStatus(StatusConsulta.valueOf(rs.getString("status")));
        consulta.setConsultationDateTime(rs.getTimestamp("consultationdatetime").toLocalDateTime());
        consulta.setSecretariaId(UUID.fromString(rs.getString("secretaria_id")));
        return consulta;
    };

    public ConsultaRepository(ConsultaMapper consultaMapper) {
        this.consultaMapper = consultaMapper;
    }


    @Override
    public Consulta save(Consulta consulta) {
        ConsultaEntity consultaEntity = consultaMapper.toEntity(consulta);
        if (consultaEntity.getId() == null) {
            UUID id = UUID.randomUUID();
            consultaEntity.setId(id);

            logger.info("Inserindo nova consulta com id {}", id);

            jdbcTemplate.update(
                    "call pr_upsert_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    id,
                    consultaEntity.getDoctorName(),
                    consultaEntity.getPatientName(),
                    consultaEntity.getPatientNumber(),
                    consultaEntity.getSpeciality(),
                    consultaEntity.getDescription(),
                    consultaEntity.getStatus().name(),
                    consultaEntity.getConsultationDateTime(),
                    consultaEntity.getSecretariaId()
            );
        } else {

            logger.info("Atualizando consulta com id {}", consultaEntity.getId());

            jdbcTemplate.update(
                    "call pr_upsert_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    consultaEntity.getId(),
                    consultaEntity.getDoctorName(),
                    consultaEntity.getPatientName(),
                    consultaEntity.getPatientNumber(),
                    consultaEntity.getSpeciality(),
                    consultaEntity.getDescription(),
                    consultaEntity.getStatus().name(),
                    consultaEntity.getConsultationDateTime(),
                    consultaEntity.getSecretariaId()

            );
        }
        return consultaMapper.toDomainEntity(consultaEntity);
    }

    public List<Consulta> findAll() {

        logger.debug("Buscando todas as consultas via fn_BuscarTodasConsultas()");

        String sql = "SELECT * FROM fn_BuscarTodasConsultas()";
        return jdbcTemplate.query(sql, consultaRowMapper);
    }

    public List<Consulta> findUpcomingConsultas(LocalDateTime now) {

        logger.debug("Buscando consultas futuras a partir de {}", now);

        String sql = "SELECT * FROM fn_find_upcoming_consultas(?)";
        return jdbcTemplate.query(sql, consultaRowMapper, now);
    }

    public Optional<Consulta> findById(UUID id) {
        String sql = "SELECT * FROM fn_find_consulta_by_id(?)";
        try {
            logger.debug("Buscando consulta com id {}", id);

            Consulta consulta = jdbcTemplate.queryForObject(sql, consultaRowMapper, id);
            return Optional.ofNullable(consulta);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Nenhuma consulta encontrada com id {}", id);

            return Optional.empty();
        }
    }

    public void delete(Consulta consulta) {
        logger.info("Deletando consulta com id {}", consulta.getId());

        String sql = "call pr_delete_consulta(?)";
        jdbcTemplate.update(sql, consulta.getId());
    }

    @Override
    public List<Consulta> findByDoctorNameAndDateTime(String doctorName, LocalDateTime dateTime) {
        logger.debug("Buscando consultas para o médico {} na data/hora {}", doctorName, dateTime);

        String sql = "SELECT * FROM fn_find_consultas_by_doctor_and_datetime(?, ?)";

        return jdbcTemplate.query(sql, consultaRowMapper, doctorName, dateTime);
    }


}
