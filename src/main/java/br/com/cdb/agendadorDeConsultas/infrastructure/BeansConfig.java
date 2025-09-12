package br.com.cdb.agendadorDeConsultas.infrastructure;

import br.com.cdb.agendadorDeConsultas.core.usecase.ConsultaUseCase;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ConsultaUseCase consultaUseCaseImpl(ConsultaOutputPort consultaOutputPort){
        return new ConsultaUseCase(consultaOutputPort);
    }
}
