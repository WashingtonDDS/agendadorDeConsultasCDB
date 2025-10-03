package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.SecretariaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.ConsultaEntity;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.SecretariaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
import br.com.cdb.agendadorDeConsultas.factory.SecretariaFactoryBot;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class SecretariaRepositoryTest {

    @Autowired
    private SecretariaRepository secretariaRepository;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SecretariaMapper secretariaMapper;

    @Test
    @DisplayName("Deve salvar uma nova secretaria")
    void save_newSecretaria() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setId(null);
        SecretariaEntity secretariaEntity = new SecretariaEntity();

        when(secretariaMapper.toEntity(any(Secretaria.class))).thenReturn(secretariaEntity);
        when(secretariaMapper.toDomainEntity(any(SecretariaEntity.class))).thenReturn(secretaria);

        secretariaRepository.save(secretaria);

        verify(jdbcTemplate).update(eq("call pr_upsert_secretaria(?, ?, ?, ?, ?)"), any(UUID.class), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve atualizar uma secretaria existente")
    void save_updateSecretaria() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        SecretariaEntity secretariaEntity = new SecretariaEntity();
        secretariaEntity.setId(secretaria.getId());

        when(secretariaMapper.toEntity(any(Secretaria.class))).thenReturn(secretariaEntity);
        when(secretariaMapper.toDomainEntity(any(SecretariaEntity.class))).thenReturn(secretaria);

        secretariaRepository.save(secretaria);

        verify(jdbcTemplate).update("call pr_upsert_secretaria(?, ?, ?, ?, ?)",
                secretariaEntity.getId(),
                secretariaEntity.getName(),
                secretariaEntity.getCpf(),
                secretariaEntity.getEmail(),
                secretariaEntity.getPassword());
    }

    @Test
    @DisplayName("Deve encontrar todas as secretarias")
    void findAll() {
        String sql = "SELECT * FROM fn_BuscarTodasSecretarias()";
        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any())).thenReturn(List.of(new Secretaria()));

        List<Secretaria> result = secretariaRepository.findAll();

        assertFalse(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver secretarias")
    void findAll_empty() {
        String sql = "SELECT * FROM fn_BuscarTodasSecretarias()";
        when(jdbcTemplate.query(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any())).thenReturn(Collections.emptyList());

        List<Secretaria> result = secretariaRepository.findAll();

        assertTrue(result.isEmpty());
        verify(jdbcTemplate).query(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando houver erro no banco ao buscar todas secretarias")
    void findAll_shouldThrowExceptionOnError() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> secretariaRepository.findAll());
    }

    @Test
    @DisplayName("Deve encontrar secretaria por ID")
    void findById_found() {
        UUID id = UUID.randomUUID();
        Secretaria secretaria = SecretariaFactoryBot.build();
        String sql = "SELECT * FROM fn_find_secretaria_by_id(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(id))).thenReturn(secretaria);

        Secretaria result = secretariaRepository.findById(id);

        assertNotNull(result);
        assertEquals(secretaria, result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando secretaria não for encontrada por ID")
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        String sql = "SELECT * FROM fn_find_secretaria_by_id(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(id))).thenThrow(new EmptyResultDataAccessException(1));

        assertThrows(RuntimeException.class, () -> secretariaRepository.findById(id));
    }

    @Test
    @DisplayName("Deve deletar uma secretaria")
    void delete() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        String sql = "call pr_delete_secretaria(?)";

        secretariaRepository.delete(secretaria);

        verify(jdbcTemplate).update(sql, secretaria.getId());
    }

    @Test
    @DisplayName("Deve encontrar secretaria por email")
    void findByEmail_found() {
        String email = "test@example.com";
        Secretaria secretaria = SecretariaFactoryBot.build();
        String sql = "SELECT * FROM fn_find_secretaria_by_email(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(email))).thenReturn(secretaria);

        Optional<Secretaria> result = secretariaRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(secretaria, result.get());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio se secretaria não for encontrada por email")
    void findByEmail_notFound() {
        String email = "test@example.com";
        String sql = "SELECT * FROM fn_find_secretaria_by_email(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(email))).thenThrow(new EmptyResultDataAccessException(1));

        Optional<Secretaria> result = secretariaRepository.findByEmail(email);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve encontrar secretaria por CPF")
    void findByCpf_found() {
        String cpf = "123.456.789-00";
        Secretaria secretaria = SecretariaFactoryBot.build();
        String sql = "SELECT * FROM fn_find_secretaria_by_cpf(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(cpf))).thenReturn(secretaria);

        Optional<Secretaria> result = secretariaRepository.findByCpf(cpf);

        assertTrue(result.isPresent());
        assertEquals(secretaria, result.get());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio se secretaria não for encontrada por CPF")
    void findByCpf_notFound() {
        String cpf = "123.456.789-00";
        String sql = "SELECT * FROM fn_find_secretaria_by_cpf(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(cpf))).thenThrow(new EmptyResultDataAccessException(1));

        Optional<Secretaria> result = secretariaRepository.findByCpf(cpf);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve mapear corretamente os campos do ResultSet para Secretaria")
    void rowMapper_shouldMapResultSetCorrectly() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        UUID id = UUID.randomUUID();

        when(rs.getString("id")).thenReturn(id.toString());
        when(rs.getString("name")).thenReturn("Natasha Romanoff");
        when(rs.getString("cpf")).thenReturn("111.222.333-44");
        when(rs.getString("email")).thenReturn("natasha@avengers.com");
        when(rs.getString("password")).thenReturn("secretPassword");

        Secretaria result = secretariaRepository.getSecretariaRowMapper().mapRow(rs, 1);

        assertEquals(id, result.getId());
        assertEquals("Natasha Romanoff", result.getName());
        assertEquals("111.222.333-44", result.getCpf());
        assertEquals("natasha@avengers.com", result.getEmail());
        assertEquals("secretPassword", result.getPassword());
    }
    @Test
    @DisplayName("Deve retornar a secretaria salva com o ID gerado")
    void save_shouldReturnSavedSecretariaWithId() {
        Secretaria secretariaToSave = SecretariaFactoryBot.build();
        secretariaToSave.setId(null);

        SecretariaEntity entityWithoutId = new SecretariaEntity();
        SecretariaEntity entityWithId = new SecretariaEntity();
        entityWithId.setId(UUID.randomUUID());

        when(secretariaMapper.toEntity(secretariaToSave)).thenReturn(entityWithoutId);
        when(secretariaMapper.toDomainEntity(any(SecretariaEntity.class))).thenAnswer(invocation -> {
            SecretariaEntity savedEntity = invocation.getArgument(0);
            secretariaToSave.setId(savedEntity.getId());
            return secretariaToSave;
        });

        Secretaria savedSecretaria = secretariaRepository.save(secretariaToSave);

        assertNotNull(savedSecretaria.getId(), "O ID da secretaria retornada não deveria ser nulo.");
        assertEquals(secretariaToSave.getName(), savedSecretaria.getName());
    }

    @Test
    @DisplayName("Deve propagar exceção do banco de dados ao tentar encontrar por ID")
    void findById_shouldPropagateGenericDbError() {
        UUID id = UUID.randomUUID();
        String sql = "SELECT * FROM fn_find_secretaria_by_id(?)";

        when(jdbcTemplate.queryForObject(eq(sql), ArgumentMatchers.<RowMapper<Secretaria>>any(), eq(id)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Erro de integridade"));

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> secretariaRepository.findById(id),
                "Deveria propagar a exceção do banco de dados.");
    }

    @Test
    @DisplayName("Deve propagar exceção do banco de dados ao tentar deletar")
    void delete_shouldPropagateDbError() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        String sql = "call pr_delete_secretaria(?)";

        doThrow(new org.springframework.dao.DataIntegrityViolationException("Erro de FK"))
                .when(jdbcTemplate).update(sql, secretaria.getId());

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> secretariaRepository.delete(secretaria),
                "Deveria propagar a exceção ao falhar a deleção.");
    }

    @Test
    @DisplayName("Deve lançar exceção de acesso a dados ao tentar deletar secretaria com ID nulo")
    void delete_shouldThrowIllegalArgumentExceptionWhenIdIsNull() {

        Secretaria secretariaWithNullId = SecretariaFactoryBot.build();
        secretariaWithNullId.setId(null);

        assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class,
                () -> secretariaRepository.delete(secretariaWithNullId),
                "Deveria lançar uma exceção de acesso a dados inválido se o ID for nulo.");
    }

    @Test
    @DisplayName("Deve capturar e usar um novo UUID ao salvar nova secretaria")
    void save_newSecretaria_shouldGenerateAndUseNewId() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setId(null);
        SecretariaEntity secretariaEntity = new SecretariaEntity();

        when(secretariaMapper.toEntity(any(Secretaria.class))).thenReturn(secretariaEntity);

        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);

        secretariaRepository.save(secretaria);

        verify(jdbcTemplate).update(
                eq("call pr_upsert_secretaria(?, ?, ?, ?, ?)"),
                uuidCaptor.capture(),
                any(), any(), any(), any()
        );

        assertNotNull(uuidCaptor.getValue(), "Um novo UUID deveria ter sido gerado e passado para o jdbcTemplate.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar secretaria com erro de banco de dados")
    void save_shouldThrowExceptionOnDatabaseError() {
        // Arrange
        Secretaria secretaria = SecretariaFactoryBot.build();
        SecretariaEntity secretariaEntity = new SecretariaEntity();
        when(secretariaMapper.toEntity(secretaria)).thenReturn(secretariaEntity);

        doThrow(new RuntimeException("Database error"))
                .when(jdbcTemplate).update(eq("call pr_upsert_secretaria(?, ?, ?, ?, ?)"), any(), any(), any(), any(), any());


        assertThrows(RuntimeException.class, () -> secretariaRepository.save(secretaria));


        verify(jdbcTemplate).update(eq("call pr_upsert_secretaria(?, ?, ?, ?, ?)"), any(), any(), any(), any(), any());
    }
}
