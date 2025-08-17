package br.com.cdb.agendadorDeConsultas.service;

import br.com.cdb.agendadorDeConsultas.dto.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import org.springframework.stereotype.Service;

@Service
class ConsultaService {

    public Consulta createConsulta(ConsultaRequestDTO data){

        Consulta newConsulta = new Consulta();
        newConsulta.setDoctorName(data.doctorName());
        newConsulta.setPatientName(data.patientName());
        newConsulta.setPatientNumber(data.PatientNumber());
        newConsulta.setTitle(data.title());
        newConsulta.setDescription(data.description());
        newConsulta.setDataConsulta(data.dataConsulta());

        return newConsulta;
    }
}
