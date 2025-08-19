package br.com.cdb.agendadorDeConsultas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ConsultaUpdateDTO(String doctorName, String patientName, String patientNumber,
                                @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
                                LocalDateTime consultationDateTime) {
}
