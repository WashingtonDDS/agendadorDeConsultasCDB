package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.adapter.output.entity.ConsultaEntity;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.factory.ConsultaFactoryBot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ConsultaMapperTest {

    @Autowired
    private ConsultaMapper consultaMapper;

    @Test
    @DisplayName("Deve mapear ConsultaRequest para Consulta Domain")
    void toDomainFromRequest() {
        ConsultaRequest request = ConsultaFactoryBot.buildRequest();

        Consulta domain = consultaMapper.toDomain(request);

        assertNotNull(domain);
        assertEquals(request.doctorName(), domain.getDoctorName());
        assertEquals(request.patientName(), domain.getPatientName());
        assertEquals(request.patientNumber(), domain.getPatientNumber());
        assertEquals(request.speciality(), domain.getSpeciality());
        assertEquals(request.description(), domain.getDescription());
        assertEquals(request.consultationDateTime(), domain.getConsultationDateTime());
    }

    @Test
    @DisplayName("Deve mapear ConsultaEntity para Consulta Domain")
    void toDomainFromEntity() {
        ConsultaEntity entity = ConsultaFactoryBot.buildEntity();

        Consulta domain = consultaMapper.toDomainEntity(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getDoctorName(), domain.getDoctorName());
        assertEquals(entity.getPatientName(), domain.getPatientName());
        assertEquals(entity.getPatientNumber(), domain.getPatientNumber());
        assertEquals(entity.getSpeciality(), domain.getSpeciality());
        assertEquals(entity.getDescription(), domain.getDescription());
        assertEquals(entity.getConsultationDateTime(), domain.getConsultationDateTime());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getSecretariaId(), domain.getSecretariaId());
    }

    @Test
    @DisplayName("Deve mapear Consulta Domain para ConsultaEntity")
    void toEntity() {
        Consulta domain = ConsultaFactoryBot.build();

        ConsultaEntity entity = consultaMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getDoctorName(), entity.getDoctorName());
        assertEquals(domain.getPatientName(), entity.getPatientName());
        assertEquals(domain.getPatientNumber(), entity.getPatientNumber());
        assertEquals(domain.getSpeciality(), entity.getSpeciality());
        assertEquals(domain.getDescription(), entity.getDescription());
        assertEquals(domain.getConsultationDateTime(), entity.getConsultationDateTime());
        assertEquals(domain.getStatus(), entity.getStatus());
        assertEquals(domain.getSecretariaId(), entity.getSecretariaId());
    }

    @Test
    @DisplayName("Deve mapear Consulta Domain para ConsultaResponse")
    void toResponse() {
        Consulta domain = ConsultaFactoryBot.build();

        ConsultaResponse response = consultaMapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(domain.getId(), response.id());
        assertEquals(domain.getDoctorName(), response.doctorName());
        assertEquals(domain.getPatientName(), response.patientName());
        assertEquals(domain.getPatientNumber(), response.patientNumber());
        assertEquals(domain.getSpeciality(), response.speciality());
        assertEquals(domain.getDescription(), response.description());
        assertEquals(domain.getStatus(), response.status());
        assertEquals(domain.getConsultationDateTime(), response.consultationDateTime());
        assertEquals(domain.getSecretariaId(), response.secretariaId());
    }

    @Test
    @DisplayName("Deve mapear Consulta Domain para ConsultaRequest")
    void toRequest() {
        Consulta domain = ConsultaFactoryBot.build();

        ConsultaRequest request = consultaMapper.toRequest(domain);

        assertNotNull(request);
        assertEquals(domain.getDoctorName(), request.doctorName());
        assertEquals(domain.getPatientName(), request.patientName());
        assertEquals(domain.getPatientNumber(), request.patientNumber());
        assertEquals(domain.getSpeciality(), request.speciality());
        assertEquals(domain.getDescription(), request.description());
        assertEquals(domain.getConsultationDateTime(), request.consultationDateTime());
    }

    @Test
    @DisplayName("Deve mapear Consulta Domain para ConsultaDetails")
    void toDetails() {
        Consulta domain = ConsultaFactoryBot.build();

        ConsultaDetails details = consultaMapper.toDetails(domain);

        assertNotNull(details);
        assertEquals(domain.getId(), details.id());
        assertEquals(domain.getDoctorName(), details.doctorName());
        assertEquals(domain.getPatientName(), details.patientName());
        assertEquals(domain.getPatientNumber(), details.patientNumber());
        assertEquals(domain.getSpeciality(), details.speciality());
        assertEquals(domain.getDescription(), details.description());
        assertEquals(domain.getStatus(), details.status());
        assertEquals(domain.getConsultationDateTime(), details.consultationDateTime());
    }
}
