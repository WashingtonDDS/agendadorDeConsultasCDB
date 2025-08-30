package br.com.cdb.agendadorDeConsultas.adapter.input.request;

import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponse(UUID id, String doctorName, String patientName, String patientNumber, String speciality, String description,
                               StatusConsulta status,
                               @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
                                  LocalDateTime consultationDateTime) {
    
}
