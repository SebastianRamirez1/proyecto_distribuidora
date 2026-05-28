package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PrecioPublicoTest {

    /** Tabla de precios de referencia usada en todos los tests. */
    private final PrecioPublico precios = new PrecioPublico(
            1L,
            Precio.de("4.00"),  // EXTRA
            Precio.de("3.60"),  // AA
            Precio.de("3.00"),  // A
            Precio.de("2.50")   // B
    );

    // ── tipos normales ────────────────────────────────────────────────────────

    @Test
    void precioExtraDevuelvePrecioCompleto() {
        assertThat(precios.obtenerPrecio(TipoProducto.EXTRA).getValor())
                .isEqualByComparingTo("4.00");
    }

    @Test
    void precioAADevuelvePrecioCompleto() {
        assertThat(precios.obtenerPrecio(TipoProducto.AA).getValor())
                .isEqualByComparingTo("3.60");
    }

    @Test
    void precioADevuelvePrecioCompleto() {
        assertThat(precios.obtenerPrecio(TipoProducto.A).getValor())
                .isEqualByComparingTo("3.00");
    }

    @Test
    void precioBDevuelvePrecioCompleto() {
        assertThat(precios.obtenerPrecio(TipoProducto.B).getValor())
                .isEqualByComparingTo("2.50");
    }

    // ── media canasta ─────────────────────────────────────────────────────────

    @Test
    void precioMediaExtraEsLaMitadDelPrecioExtra() {
        // 4.00 / 2 = 2.00
        assertThat(precios.obtenerPrecio(TipoProducto.EXTRA_MEDIA).getValor())
                .isEqualByComparingTo("2.00");
    }

    @Test
    void precioMediaAAEsLaMitadDelPrecioAA() {
        // 3.60 / 2 = 1.80
        assertThat(precios.obtenerPrecio(TipoProducto.AA_MEDIA).getValor())
                .isEqualByComparingTo("1.80");
    }

    @Test
    void dosMediasEquivaleEnPrecioAUnaEntera() {
        // El precio de 2 medias debe ser igual al precio de 1 entera
        BigDecimal precioUnaEntera = precios.obtenerPrecio(TipoProducto.EXTRA).getValor();
        BigDecimal precioUnaMedia  = precios.obtenerPrecio(TipoProducto.EXTRA_MEDIA).getValor();

        assertThat(precioUnaMedia.multiply(BigDecimal.valueOf(2)))
                .isEqualByComparingTo(precioUnaEntera);
    }

    @Test
    void precioMediaExtraNoDependeDeMediaAA() {
        assertThat(precios.obtenerPrecio(TipoProducto.EXTRA_MEDIA).getValor())
                .isNotEqualByComparingTo(
                        precios.obtenerPrecio(TipoProducto.AA_MEDIA).getValor());
    }
}
