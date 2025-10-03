package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.SecretariaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.core.usecase.ConsultaUseCase;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
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

import static br.com.cdb.agendadorDeConsultas.adapter.input.controller.ConsultaControllerTest.SECRETARIA_ID;
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

    @MockitoBean
    private ConsultaMapper  consultaMapper;

    @MockitoBean
    private ConsultaUseCase consultaUseCase;

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
    @DisplayName("Deve retornar 404 Not Found ao tentar atualizar secretaria com ID que não existe")
    void updateSecretaria_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idNaoExistente = UUID.randomUUID();
        SecretariaUpdate request = SecretariaFactoryBot.buildUpdate();

        when(secretariaInputPort.update(eq(idNaoExistente), any(SecretariaUpdate.class)))
                .thenThrow(new EntityNotFoundException("Secretaria não encontrada"));

        mockMvc.perform(put("/secretarias/{id}", idNaoExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
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

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar secretaria com ID que não existe")
    void deleteSecretaria_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idNaoExistente = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Secretaria não encontrada")).when(secretariaInputPort).delete(idNaoExistente);

        mockMvc.perform(delete("/secretarias/{id}", idNaoExistente))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Deve retornar 500 Internal Server Error para exceções inesperadas")
    void createSecretaria_shouldReturnInternalServerError_whenUnexpectedError() throws Exception {
        SecretariaRequest request = new SecretariaRequest("Nome", "123.456.789-00", "email@valido.com", "Senha@123");

        when(secretariaMapper.toDomain(any(SecretariaRequest.class))).thenReturn(new Secretaria());
        when(secretariaInputPort.create(any(Secretaria.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/secretarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }



    @Test
    @DisplayName("Deve retornar 500 Internal Server Error para exceções inesperadas")
    void create_shouldReturnInternalServerError_whenUnexpectedError() throws Exception {
        ConsultaRequest request = ConsultaFactoryBot.buildRequest();

        when(consultaMapper.toDomain(any(ConsultaRequest.class))).thenReturn(new Consulta());
        when(consultaUseCase.createConsulta(eq(SECRETARIA_ID), any(Consulta.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/consultas/{secretariaId}", SECRETARIA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}