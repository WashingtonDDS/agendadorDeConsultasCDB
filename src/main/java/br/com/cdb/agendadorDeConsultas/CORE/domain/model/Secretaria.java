package br.com.cdb.agendadorDeConsultas.core.domain.model;

import java.util.UUID;

public class Secretaria {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String password;


    public Secretaria(UUID id, String nome, String cpf, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.password = password;

    }

    public Secretaria(String password) {

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
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
