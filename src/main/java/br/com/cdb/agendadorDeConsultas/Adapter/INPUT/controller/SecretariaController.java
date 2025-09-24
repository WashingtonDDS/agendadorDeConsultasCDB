package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.SecretariaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.core.usecase.validation.SecretariaValidator;
import br.com.cdb.agendadorDeConsultas.port.input.SecretariaInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/secretarias")
public class SecretariaController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaController.class);

    private final SecretariaInputPort secretariaInputPort;

    private final SecretariaValidator validator;

    private final SecretariaMapper secretariaMapper;

    public SecretariaController(SecretariaValidator validator, SecretariaMapper secretariaMapper, SecretariaInputPort secretariaInputPort) {
        this.validator = validator;
        this.secretariaMapper = secretariaMapper;
        this.secretariaInputPort = secretariaInputPort;
    }

    @PostMapping
    public ResponseEntity<SecretariaResponse> createSecretaria(@RequestBody SecretariaRequest body) {
        logger.info("Recebida requisição para criar secretaria: {}", body);

        Secretaria secretaria = secretariaMapper.toDomain(body);
        Secretaria newSecretaria = secretariaInputPort.create(secretaria);

        logger.debug("Secretaria criada com ID: {}", newSecretaria.getId());
        return ResponseEntity.ok(secretariaMapper.toResponse(newSecretaria));
    }

    @GetMapping
    public ResponseEntity<List<SecretariaResponse>> getAllSecretarias(){
        logger.info("Recebida requisição para listar todas as secretarias");
        List<Secretaria> secretarias = secretariaInputPort.findAll();
        List<SecretariaResponse> responseDTOs = secretarias.stream()
                .map(secretariaMapper::toResponse)
                .collect(Collectors.toList());
        logger.debug("Total de secretarias encontradas: {}", secretarias.size());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretariaResponse> getSecretariaById(@PathVariable("id") UUID id) {
        logger.info("Recebida requisição para buscar secretaria com ID: {}", id);
        Secretaria secretaria = secretariaInputPort.findById(id);
        return ResponseEntity.ok(secretariaMapper.toResponse(secretaria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SecretariaResponse>updateSecretaria(@PathVariable("id") UUID id, @RequestBody SecretariaUpdate body) {
        logger.info("Recebida requisição para atualizar secretaria com ID: {}", id);
        Secretaria updatedSecretaria = secretariaInputPort.update(id, body);
        return ResponseEntity.ok(secretariaMapper.toResponse(updatedSecretaria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecretaria(@PathVariable("id") UUID id) {
        logger.info("Recebida requisição para deletar secretaria com ID: {}", id);
        secretariaInputPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
