package br.com.cdb.agendadorDeConsultas.core.domain.model;



import java.time.LocalDateTime;
import java.util.UUID;


public class Consulta implements Cloneable {

    private UUID id;



    private String doctorName;


    private String patientName;

    private String patientNumber;


    private String speciality;

    private String description;


    private StatusConsulta status = StatusConsulta.AGENDADA;

    private LocalDateTime consultationDateTime;

    private UUID secretariaId;

    public Consulta(UUID id, String patientNumber, String doctorName, String patientName, String speciality, String description, StatusConsulta status, LocalDateTime  dataConsulta, UUID secretariaId) {
        this.id = id;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.patientNumber = patientNumber;
        this.speciality = speciality;
        this.description = description;
        this.status = status;
        this.consultationDateTime = dataConsulta;

        this.secretariaId = secretariaId;
    }

    public UUID getSecretariaId() {
        return secretariaId;
    }

    public void setSecretariaId(UUID secretariaId) {
        this.secretariaId = secretariaId;
    }

    public  Consulta(){

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

    @Override
    public Consulta clone() {
        try {
            return (Consulta) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
