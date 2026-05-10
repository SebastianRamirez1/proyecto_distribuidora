package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.application.mapper.VentaMapper;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ConsultarVentasDiaService {

    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;

    public ConsultarVentasDiaService(VentaRepository ventaRepository, VentaMapper ventaMapper) {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
    }

    public List<VentaResponse> ejecutar(LocalDate fecha) {
        return ventaRepository.findByFecha(fecha).stream()
                .map(ventaMapper::toResponse)
                .collect(Collectors.toList());
    }
}
