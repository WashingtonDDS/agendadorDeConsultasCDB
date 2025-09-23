package br.com.cdb.agendadorDeConsultas.core.usecase.validation;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecretariaValidator {
    private final SecretariaOutputPort secretariaOutputPort;
    private final PasswordEncoder passwordEncoder;

    public SecretariaValidator(SecretariaOutputPort secretariaOutputPort, PasswordEncoder passwordEncoder) {
        this.secretariaOutputPort = secretariaOutputPort;
        this.passwordEncoder = passwordEncoder;
    }


    public void validate(Secretaria secretariaExistente, SecretariaUpdate request) {
        validarNome(request.name());
        validarEmail(request.email(), secretariaExistente);
        validarSenha(request.password(), secretariaExistente);
    }
    public void validateCreate(Secretaria secretariaExistente, SecretariaRequest request) {
        validarNome(request.name());
        validarEmail(request.email(), secretariaExistente);
        validarSenha(request.password(), secretariaExistente);
    }



    private void validarNome(String nome) {
        if (!nome.matches("^[a-zA-Z\\s]+$")) {
            throw new BusinessRuleValidationException("O nome deve conter apenas letras e espaços.");
        }
    }


    private void validarEmail(String novoEmail, Secretaria secretariaExistente) {
        secretariaOutputPort.findByEmail(novoEmail).ifPresent(outraSecretaria -> {
            if (!outraSecretaria.getId().equals(secretariaExistente.getId())) {
                throw new BusinessRuleValidationException("E-mail já cadastrado para outro usuário.");
            }
        });
    }


    private void validarSenha(String novaSenha, Secretaria secretariaExistente) {

        if (novaSenha == null || novaSenha.length() < 8) {
            throw new BusinessRuleValidationException(

                    "A senha deve ter no mínimo 8 caracteres."
            );

        }

        if (passwordEncoder.matches(novaSenha, secretariaExistente.getPassword())) {
            throw new BusinessRuleValidationException("A nova senha não pode ser igual à senha anterior.");
        }

    }
}
