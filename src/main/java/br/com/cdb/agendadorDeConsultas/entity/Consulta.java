package br.com.cdb.agendadorDeConsultas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.UUID;

@Table(name = "consulta")
@Entity

public class Consulta {
    @Id
    @GeneratedValue
    private UUID id;

    private String doctorName;

    private String patientName;

    private String PatientNumber;

    private String title;

    private String description;

    private Date dataConsulta;

    public Consulta(UUID id,String PatientNumber, String doctorName,String patientName, String title, String description, Date dataConsulta) {
        this.id = id;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.PatientNumber = PatientNumber;
        this.title = title;
        this.description = description;
        this.dataConsulta = dataConsulta;

    }
    public  Consulta(){

    }

    public String getPatientNumber() {
        return PatientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        PatientNumber = patientNumber;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(Date dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
}
