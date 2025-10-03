package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.SecretariaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.core.usecase.validation.SecretariaValidator;
import br.com.cdb.agendadorDeConsultas.factory.SecretariaFactoryBot;
import br.com.cdb.agendadorDeConsultas.port.input.SecretariaInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecretariaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SecretariaInputPort secretariaInputPort;

    @MockitoBean
    private SecretariaMapper secretariaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar uma secretaria com sucesso")
    void createSecretaria() throws Exception {
        Secretaria secretaria = SecretariaFactoryBot.build();
        SecretariaRequest request = new SecretariaRequest(secretaria.getName(), secretaria.getCpf(), secretaria.getEmail(), secretaria.getPassword());
        SecretariaResponse response = new SecretariaResponse(secretaria.getId(), secretaria.getName(), secretaria.getEmail());

        when(secretariaMapper.toDomain(any(SecretariaRequest.class))).thenReturn(secretaria);
        when(secretariaInputPort.create(any(Secretaria.class))).thenReturn(secretaria);
        when(secretariaMapper.toResponse(any(Secretaria.class))).thenReturn(response);

        mockMvc.perform(post("/secretarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(secretaria.getId().toString()))
                .andExpect(jsonPath("$.name").value(secretaria.getName()));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar secretaria com dados inválidos")
    void createSecretaria_shouldReturnBadRequest_whenValidationFails() throws Exception {
        SecretariaRequest request = new SecretariaRequest("Nome", "123", "email-invalido", "senha");

        // Simula a exceção que seria lançada pela camada de serviço/validação
        when(secretariaInputPort.create(any(Secretaria.class)))
                .thenThrow(new BusinessRuleValidationException("CPF inválido"));
        when(secretariaMapper.toDomain(any(SecretariaRequest.class))).thenReturn(new Secretaria());

        mockMvc.perform(post("/secretarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF inválido"));
    }


    @Test
    @DisplayName("Deve listar todas as secretarias")
    void getAllSecretarias() throws Exception {
        Secretaria secretaria = SecretariaFactoryBot.build();
        SecretariaResponse response = new SecretariaResponse(secretaria.getId(), secretaria.getName(), secretaria.getEmail());

        when(secretariaInputPort.findAll()).thenReturn(Collections.singletonList(secretaria));
        when(secretariaMapper.toResponse(any(Secretaria.class))).thenReturn(response);

        mockMvc.perform(get("/secretarias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(secretaria.getId().toString()));
    }

    @Test
    @DisplayName("Deve buscar uma secretaria por id com sucesso")
    void getSecretariaById() throws Exception {
        Secretaria secretaria = SecretariaFactoryBot.build();
        SecretariaResponse response = new SecretariaResponse(secretaria.getId(), secretaria.getName(), secretaria.getEmail());
        UUID id = secretaria.getId();

        when(secretariaInputPort.findById(id)).thenReturn(secretaria);
        when(secretariaMapper.toResponse(any(Secretaria.class))).thenReturn(response);

        mockMvc.perform(get("/secretarias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar secretaria com ID que não existe")
    void getSecretariaById_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idNaoExistente = UUID.randomUUID();
        when(secretariaInputPort.findById(idNaoExistente)).thenThrow(new EntityNotFoundException("Secretaria não encontrada"));

        mockMvc.perform(get("/secretarias/{id}", idNaoExistente))
                .andExpect(status().isNotFound()); // Supondo que você tenha um handler para EntityNotFoundException
    }


    @Test
    @DisplayName("Deve atualizar uma secretaria com sucesso")
    void updateSecretaria() throws Exception {
        Secretaria secretaria = SecretariaFactoryBot.build();
        SecretariaUpdate request = SecretariaFactoryBot.buildUpdate();
        secretaria.setName(request.name()); // Atualiza o objeto para o mock retornar o valor certo
        SecretariaResponse response = new SecretariaResponse(secretaria.getId(), request.name(), request.email());
        UUID id = secretaria.getId();

        when(secretariaInputPort.update(eq(id), any(SecretariaUpdate.class))).thenReturn(secretaria);
        when(secretariaMapper.toResponse(any(Secretaria.class))).thenReturn(response);

        mockMvc.perform(put("/secretarias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    @DisplayName("Deve deletar uma secretaria com sucesso")
    void deleteSecretaria() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(secretariaInputPort).delete(id); // Configura o mock para não fazer nada

        mockMvc.perform(delete("/secretarias/{id}", id))
                .andExpect(status().isNoContent());

        verify(secretariaInputPort, times(1)).delete(id); // Verifica se o método delete foi chamado
    }
}