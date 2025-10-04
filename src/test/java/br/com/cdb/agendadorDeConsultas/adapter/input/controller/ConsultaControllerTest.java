package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.core.usecase.ConsultaUseCase;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConsultaUseCase consultaUseCase;

    @MockitoBean
    private ConsultaMapper consultaMapper;

    static final UUID CONSULTA_ID = UUID.randomUUID();
    static final UUID SECRETARIA_ID = UUID.randomUUID();
    static final String DOCTOR_NAME = "Dr. House";
    static final String PATIENT_NAME = "John Doe";

    @Test
    @DisplayName("Deve agendar uma nova consulta com sucesso quando os dados são válidos")
    void create_shouldScheduleNewConsulta_whenDataIsValid() throws Exception {
        ConsultaRequest request = ConsultaFactoryBot.buildRequest();
        Consulta consultaParaSalvar = new Consulta();
        Consulta consultaSalva = new Consulta();
        consultaSalva.setId(CONSULTA_ID);
        consultaSalva.setSecretariaId(SECRETARIA_ID);
        ConsultaResponse response = ConsultaFactoryBot.buildResponse(consultaSalva);

        when(consultaMapper.toDomain(any(ConsultaRequest.class))).thenReturn(consultaParaSalvar);
        when(consultaUseCase.createConsulta(eq(SECRETARIA_ID), any(Consulta.class))).thenReturn(consultaSalva);
        when(consultaMapper.toResponse(any(Consulta.class))).thenReturn(response);

        ResultActions result = mockMvc.perform(post("/consultas/{secretariaId}", SECRETARIA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(CONSULTA_ID.toString()))
                .andExpect(jsonPath("$.secretariaId").value(SECRETARIA_ID.toString()));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar agendar consulta com dados inválidos")
    void create_shouldReturnBadRequest_whenDataIsInvalid() throws Exception {
        ConsultaRequest request = ConsultaFactoryBot.buildRequest();
        when(consultaMapper.toDomain(any(ConsultaRequest.class))).thenReturn(new Consulta());
        when(consultaUseCase.createConsulta(eq(SECRETARIA_ID), any(Consulta.class)))
                .thenThrow(new BusinessRuleValidationException("Dados da consulta inválidos"));

        mockMvc.perform(post("/consultas/{secretariaId}", SECRETARIA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados da consulta inválidos"));
    }

    @Test
    @DisplayName("Deve retornar uma lista com todas as consultas")
    void getAllConsultas_shouldReturnListOfAllConsultas() throws Exception {
        Consulta consulta = ConsultaFactoryBot.build();
        ConsultaResponse response = ConsultaFactoryBot.buildResponse(consulta);

        when(consultaUseCase.getConsultas()).thenReturn(Collections.singletonList(consulta));
        when(consultaMapper.toResponse(any(Consulta.class))).thenReturn(response);

        ResultActions result = mockMvc.perform(get("/consultas"));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(consulta.getId().toString()))
                .andExpect(jsonPath("$[0].patientName").value(consulta.getPatientName()));
    }

    @Test
    @DisplayName("Deve retornar as próximas consultas não canceladas")
    void getUpcomingConsultas_shouldReturnUpcomingConsultasForPatient() throws Exception {
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setStatus(StatusConsulta.AGENDADA);
        ConsultaResponse response = ConsultaFactoryBot.buildResponse(consulta);

        when(consultaUseCase.getUpcomingConsultas()).thenReturn(Collections.singletonList(consulta));
        when(consultaMapper.toResponse(any(Consulta.class))).thenReturn(response);

        ResultActions result = mockMvc.perform(get("/consultas/proximas"));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(consulta.getId().toString()))
                .andExpect(jsonPath("$[0].status").value(consulta.getStatus().name()));
    }

    @Test
    @DisplayName("Deve retornar os detalhes de uma consulta específica pelo ID")
    void getConsultaDetails_shouldReturnConsultaDetails_whenIdExists() throws Exception {
        Consulta consulta = ConsultaFactoryBot.build();
        ConsultaDetails details = ConsultaFactoryBot.buildDetails(consulta);

        when(consultaUseCase.getConsultaDetails(CONSULTA_ID)).thenReturn(consulta);
        when(consultaMapper.toDetails(any(Consulta.class))).thenReturn(details);

        ResultActions result = mockMvc.perform(get("/consultas/{id}", CONSULTA_ID));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(consulta.getId().toString()))
                .andExpect(jsonPath("$.doctorName").value(consulta.getDoctorName()));
    }

    @Test
    @DisplayName("Deve atualizar uma consulta com sucesso quando os dados são válidos")
    void updateConsulta_shouldUpdateConsulta_whenDataIsValid() throws Exception {
        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();
        Consulta consultaAtualizada = new Consulta();
        consultaAtualizada.setId(CONSULTA_ID);
        consultaAtualizada.setPatientName(request.patientName());
        consultaAtualizada.setConsultationDateTime(request.consultationDateTime());
        ConsultaResponse response = ConsultaFactoryBot.buildResponse(consultaAtualizada);

        when(consultaUseCase.updateConsulta(eq(SECRETARIA_ID), eq(CONSULTA_ID), any(ConsultaUpdate.class))).thenReturn(consultaAtualizada);
        when(consultaMapper.toResponse(any(Consulta.class))).thenReturn(response);

        ResultActions result = mockMvc.perform(put("/consultas/{secretariaId}/{id}", SECRETARIA_ID, CONSULTA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value(request.patientName()));
    }

    @Test
    @DisplayName("Deve cancelar uma consulta com sucesso e retornar o status CANCELADA")
    void cancelledConsulta_shouldCancelConsulta_whenIdExists() throws Exception {
        Consulta consultaCancelada = ConsultaFactoryBot.build();
        consultaCancelada.setStatus(StatusConsulta.CANCELADA);
        ConsultaResponse response = ConsultaFactoryBot.buildResponse(consultaCancelada);

        when(consultaUseCase.canceledConsulta(SECRETARIA_ID, CONSULTA_ID)).thenReturn(consultaCancelada);
        when(consultaMapper.toResponse(any(Consulta.class))).thenReturn(response);

        ResultActions result = mockMvc.perform(patch("/consultas/{secretariaId}/{id}", SECRETARIA_ID, CONSULTA_ID));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    @DisplayName("Deve deletar uma consulta com sucesso e retornar status No Content")
    void deleteConsulta_shouldDeleteConsulta_whenIdExists() throws Exception {
        doNothing().when(consultaUseCase).deleteConsulta(SECRETARIA_ID, CONSULTA_ID);

        ResultActions result = mockMvc.perform(delete("/consultas/{secretariaId}/{id}", SECRETARIA_ID, CONSULTA_ID));
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar detalhes de uma consulta com ID inexistente")
    void getConsultaDetails_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        when(consultaUseCase.getConsultaDetails(idInexistente)).thenThrow(new EntityNotFoundException("Consulta não encontrada"));

        mockMvc.perform(get("/consultas/{id}", idInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar atualizar uma consulta com ID inexistente")
    void updateConsulta_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();

        when(consultaUseCase.updateConsulta(eq(SECRETARIA_ID), eq(idInexistente), any(ConsultaUpdate.class)))
                .thenThrow(new EntityNotFoundException("Consulta a ser atualizada não foi encontrada"));

        mockMvc.perform(put("/consultas/{secretariaId}/{id}", SECRETARIA_ID, idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar cancelar uma consulta com ID inexistente")
    void cancelledConsulta_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        when(consultaUseCase.canceledConsulta(SECRETARIA_ID, idInexistente))
                .thenThrow(new EntityNotFoundException("Consulta não encontrada para cancelamento"));

        mockMvc.perform(patch("/consultas/{secretariaId}/{id}", SECRETARIA_ID, idInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cancelar consulta que já ocorreu")
    void cancelledConsulta_shouldReturnBadRequest_whenConsultaAlreadyHappened() throws Exception {
        when(consultaUseCase.canceledConsulta(SECRETARIA_ID, CONSULTA_ID))
                .thenThrow(new BusinessRuleValidationException("Não é possível cancelar uma consulta que já ocorreu."));

        mockMvc.perform(patch("/consultas/{secretariaId}/{id}", SECRETARIA_ID, CONSULTA_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Não é possível cancelar uma consulta que já ocorreu."));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar uma consulta com ID inexistente")
    void deleteConsulta_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Consulta não encontrada para exclusão"))
                .when(consultaUseCase).deleteConsulta(SECRETARIA_ID, idInexistente);

        mockMvc.perform(delete("/consultas/{secretariaId}/{id}", SECRETARIA_ID, idInexistente))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Deve criar uma consulta de retorno com sucesso quando os IDs são válidos")
    void createFollowUp_shouldCreateFollowUpConsulta_whenIdsAreValid() throws Exception {
        UUID originalConsultaId = UUID.randomUUID();
        Consulta followupConsulta = ConsultaFactoryBot.build();
        ConsultaResponse response = ConsultaFactoryBot.buildResponse(followupConsulta);

        when(consultaUseCase.createFollowUpConsulta(SECRETARIA_ID, originalConsultaId)).thenReturn(followupConsulta);
        when(consultaMapper.toResponse(followupConsulta)).thenReturn(response);

        mockMvc.perform(post("/consultas/{secretariaId}/{originalConsultaId}/retorno", SECRETARIA_ID, originalConsultaId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.patientName").value(response.patientName()));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar criar retorno de consulta original inexistente")
    void createFollowUp_shouldReturnNotFound_whenOriginalConsultaDoesNotExist() throws Exception {
        UUID originalConsultaId = UUID.randomUUID();
        String errorMessage = "Consulta original não encontrada";

        when(consultaUseCase.createFollowUpConsulta(SECRETARIA_ID, originalConsultaId))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(post("/consultas/{secretariaId}/{originalConsultaId}/retorno", SECRETARIA_ID, originalConsultaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}