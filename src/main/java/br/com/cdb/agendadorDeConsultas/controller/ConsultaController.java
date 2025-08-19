package br.com.cdb.agendadorDeConsultas.controller;

import br.com.cdb.agendadorDeConsultas.dto.ConsultaDetailsDTO;
import br.com.cdb.agendadorDeConsultas.dto.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.dto.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.dto.ConsultaUpdateDTO;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;


    @PostMapping
    public ResponseEntity<Consulta> create(@RequestBody ConsultaRequestDTO body){
        Consulta newConsulta = this.consultaService.createConsulta(body);
        return ResponseEntity.ok(newConsulta);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> getAllConsultas(){
        List<ConsultaResponseDTO> allConsultas = this.consultaService.getConsultas();
        return ResponseEntity.ok(allConsultas);

    }

    @GetMapping("/proximas")
    public ResponseEntity<List<ConsultaResponseDTO>> getUpcomingConsultas(){
        List<ConsultaResponseDTO> allUpcomingConsultas = this.consultaService.getUpcomingConsultas();
        return ResponseEntity.ok(allUpcomingConsultas);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetailsDTO> getConsultaDetails(@PathVariable UUID id) {
        ConsultaDetailsDTO consulta = this.consultaService.getConsultaDetails(id);
        return ResponseEntity.ok(consulta);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> updateConsulta(
            @PathVariable UUID id,
            @RequestBody ConsultaUpdateDTO request) {

        Consulta updatedConsulta = consultaService.updateConsulta(id, request);
        return ResponseEntity.ok(new ConsultaResponseDTO(updatedConsulta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable UUID id) {
        consultaService.deleteConsulta(id);
        return ResponseEntity.noContent().build();
    }
}
