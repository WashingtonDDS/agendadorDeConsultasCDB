package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.ConsultaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class ConsultaRepositoryTest {

    @Autowired
    private ConsultaRepository consultaRepository;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private ConsultaMapper consultaMapper;

    @Test
    @DisplayName("Deve salvar uma nova consulta")
    void save_newConsulta() {
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setId(null);
        ConsultaEntity consultaEntity = new ConsultaEntity();

        when(consultaMapper.toEntity(any(Consulta.class))).thenReturn(consultaEntity);
        when(consultaMapper.toDomainEntity(any(ConsultaEntity.class))).thenReturn(consulta);

        consultaRepository.save(consulta);

        verify(jdbcTemplate).update(eq("call pr_upsert_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?)"), any(UUID.class), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve atualizar uma consulta existente")
    void save_updateConsulta() {
        Consulta consulta = ConsultaFactoryBot.build();
        ConsultaEntity consultaEntity = new ConsultaEntity();
        consultaEntity.setId(consulta.getId());

        when(consultaMapper.toEntity(any(Consulta.class))).thenReturn(consultaEntity);
        when(consultaMapper.toDomainEntity(any(ConsultaEntity.class))).thenReturn(consulta);

        consultaRepository.save(consulta);

        verify(jdbcTemplate).update("call pr_upsert_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                consultaEntity.getId(),
                consultaEntity.getDoctorName(),
                consultaEntity.getPatientName(),
                consultaEntity.getPatientNumber(),
                consultaEntity.getSpeciality(),
                consultaEntity.getDescription(),
                consultaEntity.getStatus().name(),
                consultaEntity.getConsultationDateTime(),
                consultaEntity.getSecretariaId());
    }

    @Test
    @DisplayName("Deve encontrar todas as consultas")
    void findAll() {
        String sql = "SELECT * FROM fn_BuscarTodasConsultas()";
        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any())).thenReturn(List.of(new Consulta()));

        List<Consulta> result = consultaRepository.findAll();

        assertFalse(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver consultas")
    void findAll_empty() {
        String sql = "SELECT * FROM fn_BuscarTodasConsultas()";
        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any())).thenReturn(Collections.emptyList());

        List<Consulta> result = consultaRepository.findAll();

        assertTrue(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando houver erro no banco ao buscar todas consultas")
    void findAll_shouldThrowExceptionOnError() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> consultaRepository.findAll());
    }

    @Test
    @DisplayName("Deve encontrar consultas futuras")
    void findUpcomingConsultas() {
        LocalDateTime now = LocalDateTime.now();
        String sql = "SELECT * FROM fn_find_upcoming_consultas(?)";
        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(now))).thenReturn(List.of(new Consulta()));

        List<Consulta> result = consultaRepository.findUpcomingConsultas(now);

        assertFalse(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(now));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver consultas futuras")
    void findUpcomingConsultas_empty() {
        LocalDateTime now = LocalDateTime.now();
        String sql = "SELECT * FROM fn_find_upcoming_consultas(?)";
        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(now))).thenReturn(Collections.emptyList());

        List<Consulta> result = consultaRepository.findUpcomingConsultas(now);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(now));
    }

    @Test
    @DisplayName("Deve encontrar consulta por ID")
    void findById_found() {
        UUID id = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        String sql = "SELECT * FROM fn_find_consulta_by_id(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(id))).thenReturn(consulta);

        Optional<Consulta> result = consultaRepository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(consulta, result.get());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio se consulta não for encontrada por ID")
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        String sql = "SELECT * FROM fn_find_consulta_by_id(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(id))).thenThrow(new EmptyResultDataAccessException(1));

        Optional<Consulta> result = consultaRepository.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar uma consulta")
    void delete() {
        Consulta consulta = ConsultaFactoryBot.build();
        String sql = "call pr_delete_consulta(?)";

        consultaRepository.delete(consulta);

        verify(jdbcTemplate).update(sql, consulta.getId());
    }

    @Test
    @DisplayName("Deve encontrar consultas por nome do médico e data/hora")
    void findByDoctorNameAndDateTime() {
        String doctorName = "Dr. House";
        LocalDateTime dateTime = LocalDateTime.now();
        String sql = "SELECT * FROM fn_find_consultas_by_doctor_and_datetime(?, ?)";

        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(doctorName), eq(dateTime))).thenReturn(List.of(new Consulta()));

        List<Consulta> result = consultaRepository.findByDoctorNameAndDateTime(doctorName, dateTime);

        assertFalse(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(doctorName), eq(dateTime));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver consultas para o médico e data/hora")
    void findByDoctorNameAndDateTime_empty() {
        String doctorName = "Dr. House";
        LocalDateTime dateTime = LocalDateTime.now();
        String sql = "SELECT * FROM fn_find_consultas_by_doctor_and_datetime(?, ?)";

        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(doctorName), eq(dateTime))).thenReturn(Collections.emptyList());

        List<Consulta> result = consultaRepository.findByDoctorNameAndDateTime(doctorName, dateTime);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(doctorName), eq(dateTime));
    }

    @Test
    @DisplayName("Deve mapear corretamente os campos do ResultSet para Consulta")
    void rowMapper_shouldMapResultSetCorrectly() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        UUID id = UUID.randomUUID();
        UUID secretariaId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.now();

        when(rs.getString("id")).thenReturn(id.toString());
        when(rs.getString("doctorname")).thenReturn("Dr. Strange");
        when(rs.getString("patientname")).thenReturn("Tony Stark");
        when(rs.getString("patientnumber")).thenReturn("12345");
        when(rs.getString("speciality")).thenReturn("Cardiology");
        when(rs.getString("description")).thenReturn("Check-up");
        when(rs.getString("status")).thenReturn("AGENDADA");
        when(rs.getTimestamp("consultationdatetime")).thenReturn(Timestamp.valueOf(dateTime));
        when(rs.getString("secretaria_id")).thenReturn(secretariaId.toString());

        Consulta result = consultaRepository.getConsultaRowMapper().mapRow(rs, 1);

        assertEquals(id, result.getId());
        assertEquals("Dr. Strange", result.getDoctorName());
        assertEquals("Tony Stark", result.getPatientName());
        assertEquals(StatusConsulta.AGENDADA, result.getStatus());
        assertEquals(dateTime, result.getConsultationDateTime());
        assertEquals(secretariaId, result.getSecretariaId());
    }
    @Test
    @DisplayName("Deve retornar a consulta salva com o ID gerado")
    void save_shouldReturnSavedConsultaWithId() {
        Consulta consultaToSave = ConsultaFactoryBot.build();
        consultaToSave.setId(null);

        ConsultaEntity entityWithoutId = new ConsultaEntity();

        when(consultaMapper.toEntity(consultaToSave)).thenReturn(entityWithoutId);
        when(consultaMapper.toDomainEntity(any(ConsultaEntity.class))).thenAnswer(invocation -> {
            ConsultaEntity savedEntity = invocation.getArgument(0);
            consultaToSave.setId(savedEntity.getId());
            return consultaToSave;
        });

        Consulta savedConsulta = consultaRepository.save(consultaToSave);

        assertNotNull(savedConsulta.getId());
    }

    @Test
    @DisplayName("Deve capturar e usar um novo UUID ao salvar nova consulta")
    void save_newConsulta_shouldGenerateAndUseNewId() {
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setId(null);
        ConsultaEntity consultaEntity = new ConsultaEntity();

        when(consultaMapper.toEntity(any(Consulta.class))).thenReturn(consultaEntity);

        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);

        consultaRepository.save(consulta);

        verify(jdbcTemplate).update(
                eq("call pr_upsert_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?)"),
                uuidCaptor.capture(),
                any(), any(), any(), any(), any(), any(), any(), any()
        );

        assertNotNull(uuidCaptor.getValue());
    }

    @Test
    @DisplayName("Deve propagar exceção do banco de dados ao salvar")
    void save_shouldPropagateDbError() {
        Consulta consulta = ConsultaFactoryBot.build();
        ConsultaEntity consultaEntity = new ConsultaEntity();

        when(consultaMapper.toEntity(consulta)).thenReturn(consultaEntity);
        doThrow(new org.springframework.dao.DataIntegrityViolationException("Erro de DB"))
                .when(jdbcTemplate).update(anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any());

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> consultaRepository.save(consulta));
    }

    @Test
    @DisplayName("Deve propagar exceção do banco de dados ao buscar por ID")
    void findById_shouldPropagateDbError() {
        UUID id = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Consulta>>any(), eq(id)))
                .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("DB offline"));

        assertThrows(org.springframework.dao.DataAccessResourceFailureException.class,
                () -> consultaRepository.findById(id));
    }

    @Test
    @DisplayName("Deve lançar exceção de acesso a dados ao tentar deletar consulta com ID nulo")
    void delete_shouldThrowExceptionWhenIdIsNull() {

        Consulta consultaWithNullId = ConsultaFactoryBot.build();
        consultaWithNullId.setId(null);

        assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class,
                () -> consultaRepository.delete(consultaWithNullId));
    }

    @Test
    @DisplayName("Deve propagar exceção do banco de dados ao deletar")
    void delete_shouldPropagateDbError() {
        Consulta consulta = ConsultaFactoryBot.build();

        doThrow(new org.springframework.dao.DataIntegrityViolationException("Erro de FK"))
                .when(jdbcTemplate).update(anyString(), eq(consulta.getId()));

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> consultaRepository.delete(consulta));
    }
}
