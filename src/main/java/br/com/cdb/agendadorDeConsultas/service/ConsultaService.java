package br.com.cdb.agendadorDeConsultas.service;

import br.com.cdb.agendadorDeConsultas.dto.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.repositories.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public Consulta createConsulta(ConsultaRequestDTO data){

        Consulta newConsulta = new Consulta();
        newConsulta.setDoctorName(data.doctorName());
        newConsulta.setPatientName(data.patientName());
        newConsulta.setpatientNumber(data.patientNumber());
        newConsulta.setSpeciality(data.speciality());
        newConsulta.setDescription(data.description());
        newConsulta.setConsultationDateTime(data.consultationDateTime());

        consultaRepository.save(newConsulta);

        return newConsulta;
    }
}
