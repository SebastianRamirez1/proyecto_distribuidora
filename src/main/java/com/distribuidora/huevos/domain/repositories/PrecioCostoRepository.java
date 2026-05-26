package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.valueobjects.PrecioCosto;

public interface PrecioCostoRepository {
    PrecioCosto findCurrent();
    PrecioCosto save(PrecioCosto precioCosto);
}
