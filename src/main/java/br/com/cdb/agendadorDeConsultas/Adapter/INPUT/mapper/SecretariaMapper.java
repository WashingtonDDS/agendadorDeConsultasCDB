package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.SecretariaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.SecretariaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SecretariaMapper {

    Secretaria toDomain(SecretariaRequest secretariaRequest);

    Secretaria toDomainEntity(SecretariaEntity secretariaEntity);

    SecretariaEntity toEntity(Secretaria secretaria);

    SecretariaResponse toResponse(Secretaria secretaria);


}
