package br.com.cdb.agendadorDeConsultas.service;

import br.com.cdb.agendadorDeConsultas.dto.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.dto.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.repositories.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    public List<ConsultaResponseDTO> getConsultas(){
        List<Consulta> consultas = consultaRepository.findAll();
        return consultas.stream()
                .map(consulta -> new ConsultaResponseDTO(
                        consulta.getId(),
                        consulta.getDoctorName(),
                        consulta.getPatientName(),
                        consulta.getPatientNumber(),
                        consulta.getSpeciality(),
                        consulta.getDescription(),
                        consulta.getConsultationDateTime()))
                .toList();
    }


}
