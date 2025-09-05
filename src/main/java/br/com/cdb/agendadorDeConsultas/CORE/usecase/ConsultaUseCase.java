package br.com.cdb.agendadorDeConsultas.core.usecase;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaUpdate;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.core.domain.model.StatusConsulta;
import br.com.cdb.agendadorDeConsultas.port.input.ConsultaInputPort;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class ConsultaUseCase implements ConsultaInputPort {


    private final ConsultaOutputPort consultaOutputPort;

    public ConsultaUseCase(ConsultaOutputPort consultaOutputPort) {
        this.consultaOutputPort = consultaOutputPort;
    }

    public Consulta createConsulta(Consulta consulta){
        return consultaOutputPort.save(consulta);
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

    public Consulta getConsultaDetails( UUID id) {
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
