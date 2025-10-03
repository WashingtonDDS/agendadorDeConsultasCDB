package br.com.cdb.agendadorDeConsultas.adapter.output.repositories.proxy;

import br.com.cdb.agendadorDeConsultas.adapter.output.repositories.ConsultaRepository;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import br.com.cdb.agendadorDeConsultas.port.output.ConsultaOutputPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class ConsultaRepositoryProxyTest {

    @Autowired
    private ConsultaOutputPort proxy;

    @MockitoBean
    private ConsultaRepository realRepository;

    @Test
    void deveBuscarNoRepositorioRealNaPrimeiraVezERetornarDoCacheNaSegunda() {
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = new Consulta();
        consulta.setId(consultaId);

        when(realRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

        Optional<Consulta> primeiraChamada = proxy.findById(consultaId);
        Optional<Consulta> segundaChamada = proxy.findById(consultaId);

        assertEquals(consulta, primeiraChamada.get());
        assertEquals(consulta, segundaChamada.get());
        verify(realRepository, times(1)).findById(consultaId);
    }

    @Test
    void deveInvalidarOCacheAposSalvar() {
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = new Consulta();
        consulta.setId(consultaId);

        when(realRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

        proxy.findById(consultaId);
        proxy.save(consulta);
        proxy.findById(consultaId);

        verify(realRepository, times(2)).findById(consultaId);
    }

    @Test
    void deveInvalidarOCacheAposDeletar() {
        UUID consultaId = UUID.randomUUID();
        Consulta consulta = new Consulta();
        consulta.setId(consultaId);

        when(realRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

        proxy.findById(consultaId);
        proxy.delete(consulta);
        proxy.findById(consultaId);

        verify(realRepository, times(2)).findById(consultaId);
    }

    @Test
    void devePropagarExcecaoDoRepositorioReal() {
        UUID consultaId = UUID.randomUUID();
        when(realRepository.findById(consultaId)).thenThrow(new RuntimeException("Erro de banco de dados"));

        assertThrows(RuntimeException.class, () -> {
            proxy.findById(consultaId);
        });

        verify(realRepository, times(1)).findById(consultaId);
    }
}