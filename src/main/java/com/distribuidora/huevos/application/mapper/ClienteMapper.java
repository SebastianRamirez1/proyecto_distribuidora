package com.distribuidora.huevos.application.mapper;

import com.distribuidora.huevos.application.dto.response.ClienteResponse;
import com.distribuidora.huevos.domain.entities.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNombre(cliente.getNombre());
        response.setTipo(cliente.getTipo());

        if (cliente.getPrecioEspecial() != null) {
            response.setPrecioEspecialExtra(cliente.getPrecioEspecial().getPrecioExtra().getValor());
            response.setPrecioEspecialNormal(cliente.getPrecioEspecial().getPrecioNormal().getValor());
        }

        if (cliente.getDescuentoVolumen() != null) {
            response.setDescuentoDesdeCanastas(cliente.getDescuentoVolumen().getDesdeCanastas().getValor());
            response.setDescuentoPrecioExtra(cliente.getDescuentoVolumen().getPrecioExtra().getValor());
            response.setDescuentoPrecioNormal(cliente.getDescuentoVolumen().getPrecioNormal().getValor());
        }

        return response;
    }
}
