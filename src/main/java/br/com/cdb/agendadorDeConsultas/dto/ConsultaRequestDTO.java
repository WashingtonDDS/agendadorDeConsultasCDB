package br.com.cdb.agendadorDeConsultas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

public record ConsultaRequestDTO(String doctorName, String patientName, String patientNumber, String speciality, String description,
                                 @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
                                 LocalDateTime consultationDateTime) {
}
