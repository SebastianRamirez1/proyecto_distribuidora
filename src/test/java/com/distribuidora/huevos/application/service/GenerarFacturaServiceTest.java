package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.GenerarFacturaCommand;
import com.distribuidora.huevos.application.dto.response.FacturaResponse;
import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.*;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.ConfiguracionFacturaRepository;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerarFacturaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private ConfiguracionFacturaRepository configRepo;

    @InjectMocks
    private GenerarFacturaService service;

    private Cliente cliente;
    private Venta ventaConCliente;
    private Venta ventaPublicoGeneral;
    private ConfiguracionFactura config;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Tienda La Esperanza", TipoCliente.NORMAL, null, null);

        ventaConCliente = new Venta(10L, cliente, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("17000"), Precio.cero(),
                TipoPago.EFECTIVO, LocalDateTime.now());

        // Venta al público general: cliente null
        ventaPublicoGeneral = new Venta(20L, null, TipoProducto.AA,
                new Cantidad(1), Precio.de("14500"), Precio.cero(),
                TipoPago.EFECTIVO, LocalDateTime.now());

        config = new ConfiguracionFactura(1L,
                "Distribuidora La Golondrina", "900123456-7",
                "Cra 5 N° 12-34", "Bogotá", "3001234567",
                "No responsable de IVA", "18764000001",
                null, "FAC", 1, 9999, 1);
    }

    // ── nombre en factura ─────────────────────────────────────────────────────

    @Test
    void nombrePersonalizadoDelCommandTienePrioridad() {
        // Aunque la venta tiene un cliente registrado, si el command trae nombre
        // personalizado ese debe usarse en la factura.
        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(configRepo.save(any())).thenReturn(config);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        GenerarFacturaCommand cmd = cmd(10L, "Juan Carlos Gomez", null);
        service.ejecutar(cmd);

        assertThat(captor.getValue().getNombreCliente()).isEqualTo("Juan Carlos Gomez");
    }

    @Test
    void sinNombrePersonalizadoUsaNombreDelCliente() {
        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);
        when(configRepo.save(any())).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        GenerarFacturaCommand cmd = cmd(10L, null, null);
        service.ejecutar(cmd);

        // Sin nombre en el command → usa el nombre del cliente registrado
        assertThat(captor.getValue().getNombreCliente()).isEqualTo("Tienda La Esperanza");
    }

    @Test
    void ventaPublicoGeneralSinNombreUsaConsumidorFinal() {
        // Venta sin cliente (público general) y sin nombre en el command
        // → la factura debe quedar como "Consumidor Final"
        when(facturaRepository.findByVentaId(20L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(20L)).thenReturn(Optional.of(ventaPublicoGeneral));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);
        when(configRepo.save(any())).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(101L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        GenerarFacturaCommand cmd = cmd(20L, null, null);
        service.ejecutar(cmd);

        assertThat(captor.getValue().getNombreCliente()).isEqualTo("Consumidor Final");
        assertThat(captor.getValue().getClienteId()).isNull();
    }

    @Test
    void ventaPublicoGeneralConNombrePersonalizadoLoUsa() {
        when(facturaRepository.findByVentaId(20L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(20L)).thenReturn(Optional.of(ventaPublicoGeneral));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);
        when(configRepo.save(any())).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(101L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        GenerarFacturaCommand cmd = cmd(20L, "Maria Fernandez", null);
        service.ejecutar(cmd);

        assertThat(captor.getValue().getNombreCliente()).isEqualTo("Maria Fernandez");
    }

    // ── consecutivo ───────────────────────────────────────────────────────────

    @Test
    void generarFacturaAvanzaElConsecutivo() {
        // El consecutivo empieza en 1; después de generar debe quedar en 2
        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);

        ArgumentCaptor<ConfiguracionFactura> cfgCaptor =
                ArgumentCaptor.forClass(ConfiguracionFactura.class);
        when(configRepo.save(cfgCaptor.capture())).thenReturn(config);
        when(facturaRepository.save(any())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        service.ejecutar(cmd(10L, null, null));

        // El consecutivo fue avanzado antes de guardarse → debe ser 2
        assertThat(cfgCaptor.getValue().getConsecutivoActual()).isEqualTo(2);
    }

    @Test
    void numeroDeFacturaSigueFormatoPrefijoMasConsecutivoCincoDigitos() {
        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);
        when(configRepo.save(any())).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        FacturaResponse result = service.ejecutar(cmd(10L, null, null));

        // config tiene prefijo "FAC" y consecutivo 1 → "FAC00001"
        assertThat(result.getNumero()).isEqualTo("FAC00001");
    }

    // ── NIT ───────────────────────────────────────────────────────────────────

    @Test
    void nitVacioEnCommandGuardaSinNit() {
        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);
        when(configRepo.save(any())).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        service.ejecutar(cmd(10L, null, null)); // nit null

        assertThat(captor.getValue().getNitCliente()).isEqualTo("Sin NIT");
    }

    @Test
    void nitProvistoPorCommandSeGuardaEnLaFactura() {
        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));
        when(configRepo.findUnicaParaActualizar()).thenReturn(config);
        when(configRepo.save(any())).thenReturn(config);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(captor.capture())).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return new Factura(100L, f.getNumero(), f.getVentaId(), f.getClienteId(),
                    f.getFechaEmision(), f.getTipo(), f.getEstado(),
                    f.getNombreCliente(), f.getNitCliente(),
                    f.getTipoProducto(), f.getCantidad(),
                    f.getPrecioUnitario(), f.getTotal(), f.getTipoPago());
        });

        GenerarFacturaCommand cmd = cmd(10L, null, "900999888-1");
        service.ejecutar(cmd);

        assertThat(captor.getValue().getNitCliente()).isEqualTo("900999888-1");
    }

    // ── error paths ───────────────────────────────────────────────────────────

    @Test
    void ventaYaTieneFacturaLanzaIllegalState() {
        Factura facturaExistente = new Factura(99L, "FAC00001", 10L, 1L,
                LocalDateTime.now(), TipoFactura.MANUAL, EstadoFactura.EMITIDA,
                "Cliente", "Sin NIT", TipoProducto.EXTRA, 2,
                new BigDecimal("17000"), new BigDecimal("34000"), TipoPago.EFECTIVO);

        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.of(facturaExistente));

        assertThatThrownBy(() -> service.ejecutar(cmd(10L, null, null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("FAC00001");
    }

    @Test
    void ventaNoExisteLanzaRecursoNoEncontrado() {
        when(facturaRepository.findByVentaId(999L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(cmd(999L, null, null)))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    void ventaAnuladaNoSePuedeFacturar() {
        ventaConCliente.anular();

        when(facturaRepository.findByVentaId(10L)).thenReturn(Optional.empty());
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaConCliente));

        assertThatThrownBy(() -> service.ejecutar(cmd(10L, null, null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("anulada");
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private GenerarFacturaCommand cmd(Long ventaId, String nombre, String nit) {
        GenerarFacturaCommand c = new GenerarFacturaCommand();
        c.setVentaId(ventaId);
        c.setNombreCliente(nombre);
        c.setNitCliente(nit);
        c.setTipo(TipoFactura.MANUAL);
        return c;
    }
}
