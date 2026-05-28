package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.AjustarInventarioCommand;
import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException;
import com.distribuidora.huevos.domain.repositories.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AjustarInventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private AjustarInventarioService service;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario(1L, 20, 15, 30, 10);
    }

    @Test
    void ajustarEstableceValoresExactos() {
        AjustarInventarioCommand command = new AjustarInventarioCommand();
        command.setStockExtra(50.0);
        command.setStockAA(40.0);
        command.setStockA(60);
        command.setStockB(25);

        when(inventarioRepository.findUnico()).thenReturn(inventario);
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        when(inventarioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(command);

        assertThat(captor.getValue().getStockExtra()).isEqualTo(50.0);
        assertThat(captor.getValue().getStockAA()).isEqualTo(40.0);
        assertThat(captor.getValue().getStockA()).isEqualTo(60);
        assertThat(captor.getValue().getStockB()).isEqualTo(25);
    }

    @Test
    void ajustarConMediaCanastaFunciona() {
        // El dueño hace conteo físico: EXTRA=7.5 (una canasta abierta), AA=0.5
        AjustarInventarioCommand command = new AjustarInventarioCommand();
        command.setStockExtra(7.5);
        command.setStockAA(0.5);
        command.setStockA(10);
        command.setStockB(5);

        when(inventarioRepository.findUnico()).thenReturn(inventario);
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        when(inventarioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(command);

        assertThat(captor.getValue().getStockExtra()).isEqualTo(7.5);
        assertThat(captor.getValue().getStockAA()).isEqualTo(0.5);
    }

    @Test
    void ajustarConNegativoLanzaExcepcion() {
        when(inventarioRepository.findUnico()).thenReturn(inventario);

        AjustarInventarioCommand command = new AjustarInventarioCommand();
        command.setStockExtra(-1.0); // negativo
        command.setStockAA(0.0);
        command.setStockA(0);
        command.setStockB(0);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(OperacionNoPermitidaException.class)
                .hasMessageContaining("negativo");
    }

    @Test
    void ajustarAceroResetaElStock() {
        AjustarInventarioCommand command = new AjustarInventarioCommand();
        command.setStockExtra(0.0);
        command.setStockAA(0.0);
        command.setStockA(0);
        command.setStockB(0);

        when(inventarioRepository.findUnico()).thenReturn(inventario);
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        when(inventarioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(command);

        assertThat(captor.getValue().getStockExtra()).isEqualTo(0.0);
        assertThat(captor.getValue().getStockAA()).isEqualTo(0.0);
        assertThat(captor.getValue().getStockA()).isEqualTo(0);
        assertThat(captor.getValue().getStockB()).isEqualTo(0);
    }

    @Test
    void respuestaReflejaElStockGuardado() {
        AjustarInventarioCommand command = new AjustarInventarioCommand();
        command.setStockExtra(5.5);
        command.setStockAA(3.0);
        command.setStockA(12);
        command.setStockB(8);

        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InventarioResponse response = service.ejecutar(command);

        assertThat(response.getStockExtra()).isEqualTo(5.5);
        assertThat(response.getStockAA()).isEqualTo(3.0);
        assertThat(response.getStockA()).isEqualTo(12);
        assertThat(response.getStockB()).isEqualTo(8);
    }
}
