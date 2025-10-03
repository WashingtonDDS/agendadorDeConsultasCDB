package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Consultas", description = "Endpoints para agendamento e gerenciamento de consultas")
@RequestMapping("/consultas")
public interface SwaggerConsultaController {

    @PostMapping("/{secretariaId}")
    @Operation(summary = "Agenda uma nova consulta", description = "Registra uma nova consulta no sistema, vinculada a uma secretaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta agendada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Secretaria não encontrada para o ID informado")
    })
    ResponseEntity<ConsultaResponse> create(@PathVariable UUID secretariaId, @RequestBody ConsultaRequest body);

    @GetMapping
    @Operation(summary = "Lista todas as consultas", description = "Retorna uma lista com todas as consultas cadastradas no sistema.")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    ResponseEntity<List<ConsultaResponse>> getAllConsultas();

    @GetMapping("/proximas")
    @Operation(summary = "Lista as próximas consultas agendadas", description = "Retorna uma lista de todas as consultas futuras que não foram canceladas.")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    ResponseEntity<List<ConsultaResponse>> getUpcomingConsultas();

    @GetMapping("/{id}")
    @Operation(summary = "Busca os detalhes de uma consulta por ID", description = "Retorna os detalhes completos de uma consulta específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes da consulta encontrados com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaDetails.class))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada para o ID informado")
    })
    ResponseEntity<ConsultaDetails> getConsultaDetails(@PathVariable UUID id);

    @PutMapping("/{secretariaId}/{id}")
    @Operation(summary = "Atualiza uma consulta existente", description = "Permite a alteração dos dados de uma consulta agendada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Consulta ou secretaria não encontrada")
    })
    ResponseEntity<ConsultaResponse> updateConsulta(
            @PathVariable UUID secretariaId,
            @PathVariable UUID id,
            @RequestBody ConsultaUpdate request);

    @PatchMapping("/{secretariaId}/{id}")
    @Operation(summary = "Cancela uma consulta", description = "Altera o status de uma consulta para 'CANCELADA'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta cancelada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex: consulta já ocorreu)"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada para o ID informado")
    })
    ResponseEntity<ConsultaResponse> cancelledConsulta(@PathVariable UUID secretariaId, @PathVariable UUID id);

    @DeleteMapping("/{secretariaId}/{id}")
    @Operation(summary = "Exclui uma consulta", description = "Remove permanentemente o registro de uma consulta do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consulta excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada para o ID informado")
    })
    ResponseEntity<Void> deleteConsulta(@PathVariable UUID secretariaId, @PathVariable UUID id);
}