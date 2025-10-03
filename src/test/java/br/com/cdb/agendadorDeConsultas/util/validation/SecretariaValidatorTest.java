package br.com.cdb.agendadorDeConsultas.util.validation;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import br.com.cdb.agendadorDeConsultas.factory.SecretariaFactoryBot;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretariaValidatorTest {

    @Mock
    private SecretariaOutputPort secretariaOutputPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecretariaValidator secretariaValidator;

    private static final String VALID_PASSWORD = "Password@123";
    private static final String ANOTHER_VALID_PASSWORD = "AnotherPassword@456";

    @BeforeEach
    void setUp() {
        secretariaValidator = new SecretariaValidator(secretariaOutputPort, passwordEncoder);
    }

    @Test
    @DisplayName("validateCreate: Deve passar com dados válidos")
    void validateCreate_Success() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setPassword(VALID_PASSWORD);

        when(secretariaOutputPort.findByCpf(anyString())).thenReturn(Optional.empty());
        when(secretariaOutputPort.findByEmail(anyString())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> secretariaValidator.validateCreate(secretaria));
    }

    @Test
    @DisplayName("validateCreate: Deve falhar com nome inválido")
    void validateCreate_Fails_WithInvalidName() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setName("Nome123");
        secretaria.setPassword(VALID_PASSWORD);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateCreate(secretaria));

        assertEquals("O nome deve conter apenas letras e espaços.", exception.getMessage());
    }

    @Test
    @DisplayName("validateCreate: Deve falhar se CPF já existir")
    void validateCreate_Fails_WhenCpfExists() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setPassword(VALID_PASSWORD);

        when(secretariaOutputPort.findByCpf(secretaria.getCpf())).thenReturn(Optional.of(new Secretaria()));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateCreate(secretaria));

        assertEquals("CPF já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("validateCreate: Deve falhar se E-mail já existir")
    void validateCreate_Fails_WhenEmailExists() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setPassword(VALID_PASSWORD);

        when(secretariaOutputPort.findByEmail(secretaria.getEmail())).thenReturn(Optional.of(new Secretaria()));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateCreate(secretaria));

        assertEquals("E-mail já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("validateCreate: Deve falhar com senha fraca")
    void validateCreate_Fails_WithWeakPassword() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setPassword("senhafraca");

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateCreate(secretaria));

        assertTrue(exception.getMessage().contains("A senha deve ter no mínimo 8 caracteres"));
    }

    @Test
    @DisplayName("validateUpdate: Deve passar com dados válidos")
    void validateUpdate_Success() {
        Secretaria secretariaExistente = SecretariaFactoryBot.build();
        secretariaExistente.setPassword("encodedOldPassword");

        SecretariaUpdate request = new SecretariaUpdate("Nome Valido", "novo.email@valido.com", ANOTHER_VALID_PASSWORD);

        when(secretariaOutputPort.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.matches(request.password(), secretariaExistente.getPassword())).thenReturn(false);

        assertDoesNotThrow(() -> secretariaValidator.validateUpdate(secretariaExistente, request));
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se e-mail já estiver em uso por outro usuário")
    void validateUpdate_Fails_WhenEmailIsUsedByAnotherUser() {
        Secretaria secretariaExistente = SecretariaFactoryBot.build();
        secretariaExistente.setId(UUID.randomUUID());

        Secretaria outraSecretaria = new Secretaria();
        outraSecretaria.setId(UUID.randomUUID());

        SecretariaUpdate request = new SecretariaUpdate("Nome Valido", "email.existente@teste.com", VALID_PASSWORD);

        when(secretariaOutputPort.findByEmail(request.email())).thenReturn(Optional.of(outraSecretaria));

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateUpdate(secretariaExistente, request));

        assertEquals("E-mail já cadastrado para outro usuário.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve passar se e-mail for o mesmo da secretária atual")
    void validateUpdate_Success_WhenEmailIsTheSame() {
        Secretaria secretariaExistente = SecretariaFactoryBot.build();
        secretariaExistente.setId(UUID.randomUUID());
        secretariaExistente.setPassword("encodedOldPassword");

        SecretariaUpdate request = new SecretariaUpdate("Nome Valido", secretariaExistente.getEmail(), ANOTHER_VALID_PASSWORD);

        when(secretariaOutputPort.findByEmail(request.email())).thenReturn(Optional.of(secretariaExistente));
        when(passwordEncoder.matches(request.password(), secretariaExistente.getPassword())).thenReturn(false);

        assertDoesNotThrow(() -> secretariaValidator.validateUpdate(secretariaExistente, request));
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar se a nova senha for igual à anterior")
    void validateUpdate_Fails_WhenPasswordIsReused() {
        Secretaria secretariaExistente = SecretariaFactoryBot.build();
        secretariaExistente.setPassword("encodedOldPassword");

        SecretariaUpdate request = new SecretariaUpdate("Nome Valido", "novo.email@valido.com", VALID_PASSWORD);

        when(passwordEncoder.matches(request.password(), secretariaExistente.getPassword())).thenReturn(true);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateUpdate(secretariaExistente, request));

        assertEquals("A nova senha não pode ser igual à senha anterior.", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateUpdate: Deve falhar com nome inválido")
    void validateUpdate_Fails_WithInvalidName() {
        Secretaria secretariaExistente = SecretariaFactoryBot.build();
        SecretariaUpdate request = new SecretariaUpdate("Nome Inválido 123", "novo.email@valido.com", VALID_PASSWORD);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateUpdate(secretariaExistente, request));

        assertEquals("O nome deve conter apenas letras e espaços.", exception.getMessage());
    }

    @Test
    @DisplayName("validateUpdate: Deve falhar com senha fraca")
    void validateUpdate_Fails_WithWeakPassword() {
        Secretaria secretariaExistente = SecretariaFactoryBot.build();
        SecretariaUpdate request = new SecretariaUpdate("Nome Valido", "novo.email@valido.com", "fraca");

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateUpdate(secretariaExistente, request));

        assertTrue(exception.getMessage().contains("A senha deve ter no mínimo 8 caracteres"));
    }

    @Test
    @DisplayName("validateCreate: Deve falhar com CPF inválido")
    void validateCreate_Fails_WithInvalidCpf() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        secretaria.setCpf("123.456.789-0");
        secretaria.setPassword(VALID_PASSWORD);

        var exception = assertThrows(BusinessRuleValidationException.class,
                () -> secretariaValidator.validateCreate(secretaria));

        assertTrue(exception.getMessage().contains("CPF"));
    }


}
