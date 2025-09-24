package br.com.cdb.agendadorDeConsultas.core.usecase.validation;

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

    public void validateCreate(Secretaria request) {
        validarNome(request.getName());
        validarEmailParaCriacao(request.getEmail());
        validarCpfParaCriacao(request.getCpf());
        validarComplexidadeSenha(request.getPassword());
    }
    public void validateUpdate(Secretaria secretariaExistente, SecretariaUpdate request) {
        validarNome(request.name());
        validarEmailParaAtualizacao(request.email(), secretariaExistente);
        validarComplexidadeSenha(request.password());
        validarReutilizacaoSenha(request.password(), secretariaExistente);
    }



    private void validarNome(String nome) {
        if (!nome.matches("^[a-zA-Z\\s]+$")) {
            throw new BusinessRuleValidationException("O nome deve conter apenas letras e espaços.");
        }
    }
    private void validarCpfParaCriacao(String cpf) {
        secretariaOutputPort.findByCpf(cpf).ifPresent(s -> {
            throw new BusinessRuleValidationException("CPF já cadastrado.");
        });
    }

    private void validarEmailParaCriacao(String email) {
        secretariaOutputPort.findByEmail(email).ifPresent(s -> {
            throw new BusinessRuleValidationException("E-mail já cadastrado.");
        });
    }

    private void validarEmailParaAtualizacao(String novoEmail, Secretaria secretariaExistente) {
        secretariaOutputPort.findByEmail(novoEmail).ifPresent(outraSecretaria -> {
            if (!outraSecretaria.getId().equals(secretariaExistente.getId())) {
                throw new BusinessRuleValidationException("E-mail já cadastrado para outro usuário.");
            }
        });
    }
    private void validarComplexidadeSenha(String senha) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (!senha.matches(passwordPattern)) {
            throw new BusinessRuleValidationException(
                    "A senha deve ter no mínimo 8 caracteres, incluindo pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial (@#$%^&+=)."
            );
        }
    }


    private void validarReutilizacaoSenha(String novaSenha, Secretaria secretariaExistente) {
        if (passwordEncoder.matches(novaSenha, secretariaExistente.getPassword())) {
            throw new BusinessRuleValidationException("A nova senha не pode ser igual à senha anterior.");
        }
    }
}
