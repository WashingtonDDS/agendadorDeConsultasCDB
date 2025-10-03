package br.com.cdb.agendadorDeConsultas.core.usecase;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.core.usecase.validation.ConsultaValidator;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ConsultaUseCaseTest {

    @Mock
    ConsultaOutputPort consultaOutputPort;

    @Mock
    SecretariaOutputPort secretariaOutputPort;

    @Mock
    ConsultaValidator validator;

    @InjectMocks
    ConsultaUseCase consultaUseCase;

    @Test
    @DisplayName("Deve criar uma consulta com sucesso")
    void createConsulta_Success() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.save(any(Consulta.class))).thenReturn(consulta);

        Consulta result = consultaUseCase.createConsulta(secretariaId, consulta);

        verify(validator, times(1)).validateCreate(secretariaId, consulta);
        verify(secretariaOutputPort, times(1)).findById(secretariaId);
        verify(consultaOutputPort, times(1)).save(consulta);
        assertEquals(secretariaId, result.getSecretariaId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a validação ao criar falhar")
    void createConsulta_ValidationFails() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        doThrow(new IllegalArgumentException("Dados inválidos")).when(validator).validateCreate(secretariaId, consulta);

        assertThrows(IllegalArgumentException.class, () -> consultaUseCase.createConsulta(secretariaId, consulta));
        verify(secretariaOutputPort, never()).findById(any());
        verify(consultaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a secretária não for encontrada ao criar")
    void createConsulta_SecretariaNotFound() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        doThrow(new RuntimeException("Secretária não encontrada")).when(secretariaOutputPort).findById(secretariaId);

        assertThrows(RuntimeException.class, () -> consultaUseCase.createConsulta(secretariaId, consulta));
        verify(validator, times(1)).validateCreate(secretariaId, consulta);
        verify(consultaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a persistência falhar ao criar")
    void createConsulta_PersistenceFails() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.save(any(Consulta.class))).thenThrow(new DataAccessException("DB Error") {});

        assertThrows(DataAccessException.class, () -> consultaUseCase.createConsulta(secretariaId, consulta));
        verify(validator, times(1)).validateCreate(secretariaId, consulta);
        verify(secretariaOutputPort, times(1)).findById(secretariaId);
    }

    @Test
    @DisplayName("Deve retornar uma lista de todas as consultas")
    void getConsultas_Success() {
        List<Consulta> expectedConsultas = List.of(ConsultaFactoryBot.build());
        when(consultaOutputPort.findAll()).thenReturn(expectedConsultas);

        List<Consulta> actualConsultas = consultaUseCase.getConsultas();

        assertEquals(expectedConsultas, actualConsultas);
        verify(consultaOutputPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver consultas")
    void getConsultas_Empty() {
        when(consultaOutputPort.findAll()).thenReturn(Collections.emptyList());

        List<Consulta> actualConsultas = consultaUseCase.getConsultas();

        assertTrue(actualConsultas.isEmpty());
        verify(consultaOutputPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar consultas futuras e não canceladas")
    void getUpcomingConsultas_Success() {
        Consulta agendada = ConsultaFactoryBot.build();
        agendada.setStatus(StatusConsulta.AGENDADA);
        when(consultaOutputPort.findUpcomingConsultas(any(LocalDateTime.class))).thenReturn(List.of(agendada));

        List<Consulta> result = consultaUseCase.getUpcomingConsultas();

        assertEquals(1, result.size());
        assertEquals(StatusConsulta.AGENDADA, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver consultas futuras")
    void getUpcomingConsultas_Empty() {
        when(consultaOutputPort.findUpcomingConsultas(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        List<Consulta> result = consultaUseCase.getUpcomingConsultas();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar detalhes de uma consulta específica")
    void getConsultaDetails_Success() {
        UUID consultaId = UUID.randomUUID();
        Consulta expectedConsulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(expectedConsulta));

        Consulta actualConsulta = consultaUseCase.getConsultaDetails(consultaId);

        assertEquals(expectedConsulta, actualConsulta);
        verify(consultaOutputPort, times(1)).findById(consultaId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar detalhes de consulta inexistente")
    void getConsultaDetails_NotFound() {
        UUID consultaId = UUID.randomUUID();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consultaUseCase.getConsultaDetails(consultaId));
    }

    @Test
    @DisplayName("Deve atualizar uma consulta com sucesso")
    void updateConsulta_Success() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();
        Consulta existingConsulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(existingConsulta));
        when(consultaOutputPort.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Consulta updatedConsulta = consultaUseCase.updateConsulta(secretariaId, consultaId, request);

        verify(validator, times(1)).validateUpdate(secretariaId, existingConsulta, request);
        verify(consultaOutputPort, times(1)).save(existingConsulta);
        assertEquals(request.doctorName(), updatedConsulta.getDoctorName());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar consulta inexistente")
    void updateConsulta_NotFound() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consultaUseCase.updateConsulta(secretariaId, consultaId, request));
        verify(consultaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a validação ao atualizar falhar")
    void updateConsulta_ValidationFails() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();
        Consulta existingConsulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(existingConsulta));
        doThrow(new IllegalArgumentException("Erro de validação")).when(validator).validateUpdate(secretariaId, existingConsulta, request);

        assertThrows(IllegalArgumentException.class, () -> consultaUseCase.updateConsulta(secretariaId, consultaId, request));
        verify(consultaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a persistência falhar ao atualizar")
    void updateConsulta_PersistenceFails() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();
        Consulta existingConsulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(existingConsulta));
        when(consultaOutputPort.save(any(Consulta.class))).thenThrow(new DataAccessException("DB Error") {});

        assertThrows(DataAccessException.class, () -> consultaUseCase.updateConsulta(secretariaId, consultaId, request));
    }

    @Test
    @DisplayName("Deve cancelar uma consulta com sucesso")
    void canceledConsulta_Success() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(consultaOutputPort.save(any(Consulta.class))).thenReturn(consulta);

        Consulta canceledConsulta = consultaUseCase.canceledConsulta(secretariaId, consultaId);

        verify(validator, times(1)).validateCancelOrDelete(secretariaId, consulta);
        verify(consultaOutputPort, times(1)).save(consulta);
        assertEquals(StatusConsulta.CANCELADA, canceledConsulta.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar consulta inexistente")
    void canceledConsulta_NotFound() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consultaUseCase.canceledConsulta(secretariaId, consultaId));
        verify(consultaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma consulta com sucesso")
    void deleteConsulta_Success() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(consulta));

        consultaUseCase.deleteConsulta(secretariaId, consultaId);

        verify(validator, times(1)).validateForDelete(secretariaId, consulta);
        verify(consultaOutputPort, times(1)).delete(consulta);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar consulta inexistente")
    void deleteConsulta_NotFound() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consultaUseCase.deleteConsulta(secretariaId, consultaId));
        verify(consultaOutputPort, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a validação ao deletar falhar")
    void deleteConsulta_ValidationFails() {
        UUID secretariaId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        when(consultaOutputPort.findById(consultaId)).thenReturn(Optional.of(consulta));
        doThrow(new IllegalArgumentException("Não é possível deletar")).when(validator).validateForDelete(secretariaId, consulta);

        assertThrows(IllegalArgumentException.class, () -> consultaUseCase.deleteConsulta(secretariaId, consultaId));
        verify(consultaOutputPort, never()).delete(any());
    }
}
