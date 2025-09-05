package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.port.input.ConsultaInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaController.class);


    @Autowired
    private ConsultaInputPort consultainputPort;

    @Autowired
    private ConsultaMapper consultaMapper;




    @PostMapping
    public ResponseEntity<ConsultaResponse> create(@RequestBody ConsultaRequest body){
        logger.info("Recebida requisição para criar consulta: {}", body);

        Consulta consulta = consultaMapper.toDomain(body);
        Consulta newConsulta = consultainputPort.createConsulta(consulta);

        logger.debug("Consulta criada com ID: {}", newConsulta.getId());
        return ResponseEntity.ok(consultaMapper.toResponse(newConsulta));
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> getAllConsultas(){
        logger.info("Recebida requisição para listar todas as consultas");

        List<Consulta> consultas = consultainputPort.getConsultas();
        List<ConsultaResponse> responseDTOs = consultas.stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());

        logger.debug("Total de consultas encontradas: {}", consultas.size());
        return ResponseEntity.ok(responseDTOs);
    }


    @GetMapping("/proximas")
    public ResponseEntity<List<ConsultaResponse>> getUpcomingConsultas(){
        logger.info("Recebida requisição para listar consultas futuras");

        List<Consulta> consultas = consultainputPort.getUpcomingConsultas();
        List<ConsultaResponse> responseDTOs = consultas.stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());

        logger.debug("Total de consultas futuras encontradas: {}", consultas.size());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetails> getConsultaDetails(@PathVariable UUID id) {
        logger.info("Recebida requisição para buscar detalhes da consulta com ID: {}", id);

        Consulta consulta = consultainputPort.getConsultaDetails(id);
        ConsultaDetails details = consultaMapper.toDetails(consulta);
        return ResponseEntity.ok(details);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> updateConsulta(
            @PathVariable UUID id,
            @RequestBody ConsultaUpdate request) {
        logger.info("Recebida requisição para atualizar consulta com ID: {}", id);

        Consulta updatedConsulta = consultainputPort.updateConsulta(id, request);
        ConsultaResponse responseDTO = consultaMapper.toResponse(updatedConsulta);

        logger.debug("Consulta atualizada com ID: {}", updatedConsulta.getId());
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConsultaResponse> cancelledConsulta(@PathVariable UUID id) {
        logger.info("Recebida requisição para cancelar consulta com ID: {}", id);

        Consulta cancelledConsulta = consultainputPort.canceledConsulta(id);
        ConsultaResponse responseDTO = consultaMapper.toResponse(cancelledConsulta);

        logger.debug("Consulta cancelada com ID: {}", cancelledConsulta.getId());
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable UUID id) {
        logger.info("Recebida requisição para excluir consulta com ID: {}", id);

        consultainputPort.deleteConsulta(id);
        logger.debug("Consulta excluída com ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}
