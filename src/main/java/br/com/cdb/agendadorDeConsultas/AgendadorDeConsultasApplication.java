package br.com.cdb.agendadorDeConsultas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @io.swagger.v3.oas.annotations.info.Info(
				title = "API de Agendamento de Consultas",
				version = "1.0",
				description = "API para agendamento de consultas médicas."
		))
public class AgendadorDeConsultasApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendadorDeConsultasApplication.class, args);
	}

}
