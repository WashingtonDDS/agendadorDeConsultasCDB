package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    ConsultaResponse toResponse(Consulta consulta);

    ConsultaDetails toDetails(Consulta consulta);
}
