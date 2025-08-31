package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.port.input.ConsultaInputPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaInputPort consultainputPort;

    @Autowired
    private ConsultaMapper consultaMapper;




    @PostMapping
    public ResponseEntity<ConsultaResponse> create(@RequestBody ConsultaRequest body){
        Consulta consulta = consultaMapper.toDomain(body);
        Consulta newConsulta = consultainputPort.createConsulta(consulta);
        return ResponseEntity.ok(consultaMapper.toResponse(newConsulta));
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> getAllConsultas(){
        List<Consulta> consultas = consultainputPort.getConsultas();
        List<ConsultaResponse> responseDTOs = consultas.stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }


    @GetMapping("/proximas")
    public ResponseEntity<List<ConsultaResponse>> getUpcomingConsultas(){
        List<Consulta> consultas = consultainputPort.getUpcomingConsultas();
        List<ConsultaResponse> responseDTOs = consultas.stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetails> getConsultaDetails(@PathVariable UUID id) {
        Consulta consulta = consultainputPort.getConsultaDetails(id);
        ConsultaDetails details = consultaMapper.toDetails(consulta);
        return ResponseEntity.ok(details);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> updateConsulta(
            @PathVariable UUID id,
            @RequestBody ConsultaUpdate request) {
        Consulta updatedConsulta = consultainputPort.updateConsulta(id, request);
        ConsultaResponse responseDTO = consultaMapper.toResponse(updatedConsulta);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConsultaResponse> cancelledConsulta(@PathVariable UUID id) {
        Consulta cancelledConsulta = consultainputPort.canceledConsulta(id);
        ConsultaResponse responseDTO = consultaMapper.toResponse(cancelledConsulta);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable UUID id) {
        consultainputPort.deleteConsulta(id);
        return ResponseEntity.noContent().build();
    }
}
