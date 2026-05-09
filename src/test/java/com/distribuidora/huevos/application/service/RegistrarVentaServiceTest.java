package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarVentaCommand;
import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.application.mapper.VentaMapper;
import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.exceptions.StockInsuficienteException;
import com.distribuidora.huevos.domain.repositories.*;
import com.distribuidora.huevos.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarVentaServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private CajaRepository cajaRepository;
    @Mock private CreditoRepository creditoRepository;
    @Mock private PrecioPublicoRepository precioPublicoRepository;
    @Mock private VentaMapper ventaMapper;

    @InjectMocks
    private RegistrarVentaService service;

    private Cliente clienteNormal;
    private Cliente clienteEspecial;
    private Inventario inventario;
    private PrecioPublico precioPublico;

    @BeforeEach
    void setUp() {
        clienteNormal = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);

        PrecioEspecial precioEspecial = new PrecioEspecial(
                Precio.de("3.50"), Precio.de("2.80"));
        clienteEspecial = new Cliente(2L, "Bodega Lopez", TipoCliente.ESPECIAL,
                precioEspecial, null);

        inventario = new Inventario(1L, 100, 100);
        precioPublico = new PrecioPublico(1L, Precio.de("4.00"), Precio.de("3.00"));
    }

    @Test
    void ventaClienteNormalUsaPrecioPublico() {
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 2, TipoPago.EFECTIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(10L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("4.00"), TipoPago.EFECTIVO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);

        VentaResponse response = new VentaResponse();
        response.setId(10L);
        response.setPrecioUnitario(new java.math.BigDecimal("4.00"));
        when(ventaMapper.toResponse(any())).thenReturn(response);

        VentaResponse resultado = service.ejecutar(command);

        assertThat(resultado.getPrecioUnitario()).isEqualByComparingTo("4.00");
        verify(inventarioRepository).save(any());
        verify(ventaRepository).save(any());
    }

    @Test
    void ventaClienteEspecialUsaSuPrecioEspecial() {
        RegistrarVentaCommand command = crearCommand(2L, TipoProducto.EXTRA, 2, TipoPago.TRANSFERENCIA);

        when(clienteRepository.findById(2L)).thenReturn(Optional.of(clienteEspecial));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(11L, clienteEspecial, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("3.50"), TipoPago.TRANSFERENCIA, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);

        VentaResponse response = new VentaResponse();
        response.setId(11L);
        response.setPrecioUnitario(new java.math.BigDecimal("3.50"));
        when(ventaMapper.toResponse(any())).thenReturn(response);

        VentaResponse resultado = service.ejecutar(command);

        assertThat(resultado.getPrecioUnitario()).isEqualByComparingTo("3.50");
    }

    @Test
    void ventaConClienteInexistenteLanzaExcepcion() {
        RegistrarVentaCommand command = crearCommand(99L, TipoProducto.EXTRA, 1, TipoPago.EFECTIVO);
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void ventaConStockInsuficienteLanzaExcepcion() {
        Inventario sinStock = new Inventario(1L, 0, 0);
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 5, TipoPago.EFECTIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(inventarioRepository.findUnico()).thenReturn(sinStock);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    void ventaFiadoCreaCreditoParaElCliente() {
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 2, TipoPago.FIADO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.empty());
        when(creditoRepository.save(any())).thenReturn(
                Credito.nuevo(clienteNormal, Dinero.de(new java.math.BigDecimal("8.00"))));

        Venta ventaSaved = new Venta(12L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("4.00"), TipoPago.FIADO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        verify(creditoRepository).save(any());
    }

    private RegistrarVentaCommand crearCommand(Long clienteId, TipoProducto tipo,
                                               int cantidad, TipoPago tipoPago) {
        RegistrarVentaCommand command = new RegistrarVentaCommand();
        command.setClienteId(clienteId);
        command.setTipoProducto(tipo);
        command.setCantidad(cantidad);
        command.setTipoPago(tipoPago);
        return command;
    }
}
