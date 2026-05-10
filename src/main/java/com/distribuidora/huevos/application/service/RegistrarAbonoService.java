package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarAbonoCommand;
import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.CajaRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class RegistrarAbonoService {

    private final CreditoRepository creditoRepository;
    private final CajaRepository cajaRepository;

    public RegistrarAbonoService(CreditoRepository creditoRepository,
                                 CajaRepository cajaRepository) {
        this.creditoRepository = creditoRepository;
        this.cajaRepository = cajaRepository;
    }

    public void ejecutar(RegistrarAbonoCommand command) {
        Credito credito = creditoRepository.findByClienteId(command.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe crédito para el cliente con ID: " + command.getClienteId()));

        Dinero monto = Dinero.de(command.getMonto());
        credito.abonar(monto);
        creditoRepository.save(credito);

        // Registrar en caja según cómo pagó el cliente (efectivo sube totalEfectivo,
        // transferencia sube totalTransferencia — no va al cajón genérico de ABONO).
        LocalDate hoy = LocalDate.now();
        Caja caja = cajaRepository.findByFecha(hoy).orElse(Caja.nueva(hoy));
        caja.registrarPago(command.getMedioPago(), monto);
        cajaRepository.save(caja);
    }
}
