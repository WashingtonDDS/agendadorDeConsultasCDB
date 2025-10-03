package br.com.cdb.agendadorDeConsultas.factory;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConsultaFactoryBot {

    private static final String DOCTOR_NAME = "Dr. Smith";
    private static final String PATIENT_NAME = "John Doe (Default)";
    private static final String PATIENT_NUMBER = "123456789";
    private static final String SPECIALTY = "Cardiologia";
    private static final String DESCRIPTION = "Consulta de rotina";

    public static Consulta build() {
        Consulta consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPatientName(PATIENT_NAME);
        consulta.setDoctorName(DOCTOR_NAME);
        consulta.setPatientNumber(PATIENT_NUMBER);
        consulta.setSpeciality(SPECIALTY);
        consulta.setDescription(DESCRIPTION);
        consulta.setConsultationDateTime(LocalDateTime.now().plusDays(5));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setSecretariaId(UUID.randomUUID());
        return consulta;
    }

    public static ConsultaUpdate buildUpdate() {
        return new ConsultaUpdate(
                "Dr. House (Updated)",
                "Jane Doe (Updated)",
                "9876543210",
                LocalDateTime.now().plusDays(10)
        );
    }

    public static ConsultaRequest buildRequest() {
        return new ConsultaRequest(
                DOCTOR_NAME,
                PATIENT_NAME,
                PATIENT_NUMBER,
                SPECIALTY,
                DESCRIPTION,
                LocalDateTime.now().plusDays(1)
        );
    }

    public static ConsultaResponse buildResponse(Consulta consulta) {
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getDoctorName(),
                consulta.getPatientName(),
                consulta.getPatientNumber(),
                consulta.getSpeciality(),
                consulta.getDescription(),
                consulta.getStatus(),
                consulta.getConsultationDateTime(),
                consulta.getSecretariaId()
        );
    }

    public static ConsultaDetails buildDetails(Consulta consulta) {
        return new ConsultaDetails(
                consulta.getId(),
                consulta.getDoctorName(),
                consulta.getPatientName(),
                consulta.getPatientNumber(),
                consulta.getSpeciality(),
                consulta.getDescription(),
                consulta.getStatus(),
                consulta.getConsultationDateTime()

        );
    }
}
