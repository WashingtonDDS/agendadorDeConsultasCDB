package br.com.cdb.agendadorDeConsultas.service;

import br.com.cdb.agendadorDeConsultas.dto.*;
import br.com.cdb.agendadorDeConsultas.entity.Consulta;
import br.com.cdb.agendadorDeConsultas.entity.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.repositories.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public Consulta createConsulta(ConsultaRequestDTO data){

        Consulta newConsulta = new Consulta();
        newConsulta.setDoctorName(data.doctorName());
        newConsulta.setPatientName(data.patientName());
        newConsulta.setPatientNumber(data.patientNumber());
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
                        consulta.getStatus(),
                        consulta.getConsultationDateTime()))
                .toList();
    }

    public List<ConsultaResponseDTO> getUpcomingConsultas() {
        List<Consulta> upcomingConsultas = consultaRepository.findUpcomingConsultas(LocalDateTime.now());
        return upcomingConsultas.stream()
                .filter(consulta -> consulta.getStatus() != StatusConsulta.CANCELADA)
                .map(consulta -> new ConsultaResponseDTO(
                        consulta.getId(),
                        consulta.getDoctorName(),
                        consulta.getPatientName(),
                        consulta.getPatientNumber(),
                        consulta.getSpeciality(),
                        consulta.getDescription(),
                        consulta.getStatus(),
                        consulta.getConsultationDateTime()))
                .toList();
    }

    public ConsultaDetailsDTO getConsultaDetails(@RequestParam("id") UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        return new ConsultaDetailsDTO(
                consulta.getId(),
                consulta.getDoctorName(),
                consulta.getPatientName(),
                consulta.getPatientNumber(),
                consulta.getSpeciality(),
                consulta.getDescription(),
                consulta.getConsultationDateTime()
        );
    }

    public Consulta updateConsulta( UUID id, ConsultaUpdateDTO request) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        if (request.doctorName() != null) {
            consulta.setDoctorName(request.doctorName());
        }
        if (request.patientName() != null) {
            consulta.setPatientName(request.patientName());
        }
        if (request.patientNumber() != null) {
            consulta.setPatientNumber(request.patientNumber());
        }
        if (request.consultationDateTime() != null) {
            consulta.setConsultationDateTime(request.consultationDateTime());
        }

        return consultaRepository.save(consulta);
    }

    public Consulta canceledConsulta(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        consulta.setStatus(StatusConsulta.CANCELADA);

        return consultaRepository.save(consulta);
    }
    public void deleteConsulta(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));
        consultaRepository.delete(consulta);
    }


}
