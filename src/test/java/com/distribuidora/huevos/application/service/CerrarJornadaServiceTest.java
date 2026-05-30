package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.JornadaResponse;
import com.distribuidora.huevos.domain.entities.Jornada;
import com.distribuidora.huevos.domain.enums.EstadoJornada;
import com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CerrarJornadaServiceTest {

    @Mock private JornadaRepository jornadaRepository;

    @InjectMocks
    private CerrarJornadaService service;

    private static final LocalDate HOY = LocalDate.of(2026, 1, 14);

    @Test
    void cerrarPasaJornadaEnCierreACerrada() {
        Jornada enCierre = new Jornada(1L, HOY, EstadoJornada.EN_CIERRE, LocalDateTime.now().minusDays(1), null);

        when(jornadaRepository.findEnCierre()).thenReturn(Optional.of(enCierre));

        ArgumentCaptor<Jornada> captor = ArgumentCaptor.forClass(Jornada.class);
        when(jornadaRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar();

        Jornada guardada = captor.getValue();
        assertThat(guardada.getEstado()).isEqualTo(EstadoJornada.CERRADA);
        assertThat(guardada.getCerradaEn()).isNotNull();
    }

    @Test
    void cerrarRetornaResponseConEstadoCerrada() {
        Jornada enCierre = new Jornada(1L, HOY, EstadoJornada.EN_CIERRE, LocalDateTime.now().minusDays(1), null);

        when(jornadaRepository.findEnCierre()).thenReturn(Optional.of(enCierre));
        when(jornadaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JornadaResponse result = service.ejecutar();

        assertThat(result.estado()).isEqualTo(EstadoJornada.CERRADA);
        assertThat(result.fecha()).isEqualTo(HOY);
    }

    @Test
    void cerrarFallaSiNoHayJornadaEnCierre() {
        when(jornadaRepository.findEnCierre()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar())
                .isInstanceOf(OperacionNoPermitidaException.class)
                .hasMessageContaining("cierre");
    }
}
