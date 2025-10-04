package br.com.cdb.agendadorDeConsultas.adapter.input.controller;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaUpdate;
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

@Tag(name = "Secretarias", description = "Endpoints para gerenciamento de secretarias")
@RequestMapping("/secretarias")
public interface SwaggerSecretariaController {

    @PostMapping
    @Operation(summary = "Cria uma nova secretaria", description = "Registra uma nova secretaria no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secretaria criada com sucesso",
                    content = @Content(schema = @Schema(implementation = SecretariaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: CPF duplicado ou inválido)")
    })
    ResponseEntity<SecretariaResponse> createSecretaria(@RequestBody SecretariaRequest body);

    @GetMapping
    @Operation(summary = "Lista todas as secretarias", description = "Retorna uma lista com todas as secretarias cadastradas.")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    ResponseEntity<List<SecretariaResponse>> getAllSecretarias();

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma secretaria por ID", description = "Retorna os detalhes de uma secretaria específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secretaria encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = SecretariaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Secretaria não encontrada para o ID fornecido")
    })
    ResponseEntity<SecretariaResponse> getSecretariaById(@PathVariable("id") UUID id);

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma secretaria existente", description = "Permite a alteração dos dados de uma secretaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secretaria atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = SecretariaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Secretaria não encontrada para o ID fornecido")
    })
    ResponseEntity<SecretariaResponse> updateSecretaria(@PathVariable("id") UUID id, @RequestBody SecretariaUpdate body);

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma secretaria", description = "Remove permanentemente o registro de uma secretaria do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Secretaria excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Secretaria não encontrada para o ID fornecido")
    })
    ResponseEntity<Void> deleteSecretaria(@PathVariable("id") UUID id);
}