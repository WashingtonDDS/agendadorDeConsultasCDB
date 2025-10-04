package br.com.cdb.agendadorDeConsultas.core.usecase;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.util.validation.SecretariaValidator;
import br.com.cdb.agendadorDeConsultas.port.input.SecretariaInputPort;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

public class SecretariaUseCase implements SecretariaInputPort {

    private final SecretariaOutputPort secretariaOutputPort;
    private final PasswordEncoder passwordEncoder;
    private final SecretariaValidator validator;


    public SecretariaUseCase(SecretariaOutputPort secretariaOutputPort, PasswordEncoder passwordEncoder, SecretariaValidator validator) {
        this.secretariaOutputPort = secretariaOutputPort;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public Secretaria create(Secretaria secretaria) {
        validator.validateCreate(secretaria);

        secretaria.setPassword(passwordEncoder.encode(secretaria.getPassword()));
        return secretariaOutputPort.save(secretaria);
    }

    @Override
    public List<Secretaria> findAll() {
        return secretariaOutputPort.findAll();
    }

    @Override
    public Secretaria findById(UUID id) {
        return secretariaOutputPort.findById(id);
    }

    @Override
    public void delete(UUID id) {
        Secretaria secretaria = secretariaOutputPort.findById(id);
        secretariaOutputPort.delete(secretaria);
    }

    @Override
    public Secretaria update(UUID id, SecretariaUpdate secretariaUpdate) {
        Secretaria secretaria = secretariaOutputPort.findById(id);

        validator.validateUpdate(secretaria, secretariaUpdate);

        secretaria.setName(secretariaUpdate.name());
        secretaria.setEmail(secretariaUpdate.email());
        secretaria.setPassword(passwordEncoder.encode(secretariaUpdate.password()));
        return secretariaOutputPort.save(secretaria);
    }
}
