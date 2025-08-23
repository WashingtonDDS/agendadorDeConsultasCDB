package br.com.cdb.agendadorDeConsultas.adapter.input.request;

import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConsultaRequest {
    @Id
    @GeneratedValue
    private UUID id;


    @Column(name = "doctorname", nullable = false)
    private String doctorName;

    @Column(name = "patientname")
    private String patientName;

    @Column(name = "patientnumber")
    private String patientNumber;

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Column(name = "consultationdatetime")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime consultationDateTime;

    public ConsultaRequest(UUID id, String patientNumber, String doctorName, String patientName, String speciality, String description,StatusConsulta status, LocalDateTime  dataConsulta) {
        this.id = id;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.patientNumber = patientNumber;
        this.speciality = speciality;
        this.description = description;
        this.status = status;
        this.consultationDateTime = dataConsulta;

    }
    public  ConsultaRequest(){

    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusConsulta getStatus() { return status; }

    public void setStatus(StatusConsulta status) { this.status = status; }

    public LocalDateTime getConsultationDateTime() {
        return consultationDateTime;
    }

    public void setConsultationDateTime(LocalDateTime consultationDateTime) {
        this.consultationDateTime = consultationDateTime;
    }
}
