package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        return consulta;
    };

    @Override
    public Consulta save(Consulta consulta) {
        if (consulta.getId() == null) {
            UUID id = UUID.randomUUID();
            consulta.setId(id);

            jdbcTemplate.update(
                    "INSERT INTO consulta (id, doctorname, patientname, patientnumber, speciality, description, status, consultationdatetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    id,
                    consulta.getDoctorName(),
                    consulta.getPatientName(),
                    consulta.getPatientNumber(),
                    consulta.getSpeciality(),
                    consulta.getDescription(),
                    consulta.getStatus().name(),
                    consulta.getConsultationDateTime()
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE consulta SET doctorname = ?, patientname = ?, patientnumber = ?, speciality = ?, description = ?, status = ?, consultationdatetime = ? WHERE id = ?",
                    consulta.getDoctorName(),
                    consulta.getPatientName(),
                    consulta.getPatientNumber(),
                    consulta.getSpeciality(),
                    consulta.getDescription(),
                    consulta.getStatus().name(),
                    consulta.getConsultationDateTime(),
                    consulta.getId()
            );
        }
        return consulta;
    }
    public List<Consulta> findAll() {
        String sql = "SELECT * FROM consulta";
        return jdbcTemplate.query(sql, consultaRowMapper);
    }

    public List<Consulta> findUpcomingConsultas(LocalDateTime now) {
        String sql = "SELECT * FROM consulta WHERE consultationdatetime > ?";
        return jdbcTemplate.query(sql, consultaRowMapper, now);
    }

    public Optional<Consulta> findById(UUID id) {
        String sql = "SELECT * FROM consulta WHERE id = ?";
        try {
            Consulta consulta = jdbcTemplate.queryForObject(sql, consultaRowMapper, id);
            return Optional.ofNullable(consulta);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void delete(Consulta consulta) {
        String sql = "DELETE FROM consulta WHERE id = ?";
        jdbcTemplate.update(sql, consulta.getId());
    }


}
