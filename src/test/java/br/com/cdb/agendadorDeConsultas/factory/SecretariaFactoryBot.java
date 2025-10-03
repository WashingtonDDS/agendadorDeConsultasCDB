package br.com.cdb.agendadorDeConsultas.factory;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;

import java.util.UUID;

public class SecretariaFactoryBot {

    public static Secretaria build() {
        Secretaria secretaria = new Secretaria();
        secretaria.setId(UUID.randomUUID());
        secretaria.setName("Maria (Default)");
        secretaria.setEmail("maria.default@email.com");
        secretaria.setPassword("rawPassword123");
        secretaria.setCpf("123.456.789-00");
        return secretaria;
    }

    public static SecretariaUpdate buildUpdate() {
        return new SecretariaUpdate(
                "Maria (Updated)",
                "maria.updated@email.com",
                "newPassword456"
        );
    }
}
