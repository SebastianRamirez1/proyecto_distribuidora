package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarAbonoCommand;
import com.distribuidora.huevos.domain.entities.Abono;
import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import com.distribuidora.huevos.domain.repositories.CajaRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
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
    private final JornadaRepository jornadaRepository;

    public RegistrarAbonoService(CreditoRepository creditoRepository,
                                 CajaRepository cajaRepository,
                                 AbonoRepository abonoRepository,
                                 JornadaRepository jornadaRepository) {
        this.creditoRepository = creditoRepository;
        this.cajaRepository    = cajaRepository;
        this.abonoRepository   = abonoRepository;
        this.jornadaRepository = jornadaRepository;
    }

    public void ejecutar(RegistrarAbonoCommand command) {
        Credito credito = creditoRepository.findByClienteId(command.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe crédito para el cliente con ID: " + command.getClienteId()));

        Dinero monto = Dinero.de(command.getMonto());
        credito.abonar(monto);
        creditoRepository.save(credito);

        // Igual que ventas: si viene jornadaId usar esa fecha, si no usar la ABIERTA.
        LocalDate fechaJornada;
        if (command.getJornadaId() != null) {
            fechaJornada = jornadaRepository.findById(command.getJornadaId())
                    .map(com.distribuidora.huevos.domain.entities.Jornada::getFecha)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Jornada no encontrada con ID: " + command.getJornadaId()));
        } else {
            fechaJornada = jornadaRepository.findActiva()
                    .map(com.distribuidora.huevos.domain.entities.Jornada::getFecha)
                    .orElse(LocalDate.now());
        }
        Caja caja = cajaRepository.findByFecha(fechaJornada).orElse(Caja.nueva(fechaJornada));
        caja.registrarAbono(command.getMedioPago(), monto);
        cajaRepository.save(caja);

        // Registra el abono individual para poder consultarlo en el historial.
        abonoRepository.save(Abono.nuevo(command.getClienteId(), monto, command.getMedioPago()));
    }
}
