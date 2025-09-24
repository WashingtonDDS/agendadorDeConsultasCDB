package br.com.cdb.agendadorDeConsultas.adapter.input.request;

import java.util.UUID;

public record SecretariaResponse(UUID id, String name, String email) {
}
