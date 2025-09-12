package br.com.cdb.agendadorDeConsultas.core.domain.model;

import java.util.UUID;

public class Secretaria {
    private UUID id;
    private String name;
    private String cpf;
    private String email;
    private String password;


    public Secretaria(UUID id, String name, String cpf, String email, String password) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;

    }

    public Secretaria(String password) {

    }

    public Secretaria() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return name;
    }

    public void setNome(String nome) {
        this.name = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
