package br.com.cdb.agendadorDeConsultas.core.usecase;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.util.validation.ConsultaValidator;
import br.com.cdb.agendadorDeConsultas.port.input.ConsultaInputPort;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class ConsultaUseCase implements ConsultaInputPort {


    private final ConsultaOutputPort consultaOutputPort;
    private final SecretariaOutputPort secretariaOutputPort;
    private final ConsultaValidator validator;

    public ConsultaUseCase(ConsultaOutputPort consultaOutputPort, SecretariaOutputPort secretariaOutputPort, ConsultaValidator validator) {
        this.consultaOutputPort = consultaOutputPort;
        this.secretariaOutputPort = secretariaOutputPort;
        this.validator = validator;
    }

    @Override
    public Consulta createConsulta(UUID secretariaId, Consulta consulta) {
        validator.validateCreate(secretariaId, consulta);

        secretariaOutputPort.findById(secretariaId);
        consulta.setSecretariaId(secretariaId);

        return consultaOutputPort.save(consulta);
    }

    public List<Consulta> getConsultas(){
        return consultaOutputPort.findAll();

    }

    public List<Consulta> getUpcomingConsultas() {
        List<Consulta> upcomingConsultas = consultaOutputPort.findUpcomingConsultas(LocalDateTime.now());
        return upcomingConsultas.stream()
                .filter(consulta -> consulta.getStatus() != StatusConsulta.CANCELADA)
                .toList();
    }

    public Consulta getConsultaDetails( UUID id) {
        return consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

    }

       @Override
    public Consulta updateConsulta(UUID secretariaId, UUID id, ConsultaUpdate request) {
        Consulta consulta = consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        validator.validateUpdate(secretariaId, consulta, request);

        consulta.setDoctorName(request.doctorName());
        consulta.setPatientName(request.patientName());
        consulta.setPatientNumber(request.patientNumber());
        consulta.setConsultationDateTime(request.consultationDateTime());

        consulta.setSecretariaId(secretariaId);

        return consultaOutputPort.save(consulta);
    }

    @Override
    public Consulta canceledConsulta(UUID secretariaId, UUID id) {
        secretariaOutputPort.findById(secretariaId);
        Consulta consulta = consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        validator.validateCancelOrDelete(secretariaId, consulta);

        consulta.setStatus(StatusConsulta.CANCELADA);
        consulta.setSecretariaId(secretariaId);

        return consultaOutputPort.save(consulta);
    }

    @Override
    public void deleteConsulta(UUID secretariaId, UUID id) {
        Consulta consulta = consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        validator.validateForDelete(secretariaId, consulta);

        consultaOutputPort.delete(consulta);
    }

    @Override
    public Consulta createFollowUpConsulta(UUID secretariaId, UUID originalConsultaId) {
        Consulta originalConsulta = consultaOutputPort.findById(originalConsultaId)
                .orElseThrow(() -> new BusinessRuleValidationException("Consulta original não encontrada"));

            Consulta retorno = originalConsulta.clone();

            retorno.setId(null);
            retorno.setStatus(StatusConsulta.AGENDADA);
            retorno.setDescription("Consulta de Retorno - " + originalConsulta.getDescription());
            retorno.setConsultationDateTime(LocalDateTime.now().plusDays(15).withHour(10).withMinute(0));

        return createConsulta(secretariaId, retorno);
    }


}
