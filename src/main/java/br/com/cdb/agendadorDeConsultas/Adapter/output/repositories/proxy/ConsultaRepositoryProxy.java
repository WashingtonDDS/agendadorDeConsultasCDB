package br.com.cdb.agendadorDeConsultas.adapter.output.repositories.proxy;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConsultaRepositoryProxy implements ConsultaOutputPort {
    private static final Logger logger = LoggerFactory.getLogger(ConsultaRepositoryProxy.class);

    private final ConsultaOutputPort realRepository;
    private final Map<UUID, Consulta> cache = new ConcurrentHashMap<>();

    public ConsultaRepositoryProxy(ConsultaOutputPort realRepository) {
        this.realRepository = realRepository;
    }

    @Override
    public Optional<Consulta> findById(UUID id) {
        Consulta consultaDoCache = cache.get(id);
        if (consultaDoCache != null) {
            logger.info("PROXY HIT: Retornando consulta {} do cache.", id);
            return Optional.of(consultaDoCache);
        }

        logger.info("PROXY MISS: Consulta {} não encontrada no cache. Buscando no repositório real.", id);
        Optional<Consulta> consultaDoBanco = realRepository.findById(id);
        consultaDoBanco.ifPresent(consulta -> cache.put(id, consulta));
        return consultaDoBanco;
    }

    @Override
    public Consulta save(Consulta consulta) {
        logger.info("PROXY: Invalidando cache para a consulta {}.", consulta.getId());
        cache.remove(consulta.getId());
        return realRepository.save(consulta);
    }

    @Override
    public void delete(Consulta consulta) {
        logger.info("PROXY: Invalidando cache para a consulta {}.", consulta.getId());
        cache.remove(consulta.getId());
        realRepository.delete(consulta);
    }

    @Override
    public List<Consulta> findAll() {
        logger.debug("PROXY: Delegando findAll para o repositório real.");
        return realRepository.findAll();
    }

    @Override
    public List<Consulta> findUpcomingConsultas(LocalDateTime now) {
        logger.debug("PROXY: Delegando findUpcomingConsultas para o repositório real.");
        return realRepository.findUpcomingConsultas(now);
    }

    @Override
    public List<Consulta> findByDoctorNameAndDateTime(String doctorName, LocalDateTime dateTime) {
        logger.debug("PROXY: Delegando findByDoctorNameAndDateTime para o repositório real.");
        return realRepository.findByDoctorNameAndDateTime(doctorName, dateTime);
    }
}
