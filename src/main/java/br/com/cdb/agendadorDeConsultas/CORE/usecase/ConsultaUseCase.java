package br.com.cdb.agendadorDeConsultas.core.usecase;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequestDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponseDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetailsDTO;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdateDTO;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.port.input.ConsultaInputPort;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultaUseCase implements ConsultaInputPort {

    @Autowired
    private ConsultaOutputPort consultaOutputPort;


    public Consulta createConsulta(ConsultaRequestDTO data){

        Consulta newConsulta = new Consulta();
        newConsulta.setDoctorName(data.doctorName());
        newConsulta.setPatientName(data.patientName());
        newConsulta.setPatientNumber(data.patientNumber());
        newConsulta.setSpeciality(data.speciality());
        newConsulta.setDescription(data.description());
        newConsulta.setConsultationDateTime(data.consultationDateTime());

        consultaOutputPort.save(newConsulta);

        return newConsulta;
    }

    public List<ConsultaResponseDTO> getConsultas(){
        List<Consulta> consultas = consultaOutputPort.findAll();
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
        List<Consulta> upcomingConsultas = consultaOutputPort.findUpcomingConsultas(LocalDateTime.now());
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
        Consulta consulta = consultaOutputPort.findById(id)
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
        Consulta consulta = consultaOutputPort.findById(id)
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

        return consultaOutputPort.save(consulta);
    }

    public Consulta canceledConsulta(UUID id) {
        Consulta consulta = consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

        consulta.setStatus(StatusConsulta.CANCELADA);

        return consultaOutputPort.save(consulta);
    }
    public void deleteConsulta(UUID id) {
        Consulta consulta = consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));
        consultaOutputPort.delete(consulta);
    }


}
