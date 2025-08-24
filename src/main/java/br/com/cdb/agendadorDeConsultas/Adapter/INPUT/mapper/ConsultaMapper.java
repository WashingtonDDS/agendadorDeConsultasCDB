package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetailsDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdateDTO;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {
    ConsultaMapper INSTANCE = Mappers.getMapper(ConsultaMapper.class);

    Consulta toDomain(ConsultaRequestDTO request);

    Consulta toDomain(ConsultaUpdateDTO request);

    ConsultaRequestDTO toResponse(Consulta consulta);

    ConsultaDetailsDTO toDetails(Consulta consulta);

    void updateDomainFromDto(ConsultaUpdateDTO request,@MappingTarget Consulta consulta);
}
