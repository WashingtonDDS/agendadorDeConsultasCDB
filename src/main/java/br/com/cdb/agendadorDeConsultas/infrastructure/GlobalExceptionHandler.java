package br.com.cdb.agendadorDeConsultas.infrastructure;

import br.com.cdb.agendadorDeConsultas.core.exception.BusinessRuleValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<Object>handleBusinessRuleValidationException(BusinessRuleValidationException ex) {
        Map<String, Object> body = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Regra de Negócio Violada",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
