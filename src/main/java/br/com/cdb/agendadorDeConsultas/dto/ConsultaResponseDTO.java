package br.com.cdb.agendadorDeConsultas.dto;

import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.entity.StatusConsulta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponseDTO(UUID id, String doctorName, String patientName, String patientNumber, String speciality, String description,
                                  StatusConsulta status,
                                  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
                                  LocalDateTime consultationDateTime) {

    public ConsultaResponseDTO(Consulta consulta) {
        this(
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
