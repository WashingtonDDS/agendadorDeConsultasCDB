package br.com.cdb.agendadorDeConsultas.factory;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConsultaFactoryBot {

    public static Consulta build() {
        Consulta consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPatientName("John Doe (Default)");
        consulta.setDoctorName("Dr. Smith");
        consulta.setPatientNumber("123456789");
        consulta.setSpeciality("Cardiologia");
        consulta.setDescription("Consulta de rotina");
        consulta.setConsultationDateTime(LocalDateTime.now().plusDays(5));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setSecretariaId(UUID.randomUUID());
        return consulta;
    }

    public static ConsultaUpdate buildUpdate() {
        return new ConsultaUpdate(
                "Dr. House (Updated)",
                "Jane Doe (Updated)",
                "9876543210",
                LocalDateTime.now().plusDays(10)
        );
    }
}
