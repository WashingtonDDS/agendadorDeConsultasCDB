package br.com.cdb.agendadorDeConsultas.controller;

import br.com.cdb.agendadorDeConsultas.dto.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.dto.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
