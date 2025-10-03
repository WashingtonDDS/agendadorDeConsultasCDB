package br.com.cdb.agendadorDeConsultas.adapter.output.repositories.proxy;

import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SecretariaRepositoryProxy implements SecretariaOutputPort {

    private static final Logger logger = LoggerFactory.getLogger(SecretariaRepositoryProxy.class);

    private final SecretariaOutputPort realRepository;


    private final Map<UUID, Secretaria> cacheById = new ConcurrentHashMap<>();
    private final Map<String, Secretaria> cacheByEmail = new ConcurrentHashMap<>();
    private final Map<String, Secretaria> cacheByCpf = new ConcurrentHashMap<>();

    public SecretariaRepositoryProxy(SecretariaOutputPort realRepository) {
        this.realRepository = realRepository;
    }

    @Override
    public Secretaria findById(UUID id) {
        if (cacheById.containsKey(id)) {
            logger.info("PROXY HIT: Retornando secretaria {} do cache (por ID).", id);
            return cacheById.get(id);
        }
        logger.info("PROXY MISS: Secretaria {} não encontrada no cache. Buscando no repositório real.", id);
        Secretaria secretaria = realRepository.findById(id);
        if (secretaria != null) {
            updateAllCaches(secretaria);
        }
        return secretaria;
    }

    @Override
    public Optional<Secretaria> findByEmail(String email) {
        if (cacheByEmail.containsKey(email)) {
            logger.info("PROXY HIT: Retornando secretaria do cache (por Email).");
            return Optional.of(cacheByEmail.get(email));
        }
        logger.info("PROXY MISS: Secretaria com email {} não encontrada no cache. Buscando no repositório real.", email);
        Optional<Secretaria> secretaria = realRepository.findByEmail(email);
        secretaria.ifPresent(this::updateAllCaches);
        return secretaria;
    }

    @Override
    public Optional<Secretaria> findByCpf(String cpf) {
        if (cacheByCpf.containsKey(cpf)) {
            logger.info("PROXY HIT: Retornando secretaria do cache (por CPF).");
            return Optional.of(cacheByCpf.get(cpf));
        }
        logger.info("PROXY MISS: Secretaria com CPF {} não encontrada no cache. Buscando no repositório real.", cpf);
        Optional<Secretaria> secretaria = realRepository.findByCpf(cpf);
        secretaria.ifPresent(this::updateAllCaches);
        return secretaria;
    }

    @Override
    public Secretaria save(Secretaria secretaria) {

        invalidateAllCaches(secretaria);
        return realRepository.save(secretaria);
    }

    @Override
    public void delete(Secretaria secretaria) {
        invalidateAllCaches(secretaria);
        realRepository.delete(secretaria);
    }

    @Override
    public List<Secretaria> findAll() {
        logger.debug("PROXY: Delegando findAll para o repositório real.");
        return realRepository.findAll();
    }


    private void updateAllCaches(Secretaria secretaria) {
        logger.debug("PROXY: Atualizando todos os caches para a secretaria {}", secretaria.getId());
        cacheById.put(secretaria.getId(), secretaria);
        cacheByEmail.put(secretaria.getEmail(), secretaria);
        cacheByCpf.put(secretaria.getCpf(), secretaria);
    }

    private void invalidateAllCaches(Secretaria secretaria) {
        if (secretaria != null) {
            logger.info("PROXY: Invalidando todos os caches para a secretaria {}", secretaria.getId());
            cacheById.remove(secretaria.getId());
            cacheByEmail.remove(secretaria.getEmail());
            cacheByCpf.remove(secretaria.getCpf());
        }
    }
}