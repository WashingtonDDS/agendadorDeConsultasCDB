package br.com.cdb.agendadorDeConsultas.util.validation;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaValidatorTest {

    @Mock
    private ConsultaOutputPort consultaOutputPort;

    @Mock
    private SecretariaOutputPort secretariaOutputPort;

    private ConsultaValidator consultaValidator;

    @BeforeEach
    void setUp() {
        consultaValidator = new ConsultaValidator(consultaOutputPort, secretariaOutputPort);
    }

    private LocalDateTime getNextValidDateTime() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    @DisplayName("validateCreate: Deve passar com dados válidos")
    void validateCreate_Success() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setConsultationDateTime(getNextValidDateTime());

        when(secretariaOutputPort.findById(any(UUID.class))).thenReturn(new Secretaria());
        when(consultaOutputPort.findByDoctorNameAndDateTime(anyString(), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaValidator.validateCreate(secretariaId, consulta));

        verify(secretariaOutputPort, times(1)).findById(secretariaId);
        verify(consultaOutputPort, times(1)).findByDoctorNameAndDateTime(anyString(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("validateCreate: Deve falhar se a data for no passado")
    void validateCreate_Fails_WhenDateIsInThePast() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setConsultationDateTime(LocalDateTime.now().minusDays(1));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCreate(secretariaId, consulta));

        assertEquals("A data da consulta não pode ser no passado.", exception.getMessage());
    }

    @Test
    @DisplayName("validateCreate: Deve falhar se for fora do horário comercial")
    void validateCreate_Fails_WhenOutsideBusinessHours() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        LocalDateTime nextWeekday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(7);
        consulta.setConsultationDateTime(nextWeekday);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCreate(secretariaId, consulta));

        assertEquals("Consultas só podem ser agendadas entre 08:00 e 18:00.", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateCreate: Deve falhar se for em um fim de semana")
    void validateCreate_Fails_OnWeekend() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        LocalDateTime nextSaturday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).withHour(10);
        consulta.setConsultationDateTime(nextSaturday);

        var exception = assertThrows(BusinessRuleValidationException.class, 
                () -> consultaValidator.validateCreate(secretariaId, consulta));

        assertEquals("Consultas não podem ser agendadas nos fins de semana.", exception.getMessage());
    }

    @Test
    @DisplayName("validateCreate: Deve falhar se médico já tiver consulta no horário")
    void validateCreate_Fails_WhenDoctorHasConflict() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaParaAgendar = ConsultaFactoryBot.build();
        consultaParaAgendar.setConsultationDateTime(getNextValidDateTime());

        when(secretariaOutputPort.findById(any(UUID.class))).thenReturn(new Secretaria());

        Consulta consultaExistente = ConsultaFactoryBot.build();
        when(consultaOutputPort.findByDoctorNameAndDateTime(consultaParaAgendar.getDoctorName(), consultaParaAgendar.getConsultationDateTime()))
                .thenReturn(List.of(consultaExistente));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCreate(secretariaId, consultaParaAgendar));

        assertEquals("O médico já possui outra consulta agendada para este mesmo horário.", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateCreate: Deve falhar se a secretária não for encontrada")
    void validateCreate_Fails_WhenSecretariaNotFound() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setConsultationDateTime(getNextValidDateTime());

        when(secretariaOutputPort.findById(secretariaId)).thenThrow(new EntityNotFoundException("Secretária não encontrada"));

        assertThrows(EntityNotFoundException.class, 
                () -> consultaValidator.validateCreate(secretariaId, consulta));
    }

    @Test
    @DisplayName("validateUpdate: Deve passar com dados válidos")
    void validateUpdate_Success() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(2));

        ConsultaUpdate request = new ConsultaUpdate("Novo Doutor", "Novo Paciente", "12345", getNextValidDateTime());

        when(secretariaOutputPort.findById(any(UUID.class))).thenReturn(new Secretaria());
        when(consultaOutputPort.findByDoctorNameAndDateTime(eq(request.doctorName()), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        verify(secretariaOutputPort, times(1)).findById(secretariaId);
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se secretária não tiver permissão")
    void validateUpdate_Fails_WhenNoPermission() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(UUID.randomUUID());

        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("A secretária não tem permissão para alterar esta consulta.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se a consulta já ocorreu")
    void validateUpdate_Fails_WhenConsultaAlreadyOccurred() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().minusDays(1));

        ConsultaUpdate request = ConsultaFactoryBot.buildUpdate();

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("Não é possível alterar ou cancelar uma consulta que já ocorreu.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se o nome do paciente for em branco")
    void validateUpdate_Fails_WhenPatientNameIsBlank() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(1));

        ConsultaUpdate request = new ConsultaUpdate(null, " ", null, null);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("O nome do paciente, se fornecido, не pode ser em branco.", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateUpdate: Deve falhar se o nome do médico for em branco")
    void validateUpdate_Fails_WithBlankDoctorName() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(1));
        ConsultaUpdate request = new ConsultaUpdate("  ", null, null, null);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("O nome do médico, se fornecido, не pode ser em branco.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se o número do paciente for em branco")
    void validateUpdate_Fails_WithBlankPatientNumber() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(1));
        ConsultaUpdate request = new ConsultaUpdate(null, null, "  ", null);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("O número do paciente, se fornecido, не pode ser em branco.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se o número do paciente contiver letras")
    void validateUpdate_Fails_WithInvalidPatientNumber() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(1));
        ConsultaUpdate request = new ConsultaUpdate(null, null, "123a45", null);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("O número do paciente deve conter apenas dígitos.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se a atualização causar conflito de horário")
    void validateUpdate_Fails_WhenDoctorHasConflict() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(2));

        LocalDateTime newDateTime = getNextValidDateTime();
        ConsultaUpdate request = new ConsultaUpdate("Novo Doutor", null, null, newDateTime);

        Consulta outraConsulta = ConsultaFactoryBot.build(); // Outra consulta que causa o conflito
        when(consultaOutputPort.findByDoctorNameAndDateTime(request.doctorName(), newDateTime))
                .thenReturn(List.of(outraConsulta));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));

        assertEquals("O médico já possui outra consulta agendada para este mesmo horário.", exception.getMessage());
    }

    @Test
    @DisplayName("validateCancelOrDelete: Deve passar se a consulta for no futuro")
    void validateCancelOrDelete_Success() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setSecretariaId(secretariaId);
        consulta.setConsultationDateTime(LocalDateTime.now().plusDays(1));

        assertDoesNotThrow(() -> consultaValidator.validateCancelOrDelete(secretariaId, consulta));
    }

    @Test
    @DisplayName("validateCancelOrDelete: Deve falhar se a consulta já ocorreu")
    void validateCancelOrDelete_Fails_WhenConsultaAlreadyOccurred() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setSecretariaId(secretariaId);
        consulta.setConsultationDateTime(LocalDateTime.now().minusDays(1));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCancelOrDelete(secretariaId, consulta));

        assertEquals("Não é possível alterar ou cancelar uma consulta que já ocorreu.", exception.getMessage());
    }


    @Test
    @DisplayName("validateForDelete: Deve passar se a consulta já ocorreu")
    void validateForDelete_Success_WhenAlreadyOccurred() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setSecretariaId(secretariaId);
        consulta.setConsultationDateTime(LocalDateTime.now().minusDays(1));

        assertDoesNotThrow(() -> consultaValidator.validateForDelete(secretariaId, consulta));
    }

    @Test
    @DisplayName("validateForDelete: Deve passar se a consulta estiver cancelada")
    void validateForDelete_Success_WhenCanceled() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setSecretariaId(secretariaId);
        consulta.setStatus(StatusConsulta.CANCELADA);
        consulta.setConsultationDateTime(LocalDateTime.now().plusDays(1));

        assertDoesNotThrow(() -> consultaValidator.validateForDelete(secretariaId, consulta));
    }

    @Test
    @DisplayName("validateForDelete: Deve falhar se a consulta for futura e agendada")
    void validateForDelete_Fails_WhenFutureAndScheduled() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setSecretariaId(secretariaId);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setConsultationDateTime(LocalDateTime.now().plusDays(1));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateForDelete(secretariaId, consulta));

        assertEquals("Uma consulta futura e agendada não pode ser deletada. Cancele-a primeiro.", exception.getMessage());
    }


    @Test
    @DisplayName("validateCreate: Deve falhar com nome do médico em branco")
    void validateCreate_Fails_WithBlankDoctorName() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setDoctorName(" ");
        consulta.setConsultationDateTime(getNextValidDateTime());

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCreate(secretariaId, consulta));

        assertTrue(exception.getMessage().contains("médico"));
    }

    @Test
    @DisplayName("validateCreate: Deve falhar com nome do paciente em branco")
    void validateCreate_Fails_WithBlankPatientName() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setPatientName(" ");
        consulta.setConsultationDateTime(getNextValidDateTime());

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCreate(secretariaId, consulta));

        assertTrue(exception.getMessage().contains("paciente"));
    }

    @Test
    @DisplayName("validateCreate: Deve falhar com número do paciente inválido")
    void validateCreate_Fails_WithInvalidPatientNumber() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consulta = ConsultaFactoryBot.build();
        consulta.setPatientNumber("123a456");
        consulta.setConsultationDateTime(getNextValidDateTime());

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> consultaValidator.validateCreate(secretariaId, consulta));

        assertTrue(exception.getMessage().contains("dígitos"));
    }

    @Test
    @DisplayName("validateUpdate: Deve passar quando apenas alguns campos são fornecidos")
    void validateUpdate_Success_WithPartialFields() {
        UUID secretariaId = UUID.randomUUID();
        Consulta consultaExistente = ConsultaFactoryBot.build();
        consultaExistente.setSecretariaId(secretariaId);
        consultaExistente.setConsultationDateTime(LocalDateTime.now().plusDays(2));


        ConsultaUpdate request = new ConsultaUpdate(null, "Novo Paciente", null, null);

        when(secretariaOutputPort.findById(any(UUID.class))).thenReturn(new Secretaria());

        assertDoesNotThrow(() -> consultaValidator.validateUpdate(secretariaId, consultaExistente, request));
    }
}
