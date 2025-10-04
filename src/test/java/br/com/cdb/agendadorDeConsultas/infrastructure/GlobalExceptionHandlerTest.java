package br.com.cdb.agendadorDeConsultas.infrastructure;

import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test/business-rule-exception")
        public void throwBusinessRuleException() {
            throw new BusinessRuleValidationException("Mensagem de teste da regra de negócio");
        }

        @GetMapping("/test/entity-not-found")
        public void throwEntityNotFoundException() {
            throw new EntityNotFoundException("Mensagem de teste de entidade não encontrada");
        }
    }

    @Test
    @DisplayName("Deve capturar BusinessRuleValidationException e retornar 400 Bad Request")

    void handleBusinessRuleValidationException() throws Exception {
        mockMvc.perform(get("/test/business-rule-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Regra de Negócio Violada"))
                .andExpect(jsonPath("$.message").value("Mensagem de teste da regra de negócio"));
    }

    @Test
    @DisplayName("Deve capturar EntityNotFoundException e retornar 404 Not Found")

    void handleEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/entity-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso Não Encontrado"))
                .andExpect(jsonPath("$.message").value("Mensagem de teste de entidade não encontrada"));
    }
}
