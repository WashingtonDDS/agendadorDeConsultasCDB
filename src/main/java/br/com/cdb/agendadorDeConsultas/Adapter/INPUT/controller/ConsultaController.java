package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.mapper.ConsultaMapper;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetailsDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdateDTO;
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
    public ResponseEntity<ConsultaResponseDTO> create(@RequestBody ConsultaRequestDTO body){
        Consulta newConsulta = consultainputPort.createConsulta(body);
        ConsultaResponseDTO responseDTO = consultaMapper.toResponse(newConsulta);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> getAllConsultas(){
        List<Consulta> consultas = consultainputPort.getConsultas();
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }


    @GetMapping("/proximas")
    public ResponseEntity<List<ConsultaResponseDTO>> getUpcomingConsultas(){
        List<Consulta> consultas = consultainputPort.getUpcomingConsultas();
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetailsDTO> getConsultaDetails(@PathVariable UUID id) {
        Consulta consulta = consultainputPort.getConsultaDetails(id);
        ConsultaDetailsDTO detailsDTO = consultaMapper.toDetails(consulta);
        return ResponseEntity.ok(detailsDTO);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> updateConsulta(
            @PathVariable UUID id,
            @RequestBody ConsultaUpdateDTO request) {
        Consulta updatedConsulta = consultainputPort.updateConsulta(id, request);
        ConsultaResponseDTO responseDTO = consultaMapper.toResponse(updatedConsulta);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> cancelledConsulta(@PathVariable UUID id) {
        Consulta cancelledConsulta = consultainputPort.canceledConsulta(id);
        ConsultaResponseDTO responseDTO = consultaMapper.toResponse(cancelledConsulta);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable UUID id) {
        consultainputPort.deleteConsulta(id);
        return ResponseEntity.noContent().build();
    }
}
