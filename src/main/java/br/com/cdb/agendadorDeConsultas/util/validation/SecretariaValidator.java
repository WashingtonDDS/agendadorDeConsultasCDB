package br.com.cdb.agendadorDeConsultas.util.validation;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecretariaValidator {
    private final SecretariaOutputPort secretariaOutputPort;
    private final PasswordEncoder passwordEncoder;

    public SecretariaValidator(SecretariaOutputPort secretariaOutputPort, PasswordEncoder passwordEncoder) {
        this.secretariaOutputPort = secretariaOutputPort;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateCreate(Secretaria request) {
        validarNome(request.getName());
        validarFormatoCpf(request.getCpf());
        validarFormatoEmail(request.getEmail());
        validarComplexidadeSenha(request.getPassword());
        validarCpfParaCriacao(request.getCpf());
        validarEmailParaCriacao(request.getEmail());
    }

    public void validateUpdate(Secretaria secretariaExistente, SecretariaUpdate request) {
        if (request.name() != null && !request.name().isBlank()) {
            validarNome(request.name());
        }
        if (request.email() != null && !request.email().isBlank()) {
            validarFormatoEmail(request.email());
            validarEmailParaAtualizacao(request.email(), secretariaExistente);
        }
        if (request.password() != null && !request.password().isBlank()) {
            validarComplexidadeSenha(request.password());
            validarReutilizacaoSenha(request.password(), secretariaExistente);
        }
    }

    private void validarFormatoEmail(String email) {
        if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new BusinessRuleValidationException("Formato de e-mail inválido.");
        }
    }

    private void validarFormatoCpf(String cpf) {
        if (cpf == null || !cpf.matches("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$")) {
            throw new BusinessRuleValidationException("Formato de CPF inválido. Use o formato XXX.XXX.XXX-XX.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || !nome.matches("^[a-zA-Z\\s]+$")) {
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
        if (senha == null) {
            throw new BusinessRuleValidationException("A senha não pode ser nula.");
        }
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (!senha.matches(passwordPattern)) {
            throw new BusinessRuleValidationException(
                    "A senha deve ter no mínimo 8 caracteres, incluindo pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial (@#$%^&+=)."
            );
        }
    }

    private void validarReutilizacaoSenha(String novaSenha, Secretaria secretariaExistente) {
        if (secretariaExistente.getPassword() != null && passwordEncoder.matches(novaSenha, secretariaExistente.getPassword())) {
            throw new BusinessRuleValidationException("A nova senha não pode ser igual à senha anterior.");
        }
    }
}
