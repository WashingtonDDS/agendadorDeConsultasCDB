package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.SecretariaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.factory.SecretariaFactoryBot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecretariaMapperTest {

    @Autowired
    private SecretariaMapper secretariaMapper;

    @Test
    @DisplayName("Deve mapear SecretariaRequest para Secretaria Domain")
    void toDomainFromRequest() {
        SecretariaRequest request = SecretariaFactoryBot.buildRequest();

        Secretaria domain = secretariaMapper.toDomain(request);

        assertNotNull(domain);
        assertEquals(request.name(), domain.getName());
        assertEquals(request.email(), domain.getEmail());
        assertEquals(request.password(), domain.getPassword());
    }

    @Test
    @DisplayName("Deve mapear SecretariaEntity para Secretaria Domain")
    void toDomainFromEntity() {
        SecretariaEntity entity = SecretariaFactoryBot.buildEntity();

        Secretaria domain = secretariaMapper.toDomainEntity(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName(), domain.getName());
        assertEquals(entity.getEmail(), domain.getEmail());
        assertEquals(entity.getPassword(), domain.getPassword());
    }

    @Test
    @DisplayName("Deve mapear Secretaria Domain para SecretariaEntity")
    void toEntity() {
        Secretaria domain = SecretariaFactoryBot.build();

        SecretariaEntity entity = secretariaMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getName(), entity.getName());
        assertEquals(domain.getEmail(), entity.getEmail());
        assertEquals(domain.getPassword(), entity.getPassword());
    }

    @Test
    @DisplayName("Deve mapear Secretaria Domain para SecretariaResponse")
    void toResponse() {
        Secretaria domain = SecretariaFactoryBot.build();

        SecretariaResponse response = secretariaMapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(domain.getId(), response.id());
        assertEquals(domain.getName(), response.name());
        assertEquals(domain.getEmail(), response.email());
    }
}
