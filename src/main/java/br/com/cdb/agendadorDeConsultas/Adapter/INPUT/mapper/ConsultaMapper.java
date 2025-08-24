package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetailsDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    ConsultaResponseDTO toResponse(Consulta consulta);

    ConsultaDetailsDTO toDetails(Consulta consulta);
}
