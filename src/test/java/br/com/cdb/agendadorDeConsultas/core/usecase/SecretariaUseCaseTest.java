package br.com.cdb.agendadorDeConsultas.core.usecase;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.usecase.validation.SecretariaValidator;
import br.com.cdb.agendadorDeConsultas.factory.SecretariaFactoryBot;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SecretariaUseCaseTest {

    @Mock
    private SecretariaOutputPort secretariaOutputPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecretariaValidator validator;

    @InjectMocks
    private SecretariaUseCase secretariaUseCase;

    @Test
    @DisplayName("Deve criar uma secretária com sucesso")
    void create_Success() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        when(passwordEncoder.encode(secretaria.getPassword())).thenReturn("encodedPassword");
        when(secretariaOutputPort.save(any(Secretaria.class))).thenReturn(secretaria);

        Secretaria result = secretariaUseCase.create(secretaria);

        verify(validator, times(1)).validateCreate(secretaria);
        verify(passwordEncoder, times(1)).encode(secretaria.getPassword());
        verify(secretariaOutputPort, times(1)).save(secretaria);
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a validação ao criar falhar")
    void create_ValidationFails() {
        Secretaria secretaria = SecretariaFactoryBot.build();
        doThrow(new IllegalArgumentException("Dados inválidos")).when(validator).validateCreate(secretaria);

        assertThrows(IllegalArgumentException.class, () -> secretariaUseCase.create(secretaria));
        verify(passwordEncoder, never()).encode(anyString());
        verify(secretariaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar todas as secretárias")
    void findAll_Success() {
        List<Secretaria> expectedList = List.of(SecretariaFactoryBot.build(), SecretariaFactoryBot.build());
        when(secretariaOutputPort.findAll()).thenReturn(expectedList);

        List<Secretaria> result = secretariaUseCase.findAll();

        assertEquals(expectedList, result);
        verify(secretariaOutputPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar uma secretária pelo ID")
    void findById_Success() {
        UUID id = UUID.randomUUID();
        Secretaria expectedSecretaria = SecretariaFactoryBot.build();
        when(secretariaOutputPort.findById(id)).thenReturn(expectedSecretaria);

        Secretaria result = secretariaUseCase.findById(id);

        assertEquals(expectedSecretaria, result);
        verify(secretariaOutputPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve propagar exceção ao não encontrar secretária pelo ID")
    void findById_NotFound() {
        UUID id = UUID.randomUUID();
        when(secretariaOutputPort.findById(id)).thenThrow(new RuntimeException("Secretária não encontrada"));

        assertThrows(RuntimeException.class, () -> secretariaUseCase.findById(id));
    }

    @Test
    @DisplayName("Deve deletar uma secretária com sucesso")
    void delete_Success() {
        UUID id = UUID.randomUUID();
        Secretaria secretaria = SecretariaFactoryBot.build();
        when(secretariaOutputPort.findById(id)).thenReturn(secretaria);

        secretariaUseCase.delete(id);

        verify(secretariaOutputPort, times(1)).findById(id);
        verify(secretariaOutputPort, times(1)).delete(secretaria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar secretária inexistente")
    void delete_NotFound() {
        UUID id = UUID.randomUUID();
        when(secretariaOutputPort.findById(id)).thenThrow(new RuntimeException("Secretária não encontrada"));

        assertThrows(RuntimeException.class, () -> secretariaUseCase.delete(id));
        verify(secretariaOutputPort, never()).delete(any());
    }

    @Test
    @DisplayName("Deve atualizar uma secretária com sucesso")
    void update_Success() {
        UUID id = UUID.randomUUID();
        SecretariaUpdate updateRequest = SecretariaFactoryBot.buildUpdate();
        Secretaria existingSecretaria = SecretariaFactoryBot.build();

        when(secretariaOutputPort.findById(id)).thenReturn(existingSecretaria);
        when(passwordEncoder.encode(updateRequest.password())).thenReturn("encodedNewPassword");
        when(secretariaOutputPort.save(any(Secretaria.class))).thenAnswer(inv -> inv.getArgument(0));

        Secretaria result = secretariaUseCase.update(id, updateRequest);

        verify(secretariaOutputPort, times(1)).findById(id);
        verify(validator, times(1)).validateUpdate(existingSecretaria, updateRequest);
        verify(passwordEncoder, times(1)).encode(updateRequest.password());
        verify(secretariaOutputPort, times(1)).save(existingSecretaria);

        assertEquals(updateRequest.name(), result.getName());
        assertEquals(updateRequest.email(), result.getEmail());
        assertEquals("encodedNewPassword", result.getPassword());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar secretária inexistente")
    void update_NotFound() {
        UUID id = UUID.randomUUID();
        SecretariaUpdate updateRequest = SecretariaFactoryBot.buildUpdate();
        when(secretariaOutputPort.findById(id)).thenThrow(new RuntimeException("Secretária não encontrada"));

        assertThrows(RuntimeException.class, () -> secretariaUseCase.update(id, updateRequest));
        verify(validator, never()).validateUpdate(any(), any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(secretariaOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a validação ao atualizar falhar")
    void update_ValidationFails() {
        UUID id = UUID.randomUUID();
        SecretariaUpdate updateRequest = SecretariaFactoryBot.buildUpdate();
        Secretaria existingSecretaria = SecretariaFactoryBot.build();

        when(secretariaOutputPort.findById(id)).thenReturn(existingSecretaria);
        doThrow(new IllegalArgumentException("Dados de atualização inválidos")).when(validator).validateUpdate(existingSecretaria, updateRequest);

        assertThrows(IllegalArgumentException.class, () -> secretariaUseCase.update(id, updateRequest));
        verify(passwordEncoder, never()).encode(anyString());
        verify(secretariaOutputPort, never()).save(any());
    }
}
