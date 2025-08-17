package br.com.cdb.agendadorDeConsultas.dto;

import java.util.Date;

public record ConsultaRequestDTO(String doctorName, String patientName, String PatientNumber, String title, String description, Date dataConsulta) {
}
