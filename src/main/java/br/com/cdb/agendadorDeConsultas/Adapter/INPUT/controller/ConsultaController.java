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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/consultas")
public class ConsultaController implements SwaggerConsultaController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaController.class);


    @Autowired
    private ConsultaInputPort consultainputPort;

    @Autowired
    private ConsultaMapper consultaMapper;




    @PostMapping("/{secretariaId}")
    public ResponseEntity<ConsultaResponse> create(@PathVariable UUID secretariaId, @RequestBody ConsultaRequest body){
        logger.info("Recebida requisição para criar consulta para secretaria {}: {}", secretariaId, body);

        Consulta consulta = consultaMapper.toDomain(body);
        Consulta newConsulta = consultainputPort.createConsulta(secretariaId, consulta);

        logger.debug("Consulta criada com ID: {} para secretaria {}", newConsulta.getId(), secretariaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaMapper.toResponse(newConsulta));
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

    @PutMapping("/{secretariaId}/{id}")
    public ResponseEntity<ConsultaResponse> updateConsulta(
            @PathVariable UUID secretariaId,
            @PathVariable UUID id,
            @RequestBody ConsultaUpdate request) {
        logger.info("Recebida requisição para atualizar consulta com ID: {} pela secretaria com ID: {}", id, secretariaId);

        Consulta updatedConsulta = consultainputPort.updateConsulta(secretariaId, id, request);
        ConsultaResponse responseDTO = consultaMapper.toResponse(updatedConsulta);

        logger.debug("Consulta atualizada com ID: {} pela secretaria com ID: {}", updatedConsulta.getId(), secretariaId);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{secretariaId}/{id}")
    public ResponseEntity<ConsultaResponse> cancelledConsulta(@PathVariable UUID secretariaId, @PathVariable UUID id) {
        logger.info("Recebida requisição para cancelar consulta com ID: {} pela secretaria com ID: {}", id, secretariaId);

        Consulta cancelledConsulta = consultainputPort.canceledConsulta(secretariaId, id);
        ConsultaResponse responseDTO = consultaMapper.toResponse(cancelledConsulta);

        logger.debug("Consulta cancelada com ID: {} pela secretaria com ID: {}", cancelledConsulta.getId(), secretariaId);
        return ResponseEntity.ok(responseDTO);
    }


    @DeleteMapping("/{secretariaId}/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable UUID secretariaId, @PathVariable UUID id) {
        logger.info("Recebida requisição para excluir consulta com ID: {} pela secretaria com ID: {}", id, secretariaId);

        consultainputPort.deleteConsulta(secretariaId, id);
        logger.debug("Consulta excluída com ID: {} pela secretaria com ID: {}", id, secretariaId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{secretariaId}/{originalConsultaId}/retorno")
    @Override
    public ResponseEntity<ConsultaResponse> createFollowUp(
            @PathVariable UUID secretariaId,
            @PathVariable UUID originalConsultaId) {

        logger.info("Recebida requisição para criar consulta de retorno baseada na consulta ID: {} pela secretaria ID: {}",
                originalConsultaId, secretariaId);

        Consulta followUpConsulta = consultainputPort.createFollowUpConsulta(secretariaId, originalConsultaId);
        ConsultaResponse response = consultaMapper.toResponse(followUpConsulta);

        logger.debug("Consulta de retorno criada com ID: {}", followUpConsulta.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
