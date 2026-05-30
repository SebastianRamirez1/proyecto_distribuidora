package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarVentaCommand;
import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.application.mapper.VentaMapper;
import com.distribuidora.huevos.domain.entities.Jornada;
import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.PrecioInvalidoException;
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

import java.time.LocalDate;
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
    @Mock private PrecioCostoRepository precioCostoRepository;
    @Mock private JornadaRepository jornadaRepository;
    @Mock private VentaMapper ventaMapper;

    @InjectMocks
    private RegistrarVentaService service;

    private Cliente clienteNormal;
    private Cliente clienteEspecial;
    private Inventario inventario;
    private PrecioPublico precioPublico;
    private PrecioCosto precioCosto;

    private static final LocalDate FECHA_JORNADA = LocalDate.of(2026, 1, 15);

    @BeforeEach
    void setUp() {
        clienteNormal = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);

        PrecioEspecial precioEspecial = new PrecioEspecial(
                Precio.de("3.50"), Precio.de("3.20"), Precio.de("2.80"), Precio.de("2.40"));
        clienteEspecial = new Cliente(2L, "Bodega Lopez", TipoCliente.ESPECIAL,
                precioEspecial, null);

        inventario  = new Inventario(1L, 100, 100, 100, 100);
        precioPublico = new PrecioPublico(1L,
                Precio.de("4.00"), Precio.de("3.60"), Precio.de("3.00"), Precio.de("2.50"));
        precioCosto = new PrecioCosto(1L,
                Precio.cero(), Precio.cero(), Precio.cero(), Precio.cero());

        // La jornada activa por defecto para todos los tests que no pasan jornadaId
        Jornada jornadaActiva = new Jornada(1L, FECHA_JORNADA, com.distribuidora.huevos.domain.enums.EstadoJornada.ABIERTA,
                LocalDateTime.now(), null);
        lenient().when(jornadaRepository.findActiva()).thenReturn(Optional.of(jornadaActiva));
    }

    @Test
    void ventaClienteNormalUsaPrecioPublico() {
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 2, TipoPago.EFECTIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(10L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("4.00"), Precio.cero(), TipoPago.EFECTIVO, LocalDateTime.now());
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
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(11L, clienteEspecial, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("3.50"), Precio.cero(), TipoPago.TRANSFERENCIA, LocalDateTime.now());
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
        Inventario sinStock = new Inventario(1L, 0, 0, 0, 0);
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 5, TipoPago.EFECTIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(sinStock);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    void ventaFiadoCreaCreditoParaElCliente() {
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 2, TipoPago.FIADO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.empty());
        when(creditoRepository.save(any())).thenReturn(
                Credito.nuevo(clienteNormal, Dinero.de(new java.math.BigDecimal("8.00"))));

        Venta ventaSaved = new Venta(12L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("4.00"), Precio.cero(), TipoPago.FIADO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        verify(creditoRepository).save(any());
    }

    // ── público general (clienteId null) ─────────────────────────────────────

    @Test
    void ventaPublicoGeneralUsaPrecioPublico() {
        // clienteId null → no se busca cliente en repo, se usa precioPublico directamente
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.EXTRA, 3, TipoPago.EFECTIVO);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(13L, null, TipoProducto.EXTRA,
                new Cantidad(3), Precio.de("4.00"), Precio.cero(),
                TipoPago.EFECTIVO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);

        VentaResponse response = new VentaResponse();
        response.setId(13L);
        response.setPrecioUnitario(new java.math.BigDecimal("4.00"));
        when(ventaMapper.toResponse(any())).thenReturn(response);

        VentaResponse resultado = service.ejecutar(command);

        // Nunca debió consultarse el repositorio de clientes
        verify(clienteRepository, never()).findById(any());
        assertThat(resultado.getPrecioUnitario()).isEqualByComparingTo("4.00");
    }

    @Test
    void ventaPublicoGeneralConFiadoLanzaOperacionNoPermitida() {
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.EXTRA, 1, TipoPago.FIADO);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException.class)
                .hasMessageContaining("fiado");
    }

    // ── precio manual (rebaja puntual) ────────────────────────────────────────

    @Test
    void ventaConPrecioManualIgnoraCalculoDeCliente() {
        // precioManual = 3.00, pero el precio público de EXTRA es 4.00
        // y el cliente es NORMAL → sin rebaja debería pagar 4.00; con rebaja paga 3.00.
        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 2, TipoPago.EFECTIVO);
        command.setPrecioManual(new java.math.BigDecimal("3.00"));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(14L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("3.00"), Precio.cero(),
                TipoPago.EFECTIVO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);

        VentaResponse response = new VentaResponse();
        response.setId(14L);
        response.setPrecioUnitario(new java.math.BigDecimal("3.00"));
        when(ventaMapper.toResponse(any())).thenReturn(response);

        VentaResponse resultado = service.ejecutar(command);

        assertThat(resultado.getPrecioUnitario()).isEqualByComparingTo("3.00");
    }

    @Test
    void ventaConPrecioPublicoCeroLanzaExcepcion() {
        // precio_publico recién inicializado — todos los tipos en S/ 0.00
        PrecioPublico preciosCero = new PrecioPublico(1L,
                Precio.cero(), Precio.cero(), Precio.cero(), Precio.cero());

        RegistrarVentaCommand command = crearCommand(1L, TipoProducto.EXTRA, 2, TipoPago.EFECTIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNormal));
        when(precioPublicoRepository.findCurrent()).thenReturn(preciosCero);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(PrecioInvalidoException.class)
                .hasMessageContaining("S/ 0.00")
                .hasMessageContaining("EXTRA");
    }

    // ── media canasta ─────────────────────────────────────────────────────────

    @Test
    void ventaMediaExtraDescuenta0punto5PorUnidadDelStock() {
        // 2 unidades de EXTRA_MEDIA → descuenta 1.0 del stockExtra (no 2)
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.EXTRA_MEDIA, 2, TipoPago.EFECTIVO);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario); // stockExtra = 100

        org.mockito.ArgumentCaptor<Inventario> invCaptor =
                org.mockito.ArgumentCaptor.forClass(Inventario.class);
        when(inventarioRepository.save(invCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(20L, null, TipoProducto.EXTRA_MEDIA,
                new Cantidad(2), Precio.de("2.00"), Precio.cero(),
                TipoPago.EFECTIVO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        // 100 - (2 × 0.5) = 99.0  — si fuera 1 entera por unidad sería 98.0 (bug)
        assertThat(invCaptor.getValue().getStockExtra()).isEqualTo(99.0);
    }

    @Test
    void ventaMediaExtraUsaPrecioMitadDelPrecioExtra() {
        // precioPublico EXTRA = 4.00 → EXTRA_MEDIA debe quedar registrada con 2.00
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.EXTRA_MEDIA, 1, TipoPago.EFECTIVO);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        org.mockito.ArgumentCaptor<Venta> ventaCaptor =
                org.mockito.ArgumentCaptor.forClass(Venta.class);
        when(ventaRepository.save(ventaCaptor.capture())).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            return new Venta(21L, null, v.getTipoProducto(),
                    v.getCantidad(), v.getPrecioUnitario(), v.getCostoUnitario(),
                    v.getTipoPago(), v.getFecha());
        });
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        assertThat(ventaCaptor.getValue().getPrecioUnitario().getValor())
                .isEqualByComparingTo("2.00");
    }

    @Test
    void ventaMediaAADescuenta0punto5PorUnidadDelStock() {
        // 4 unidades AA_MEDIA → descuenta 2.0 del stockAA
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.AA_MEDIA, 4, TipoPago.EFECTIVO);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario); // stockAA = 100

        org.mockito.ArgumentCaptor<Inventario> invCaptor =
                org.mockito.ArgumentCaptor.forClass(Inventario.class);
        when(inventarioRepository.save(invCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        Venta ventaSaved = new Venta(22L, null, TipoProducto.AA_MEDIA,
                new Cantidad(4), Precio.de("1.80"), Precio.cero(),
                TipoPago.EFECTIVO, LocalDateTime.now());
        when(ventaRepository.save(any())).thenReturn(ventaSaved);
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        // 100 - (4 × 0.5) = 98.0
        assertThat(invCaptor.getValue().getStockAA()).isEqualTo(98.0);
    }

    @Test
    void ventaMediaAAUsaPrecioMitadDelPrecioAA() {
        // precioPublico AA = 3.60 → AA_MEDIA debe quedar registrada con 1.80
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.AA_MEDIA, 1, TipoPago.EFECTIVO);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(java.time.LocalDate.now()));

        org.mockito.ArgumentCaptor<Venta> ventaCaptor =
                org.mockito.ArgumentCaptor.forClass(Venta.class);
        when(ventaRepository.save(ventaCaptor.capture())).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            return new Venta(23L, null, v.getTipoProducto(),
                    v.getCantidad(), v.getPrecioUnitario(), v.getCostoUnitario(),
                    v.getTipoPago(), v.getFecha());
        });
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        assertThat(ventaCaptor.getValue().getPrecioUnitario().getValor())
                .isEqualByComparingTo("1.80");
    }

    // ── jornada específica (hoja anterior) ───────────────────────────────────

    @Test
    void ventaConJornadaIdUsaFechaDeEsaJornada() {
        // Venta asignada a una jornada anterior (jornadaId = 99, fecha = 2026-01-10)
        LocalDate fechaAnterior = LocalDate.of(2026, 1, 10);
        Jornada jornadaAnterior = new Jornada(99L, fechaAnterior,
                com.distribuidora.huevos.domain.enums.EstadoJornada.EN_CIERRE,
                LocalDateTime.now().minusDays(1), null);

        RegistrarVentaCommand command = crearCommand(null, TipoProducto.EXTRA, 1, TipoPago.EFECTIVO);
        command.setJornadaId(99L);

        when(jornadaRepository.findById(99L)).thenReturn(Optional.of(jornadaAnterior));
        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(cajaRepository.findByFecha(fechaAnterior)).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenReturn(Caja.nueva(fechaAnterior));

        org.mockito.ArgumentCaptor<Venta> ventaCaptor =
                org.mockito.ArgumentCaptor.forClass(Venta.class);
        when(ventaRepository.save(ventaCaptor.capture())).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            return new Venta(30L, null, v.getTipoProducto(),
                    v.getCantidad(), v.getPrecioUnitario(), v.getCostoUnitario(),
                    v.getTipoPago(), v.getFecha());
        });
        when(ventaMapper.toResponse(any())).thenReturn(new VentaResponse());

        service.ejecutar(command);

        // La venta debe quedar en la fecha de la jornada anterior, no en la activa
        assertThat(ventaCaptor.getValue().getFecha().toLocalDate()).isEqualTo(fechaAnterior);
        // La caja consultada debe ser la de la fecha anterior
        verify(cajaRepository).findByFecha(fechaAnterior);
        // No debe haber consultado la jornada activa
        verify(jornadaRepository, never()).findActiva();
    }

    @Test
    void ventaConJornadaIdInvalidoLanzaExcepcion() {
        RegistrarVentaCommand command = crearCommand(null, TipoProducto.EXTRA, 1, TipoPago.EFECTIVO);
        command.setJornadaId(999L);

        when(precioPublicoRepository.findCurrent()).thenReturn(precioPublico);
        when(precioCostoRepository.findCurrent()).thenReturn(precioCosto);
        when(inventarioRepository.findUnico()).thenReturn(inventario);
        when(inventarioRepository.save(any())).thenReturn(inventario);
        when(jornadaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
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
