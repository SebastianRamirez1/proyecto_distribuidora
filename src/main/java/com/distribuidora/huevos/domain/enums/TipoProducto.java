package com.distribuidora.huevos.domain.enums;

public enum TipoProducto {
    EXTRA,
    AA,
    A,
    B,
    /** Media canasta de tipo EXTRA: precio y costo = EXTRA ÷ 2. Descuenta del stock de EXTRA. */
    EXTRA_MEDIA,
    /** Media canasta de tipo AA: precio y costo = AA ÷ 2. Descuenta del stock de AA. */
    AA_MEDIA
}
