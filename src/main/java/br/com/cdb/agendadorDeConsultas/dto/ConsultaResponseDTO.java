package br.com.cdb.agendadorDeConsultas.dto;

import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponseDTO(UUID id, String doctorName, String patientName, String patientNumber, String speciality, String description,
                                  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
                                  LocalDateTime consultationDateTime) {

}
