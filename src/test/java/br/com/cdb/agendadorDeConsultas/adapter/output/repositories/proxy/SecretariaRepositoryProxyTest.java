package br.com.cdb.agendadorDeConsultas.adapter.output.repositories.proxy;

import br.com.cdb.agendadorDeConsultas.adapter.output.repositories.SecretariaRepository;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Secretaria;
import br.com.cdb.agendadorDeConsultas.port.output.SecretariaOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class SecretariaRepositoryProxyTest {

    @Autowired
    private SecretariaOutputPort proxy;

    @MockitoBean
    private SecretariaRepository realRepository;

    @BeforeEach
    void setUp() {
        if (proxy instanceof SecretariaRepositoryProxy) {
            ((SecretariaRepositoryProxy) proxy).clearCache();
        }
    }

    @Test
    void devePopularTodosOsCachesAposPrimeiraBuscaEUsarCacheNasBuscasSeguintes() {
        Secretaria secretaria = new Secretaria(UUID.randomUUID(), "Ana", "111.111.111-11", "ana@email.com", "senha");
        when(realRepository.findByEmail(secretaria.getEmail())).thenReturn(Optional.of(secretaria));

        proxy.findByEmail(secretaria.getEmail());
        proxy.findById(secretaria.getId());
        proxy.findByCpf(secretaria.getCpf());

        verify(realRepository, times(1)).findByEmail(secretaria.getEmail());
        verify(realRepository, never()).findById(secretaria.getId());
        verify(realRepository, never()).findByCpf(secretaria.getCpf());
    }

    @Test
    void deveInvalidarTodosOsCachesAposSalvar() {
        Secretaria secretaria = new Secretaria(UUID.randomUUID(), "Ana", "111.111.111-11", "ana@email.com", "senha");
        when(realRepository.findById(secretaria.getId())).thenReturn(secretaria);

        proxy.findById(secretaria.getId());
        proxy.save(secretaria);
        proxy.findById(secretaria.getId());

        verify(realRepository, times(2)).findById(secretaria.getId());
    }

    @Test
    void deveLancarExcecaoQuandoRepositorioRealLancaExcecao() {
        UUID secretariaId = UUID.randomUUID();
        when(realRepository.findById(secretariaId)).thenThrow(new RuntimeException("Recurso não encontrado"));

        assertThrows(RuntimeException.class, () -> {
            proxy.findById(secretariaId);
        });

        verify(realRepository, times(1)).findById(secretariaId);
    }
}