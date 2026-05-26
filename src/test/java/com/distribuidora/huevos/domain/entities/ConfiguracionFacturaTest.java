package com.distribuidora.huevos.domain.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ConfiguracionFacturaTest {

    private ConfiguracionFactura cfg(String prefijo, int consecutivo) {
        return new ConfiguracionFactura(1L,
                "Distribuidora La Golondrina", "900123456-7",
                "Cra 5", "Bogotá", "3001234567",
                "No responsable de IVA", "18764000001",
                null, prefijo, 1, 9999, consecutivo);
    }

    @Test
    void generaNumeroConPrefijoYConsecutivoDeCincoDigitos() {
        ConfiguracionFactura c = cfg("FAC", 1);
        String numero = c.generarYAvanzarConsecutivo();
        assertThat(numero).isEqualTo("FAC00001");
    }

    @Test
    void consecutivoAvanzaDespuesDeGenerar() {
        ConfiguracionFactura c = cfg("FAC", 1);
        c.generarYAvanzarConsecutivo();
        assertThat(c.getConsecutivoActual()).isEqualTo(2);
    }

    @Test
    void dosGeneracionesProducenNumerosCorrelativosDistintos() {
        ConfiguracionFactura c = cfg("FAC", 7);
        String primera  = c.generarYAvanzarConsecutivo();  // FAC00007
        String segunda  = c.generarYAvanzarConsecutivo();  // FAC00008

        assertThat(primera).isEqualTo("FAC00007");
        assertThat(segunda).isEqualTo("FAC00008");
        assertThat(primera).isNotEqualTo(segunda);
    }

    @Test
    void prefijoPersonalizadoSeRefleja() {
        ConfiguracionFactura c = cfg("VTA", 42);
        String numero = c.generarYAvanzarConsecutivo();
        assertThat(numero).isEqualTo("VTA00042");
    }

    @Test
    void consecutivoGrandeMantieneFormatoDecincoDigitos() {
        ConfiguracionFactura c = cfg("FAC", 999);
        String numero = c.generarYAvanzarConsecutivo();
        assertThat(numero).isEqualTo("FAC00999");
    }

    @Test
    void estaConfiguradaConRazonSocialYNit() {
        ConfiguracionFactura c = cfg("FAC", 1);
        assertThat(c.estaConfigurada()).isTrue();
    }

    @Test
    void noEstaConfiguradaSiRazonSocialEsBlanca() {
        ConfiguracionFactura c = new ConfiguracionFactura(1L,
                "  ", "900123456-7",
                "", "", "", "", "", null, "FAC", 1, 9999, 1);
        assertThat(c.estaConfigurada()).isFalse();
    }

    @Test
    void noEstaConfiguradaSiNitEsNulo() {
        ConfiguracionFactura c = new ConfiguracionFactura(1L,
                "Distribuidora", null,
                "", "", "", "", "", null, "FAC", 1, 9999, 1);
        assertThat(c.estaConfigurada()).isFalse();
    }
}
