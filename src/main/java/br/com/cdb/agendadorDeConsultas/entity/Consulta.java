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

    private String title;

    private String description;

    private Date dataConsulta;

    public Consulta(UUID id, String title, String description, Date dataConsulta) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dataConsulta = dataConsulta;
    }
    public  Consulta(){

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
