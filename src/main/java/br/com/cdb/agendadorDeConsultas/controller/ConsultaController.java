package br.com.cdb.agendadorDeConsultas.controller;

import br.com.cdb.agendadorDeConsultas.dto.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
