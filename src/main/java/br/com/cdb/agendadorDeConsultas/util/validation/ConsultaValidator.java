package br.com.cdb.agendadorDeConsultas.util.validation;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class ConsultaValidator {
    private final ConsultaOutputPort consultaOutputPort;
    private final SecretariaOutputPort secretariaOutputPort;

    public ConsultaValidator(ConsultaOutputPort consultaOutputPort, SecretariaOutputPort secretariaOutputPort) {
        this.consultaOutputPort = consultaOutputPort;
        this.secretariaOutputPort = secretariaOutputPort;
    }

    public void validateCreate(UUID secretariaId, Consulta consulta) {
        if (consulta.getDoctorName() == null || consulta.getDoctorName().isBlank()) {
            throw new BusinessRuleValidationException("O nome do médico não pode ser em branco.");
        }
        if (consulta.getPatientName() == null || consulta.getPatientName().isBlank()) {
            throw new BusinessRuleValidationException("O nome do paciente não pode ser em branco.");
        }
        if (consulta.getPatientNumber() == null || !consulta.getPatientNumber().matches("[0-9]+")) {
            throw new BusinessRuleValidationException("O número do paciente deve conter apenas dígitos.");
        }

        checkSecretariaExists(secretariaId);
        checkConsultaIsInTheFuture(consulta.getConsultationDateTime());
        checkIsWithinBusinessHours(consulta.getConsultationDateTime());
        checkDoctorAvailability(consulta.getDoctorName(), consulta.getConsultationDateTime(), null);
    }
    public void validateUpdate(UUID secretariaId, Consulta consultaExistente, ConsultaUpdate request) {
        checkSecretariaExists(secretariaId);
        checkPermission(secretariaId, consultaExistente);
        checkCanBeModified(consultaExistente);
        validarCamposDoRequest(request, consultaExistente);
    }
    public void validateCancelOrDelete(UUID secretariaId, Consulta consulta) {
        checkSecretariaExists(secretariaId);
        checkPermission(secretariaId, consulta);
        checkCanBeModified(consulta);
    }
    public void validateForDelete(UUID secretariaId, Consulta consulta) {
        checkSecretariaExists(secretariaId);
        checkPermission(secretariaId, consulta);
        boolean jaOcorreu = consulta.getConsultationDateTime().isBefore(LocalDateTime.now());
        boolean estaCancelada = consulta.getStatus() == StatusConsulta.CANCELADA;
        if (!jaOcorreu && !estaCancelada) {
            throw new BusinessRuleValidationException("Uma consulta futura e agendada não pode ser deletada. Cancele-a primeiro.");
        }
    }
    private void checkSecretariaExists(UUID secretariaId) {
        secretariaOutputPort.findById(secretariaId);
    }
    private void checkPermission(UUID secretariaId, Consulta consulta) {
        if (!consulta.getSecretariaId().equals(secretariaId)) {
            throw new BusinessRuleValidationException("A secretária não tem permissão para alterar esta consulta.");
        }
    }
    private void checkCanBeModified(Consulta consulta) {
        if (consulta.getConsultationDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleValidationException("Não é possível alterar ou cancelar uma consulta que já ocorreu.");
        }
    }
    private void checkConsultaIsInTheFuture(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleValidationException("A data da consulta não pode ser no passado.");
        }
    }
    private void checkIsWithinBusinessHours(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        int hour = dateTime.getHour();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BusinessRuleValidationException("Consultas não podem ser agendadas nos fins de semana.");
        }
        if (hour < 8 || hour >= 18) {
            throw new BusinessRuleValidationException("Consultas só podem ser agendadas entre 08:00 e 18:00.");
        }
    }
    private void checkDoctorAvailability(String doctorName, LocalDateTime dateTime, UUID consultaIdToIgnore) {
        List<Consulta> existingConsultas = consultaOutputPort.findByDoctorNameAndDateTime(doctorName, dateTime);

        boolean hasConflict = existingConsultas.stream()
                .anyMatch(consulta -> !consulta.getId().equals(consultaIdToIgnore));
        if (hasConflict) {
            throw new BusinessRuleValidationException("O médico já possui outra consulta agendada para este mesmo horário.");
        }
    }
    private void validarCamposDoRequest(ConsultaUpdate request, Consulta consultaExistente) {
        if (request.doctorName() != null && request.doctorName().isBlank()) {
            throw new BusinessRuleValidationException("O nome do médico, se fornecido, не pode ser em branco.");
        }
        if (request.patientName() != null && request.patientName().isBlank()) {
            throw new BusinessRuleValidationException("O nome do paciente, se fornecido, не pode ser em branco.");
        }
        if (request.patientNumber() != null) {
            if (request.patientNumber().isBlank()) {
                throw new BusinessRuleValidationException("O número do paciente, se fornecido, не pode ser em branco.");
            }
            if (!request.patientNumber().matches("[0-9]+")) {
                throw new BusinessRuleValidationException("O número do paciente deve conter apenas dígitos.");
            }
        }
        if (request.consultationDateTime() != null) {
            checkConsultaIsInTheFuture(request.consultationDateTime());
            checkIsWithinBusinessHours(request.consultationDateTime());

            String doctorName = request.doctorName() != null ? request.doctorName() : consultaExistente.getDoctorName();
            checkDoctorAvailability(doctorName, request.consultationDateTime(), consultaExistente.getId());

        }

    }
}
