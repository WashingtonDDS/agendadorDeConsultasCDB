package br.com.cdb.agendadorDeConsultas.core.usecase;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaRequest;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.port.input.ConsultaInputPort;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class ConsultaUseCase implements ConsultaInputPort {


    private ConsultaOutputPort consultaOutputPort;


    public Consulta createConsulta(ConsultaRequest data){

        Consulta newConsulta = new Consulta();
        newConsulta.setDoctorName(data.doctorName());
        newConsulta.setPatientName(data.patientName());
        newConsulta.setPatientNumber(data.patientNumber());
        newConsulta.setSpeciality(data.speciality());
        newConsulta.setDescription(data.description());
        newConsulta.setConsultationDateTime(data.consultationDateTime());
        newConsulta.setStatus(StatusConsulta.AGENDADA);

        consultaOutputPort.save(newConsulta);

        return newConsulta;
    }

    public List<Consulta> getConsultas(){
        return consultaOutputPort.findAll();

    }

    public List<Consulta> getUpcomingConsultas() {
        List<Consulta> upcomingConsultas = consultaOutputPort.findUpcomingConsultas(LocalDateTime.now());
        return upcomingConsultas.stream()
                .filter(consulta -> consulta.getStatus() != StatusConsulta.CANCELADA)
                .toList();
    }

    public Consulta getConsultaDetails(@RequestParam("id") UUID id) {
        return consultaOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta not found"));

    }

    public Consulta updateConsulta( UUID id, ConsultaUpdate request) {
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
