package br.com.cdb.agendadorDeConsultas.adapter.output.repositories;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.SecretariaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.SecretariaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SecretariaRepository implements SecretariaOutputPort {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SecretariaRepository.class);

    private JdbcTemplate jdbcTemplate;

    private final SecretariaMapper secretariaMapper;

    public SecretariaRepository(SecretariaMapper secretariaMapper) {
        this.secretariaMapper = secretariaMapper;
    }

    private final RowMapper<Secretaria> secretariaRowMapper = (rs, rowNum) -> {
        Secretaria secretaria = new Secretaria();
        secretaria.setId(UUID.fromString(rs.getString("id")));
        secretaria.setNome(rs.getString("nome"));
        secretaria.setEmail(rs.getString("email"));
        return secretaria;
    };


    @Override
    public Secretaria save(Secretaria secretaria) {
        SecretariaEntity secretariaEntity = secretariaMapper.toEntity(secretaria);
        if (secretariaEntity.getId() == null) {
            UUID id = UUID.randomUUID();
            secretariaEntity.setId(id);

            logger.info("Inserindo nova Secretaria com id {}", id);
            jdbcTemplate.update(
                    "call pr_upsert_secretaria(?, ?, ?)",
                    id,
                    secretariaEntity.getNome(),
                    secretariaEntity.getEmail()
            );
        }else {
            logger.info("Atualizando Secretaria com id {}", secretariaEntity.getId());
            jdbcTemplate.update(
                    "call pr_upsert_secretaria(?, ?, ?)",
                    secretariaEntity.getId(),
                    secretariaEntity.getNome(),
                    secretariaEntity.getEmail()
            );
        }
        return secretariaMapper.toDomainEntity(secretariaEntity);
    }

    @Override
    public List<Secretaria> findAll() {

        logger.debug("Buscando todas as consultas via fn_BuscarTodasSecretarias()");
        String sql = "SELECT * FROM fn_BuscarTodasSecretarias()";
        return jdbcTemplate.query(sql, secretariaRowMapper);
    }

    @Override

    public Secretaria findById(UUID id) {

        String sql = "SELECT * FROM fn_find_secretaria_by_id(?)";
        try {
            logger.debug("Buscando secretaria com id {}", id);
            return jdbcTemplate.queryForObject(sql, secretariaRowMapper, id);

        } catch (EmptyResultDataAccessException e) {
            logger.warn("Nenhuma secretaria encontrada com id {}", id);
            throw new RuntimeException("Recurso não encontrado: Secretaria com id " + id); // Ou uma exceção customizada

        }

    }

    @Override
    public void delete(Secretaria secretaria) {
        logger.info("Deletando secretaria com id {}",secretaria.getId());

        String sql = "call pr_delete_secretaria(?)";
        jdbcTemplate.update(sql, secretaria.getId());
    }

}
