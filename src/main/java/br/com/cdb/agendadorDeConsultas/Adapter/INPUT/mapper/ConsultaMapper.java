package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.ConsultaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    Consulta toDomain(ConsultaRequest consultaRequest);

    Consulta toDomainEntity(ConsultaEntity consultaEntity);

    ConsultaEntity toEntity(Consulta consulta);

    ConsultaResponse toResponse(Consulta consulta);

    ConsultaRequest toRequest(Consulta consulta);

    ConsultaDetails toDetails(Consulta consulta);
}
