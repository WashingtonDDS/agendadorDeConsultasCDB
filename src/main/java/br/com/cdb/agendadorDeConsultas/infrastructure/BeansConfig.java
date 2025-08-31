package br.com.cdb.agendadorDeConsultas.infrastructure;

import br.com.cdb.agendadorDeConsultas.core.usecase.ConsultaUseCase;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public ConsultaUseCase consultaUseCaseImpl(ConsultaOutputPort consultaOutputPort){
        return new ConsultaUseCase(consultaOutputPort);
    }
}
