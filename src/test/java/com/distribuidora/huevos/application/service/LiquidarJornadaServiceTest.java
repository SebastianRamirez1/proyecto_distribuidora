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
class LiquidarJornadaServiceTest {

    @Mock private JornadaRepository jornadaRepository;

    @InjectMocks
    private LiquidarJornadaService service;

    private static final LocalDate HOY = LocalDate.of(2026, 1, 15);

    @Test
    void liquidarPasaJornadaActivaAEnCierreYAbreNueva() {
        Jornada jornadaActiva = new Jornada(1L, HOY, EstadoJornada.ABIERTA, LocalDateTime.now(), null);

        when(jornadaRepository.findEnCierre()).thenReturn(Optional.empty());
        when(jornadaRepository.findActiva()).thenReturn(Optional.of(jornadaActiva));

        // Capturar las dos llamadas a save
        ArgumentCaptor<Jornada> captor = ArgumentCaptor.forClass(Jornada.class);
        when(jornadaRepository.save(captor.capture())).thenAnswer(inv -> {
            Jornada j = inv.getArgument(0);
            // Simular ID generado para la nueva jornada
            return j.getId() == null
                    ? new Jornada(2L, j.getFecha(), j.getEstado(), j.getAbiertaEn(), j.getCerradaEn())
                    : j;
        });

        JornadaResponse result = service.ejecutar();

        // La jornada devuelta es la nueva (ABIERTA, día siguiente)
        assertThat(result.fecha()).isEqualTo(HOY.plusDays(1));
        assertThat(result.estado()).isEqualTo(EstadoJornada.ABIERTA);

        // Se hicieron dos saves: uno para EN_CIERRE, otro para la nueva ABIERTA
        verify(jornadaRepository, times(2)).save(any());
        // La primera guardada debe ser la antigua en estado EN_CIERRE
        Jornada primerGuardado = captor.getAllValues().get(0);
        assertThat(primerGuardado.getEstado()).isEqualTo(EstadoJornada.EN_CIERRE);
        // La segunda es la nueva jornada con fecha siguiente
        Jornada segundoGuardado = captor.getAllValues().get(1);
        assertThat(segundoGuardado.getFecha()).isEqualTo(HOY.plusDays(1));
    }

    @Test
    void liquidarFallasiYaHayUnaEnCierre() {
        Jornada enCierre = new Jornada(1L, HOY.minusDays(1), EstadoJornada.EN_CIERRE, LocalDateTime.now(), null);

        when(jornadaRepository.findEnCierre()).thenReturn(Optional.of(enCierre));

        assertThatThrownBy(() -> service.ejecutar())
                .isInstanceOf(OperacionNoPermitidaException.class)
                .hasMessageContaining("en cierre");
    }

    @Test
    void liquidarFallaSiNoHayJornadaActiva() {
        when(jornadaRepository.findEnCierre()).thenReturn(Optional.empty());
        when(jornadaRepository.findActiva()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar())
                .isInstanceOf(OperacionNoPermitidaException.class)
                .hasMessageContaining("abierta");
    }
}
