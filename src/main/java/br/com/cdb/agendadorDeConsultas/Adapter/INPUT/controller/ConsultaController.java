package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

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

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired

    private ConsultaInputPort consultainputPort;


    @PostMapping
    public ResponseEntity<Consulta> create(@RequestBody ConsultaRequestDTO body){
        Consulta newConsulta = consultainputPort.createConsulta(body);
        return ResponseEntity.ok(newConsulta);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> getAllConsultas(){
        List<ConsultaResponseDTO> allConsultas = consultainputPort.getConsultas();
        return ResponseEntity.ok(allConsultas);

    }

    @GetMapping("/proximas")
    public ResponseEntity<List<ConsultaResponseDTO>> getUpcomingConsultas(){
        List<ConsultaResponseDTO> allUpcomingConsultas = consultainputPort.getUpcomingConsultas();
        return ResponseEntity.ok(allUpcomingConsultas);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetailsDTO> getConsultaDetails(@PathVariable UUID id) {
        ConsultaDetailsDTO consulta = consultainputPort.getConsultaDetails(id);
        return ResponseEntity.ok(consulta);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> updateConsulta(
            @PathVariable UUID id,
            @RequestBody ConsultaUpdateDTO request) {

        Consulta updatedConsulta = consultainputPort.updateConsulta(id, request);
        return ResponseEntity.ok(new ConsultaResponseDTO(updatedConsulta));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Consulta>cancelledConsulta(
            @PathVariable UUID id) {
        Consulta cancelledConsulta = consultainputPort.canceledConsulta(id);
        return ResponseEntity.ok(cancelledConsulta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable UUID id) {
        consultainputPort.deleteConsulta(id);
        return ResponseEntity.noContent().build();
    }
}
