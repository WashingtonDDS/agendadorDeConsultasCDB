package br.com.cdb.agendadorDeConsultas.infrastructure;

import br.com.cdb.agendadorDeConsultas.adapter.output.repositories.ConsultaRepository;
import br.com.cdb.agendadorDeConsultas.adapter.output.repositories.SecretariaRepository;
import br.com.cdb.agendadorDeConsultas.adapter.output.repositories.proxy.ConsultaRepositoryProxy;
import br.com.cdb.agendadorDeConsultas.adapter.output.repositories.proxy.SecretariaRepositoryProxy;
import br.com.cdb.agendadorDeConsultas.core.usecase.ConsultaUseCase;
import br.com.cdb.agendadorDeConsultas.core.usecase.SecretariaUseCase;
import br.com.cdb.agendadorDeConsultas.util.validation.ConsultaValidator;
import br.com.cdb.agendadorDeConsultas.util.validation.SecretariaValidator;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ConsultaValidator consultaValidator(
            ConsultaOutputPort consultaOutputPort,
            SecretariaOutputPort secretariaOutputPort) {
        return new ConsultaValidator(consultaOutputPort, secretariaOutputPort);
    }
    @Bean
    public SecretariaValidator secretariaValidator(
            SecretariaOutputPort secretariaOutputPort,
            PasswordEncoder passwordEncoder) {
        return new SecretariaValidator(secretariaOutputPort, passwordEncoder);
    }

    @Bean
    @Primary
    public ConsultaOutputPort consultaOutputPortProxy(ConsultaRepository consultaRepository) {
        return new ConsultaRepositoryProxy(consultaRepository);
    }

    @Bean
    @Primary
    public SecretariaOutputPort secretariaOutputPortProxy(SecretariaRepository secretariaRepository) {
        return new SecretariaRepositoryProxy(secretariaRepository);
    }


    @Bean
    public ConsultaUseCase consultaUseCaseImpl(ConsultaOutputPort consultaOutputPort, SecretariaOutputPort secretariaOutputPort, ConsultaValidator consultaValidator){
        return new ConsultaUseCase(consultaOutputPort, secretariaOutputPort, consultaValidator);
    }

    @Bean
    public SecretariaUseCase secretariaUseCaseImpl(
            SecretariaOutputPort secretariaOutputPort,
            PasswordEncoder passwordEncoder,
            SecretariaValidator secretariaValidator){
        return new SecretariaUseCase(secretariaOutputPort, passwordEncoder, secretariaValidator);
    }


}
