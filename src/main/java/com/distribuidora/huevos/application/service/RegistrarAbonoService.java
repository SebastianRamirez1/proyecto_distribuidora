package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarAbonoCommand;
import com.distribuidora.huevos.domain.entities.Abono;
import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
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
    private final AbonoRepository abonoRepository;

    public RegistrarAbonoService(CreditoRepository creditoRepository,
                                 CajaRepository cajaRepository,
                                 AbonoRepository abonoRepository) {
        this.creditoRepository = creditoRepository;
        this.cajaRepository    = cajaRepository;
        this.abonoRepository   = abonoRepository;
    }

    public void ejecutar(RegistrarAbonoCommand command) {
        Credito credito = creditoRepository.findByClienteId(command.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe crédito para el cliente con ID: " + command.getClienteId()));

        Dinero monto = Dinero.de(command.getMonto());
        credito.abonar(monto);
        creditoRepository.save(credito);

        // registrarAbono suma al efectivo/transferencia (dinero físico que entró)
        // Y también a totalAbonos (deuda cobrada hoy — informativo).
        // calcularTotalCobrado usa solo efectivo+transferencia para no duplicar.
        LocalDate hoy = LocalDate.now();
        Caja caja = cajaRepository.findByFecha(hoy).orElse(Caja.nueva(hoy));
        caja.registrarAbono(command.getMedioPago(), monto);
        cajaRepository.save(caja);

        // Registra el abono individual para poder consultarlo en el historial.
        abonoRepository.save(Abono.nuevo(command.getClienteId(), monto, command.getMedioPago()));
    }
}
