package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.exceptions.VentaOperacionException;
import com.distribuidora.huevos.domain.repositories.*;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import org.junit.jupiter.api.BeforeEach;
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
class AnularVentaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private CajaRepository cajaRepository;
    @Mock private CreditoRepository creditoRepository;

    @InjectMocks
    private AnularVentaService service;

    private Cliente cliente;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        cliente    = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);
        inventario = new Inventario(1L, 10, 10, 10, 10);
    }

    // ── flujo efectivo ────────────────────────────────────────────────────────

    @Test
    void anularVentaEfectivoMarcaComoAnuladaYRestauraStock() {
        Venta venta = ventaEfectivo(1L, TipoProducto.EXTRA, 3, "4.00");

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        Caja caja = Caja.nueva(LocalDate.now());
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(venta.calcularTotal().getValor()));
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.of(caja));

        service.ejecutar(1L);

        verify(ventaRepository).save(any(Venta.class));
        verify(inventarioRepository).save(any(Inventario.class));
        verify(cajaRepository).save(any(Caja.class));
    }

    @Test
    void anularVentaEfectivoRestauraStockCorrecto() {
        // 3 canastas EXTRA vendidas → al anular deben volver al inventario
        Venta venta = ventaEfectivo(1L, TipoProducto.EXTRA, 3, "4.00");

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());

        ArgumentCaptor<Inventario> invCaptor = ArgumentCaptor.forClass(Inventario.class);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(invCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(1L);

        // inventario tenía 10 EXTRA; al revertir la venta de 3 debe quedar 13
        assertThat(invCaptor.getValue().getStockExtra()).isEqualTo(13);
    }

    @Test
    void anularVentaEfectivoRevierteMovimientoDeCaja() {
        Venta venta = ventaEfectivo(1L, TipoProducto.EXTRA, 2, "4.00"); // total S/8.00

        Caja caja = Caja.nueva(LocalDate.now());
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new java.math.BigDecimal("50.00")));

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.of(caja));

        ArgumentCaptor<Caja> cajaCaptor = ArgumentCaptor.forClass(Caja.class);
        when(cajaRepository.save(cajaCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(1L);

        // Caja tenía S/50.00; al revertir S/8.00 debe quedar S/42.00
        assertThat(cajaCaptor.getValue().getTotalEfectivo().getValor())
                .isEqualByComparingTo("42.00");
    }

    // ── flujo fiado ───────────────────────────────────────────────────────────

    @Test
    void anularVentaFiadoRevierteDeudaDelCredito() {
        Venta venta = ventaFiado(2L, TipoProducto.EXTRA, 2, "4.00"); // total S/8.00
        Credito credito = Credito.nuevo(cliente, Dinero.de(new java.math.BigDecimal("8.00")));

        when(ventaRepository.findById(2L)).thenReturn(Optional.of(venta));
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(credito));

        ArgumentCaptor<Credito> creditoCaptor = ArgumentCaptor.forClass(Credito.class);
        when(creditoRepository.save(creditoCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(2L);

        // La deuda de S/8.00 fue revertida → montoTotal queda en 0
        assertThat(creditoCaptor.getValue().getMontoTotal().getValor())
                .isEqualByComparingTo("0.00");
    }

    @Test
    void anularVentaEfectivoNoTocaCredito() {
        // Una venta en efectivo no debe modificar créditos
        Venta venta = ventaEfectivo(3L, TipoProducto.AA, 1, "3.60");

        when(ventaRepository.findById(3L)).thenReturn(Optional.of(venta));
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());

        service.ejecutar(3L);

        verify(creditoRepository, never()).findByClienteId(any());
        verify(creditoRepository, never()).save(any());
    }

    // ── error paths ───────────────────────────────────────────────────────────

    @Test
    void ventaNoEncontradaLanzaExcepcion() {
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(999L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    void anularVentaYaAnuladaLanzaExcepcion() {
        Venta venta = ventaEfectivo(4L, TipoProducto.EXTRA, 1, "4.00");
        venta.anular(); // ya estaba anulada

        when(ventaRepository.findById(4L)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> service.ejecutar(4L))
                .isInstanceOf(VentaOperacionException.class)
                .hasMessageContaining("ya fue anulada");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Venta ventaEfectivo(Long id, TipoProducto tipo, int cantidad, String precio) {
        return new Venta(id, cliente, tipo, new Cantidad(cantidad),
                Precio.de(precio), TipoPago.EFECTIVO, LocalDateTime.now());
    }

    private Venta ventaFiado(Long id, TipoProducto tipo, int cantidad, String precio) {
        return new Venta(id, cliente, tipo, new Cantidad(cantidad),
                Precio.de(precio), TipoPago.FIADO, LocalDateTime.now());
    }
}
