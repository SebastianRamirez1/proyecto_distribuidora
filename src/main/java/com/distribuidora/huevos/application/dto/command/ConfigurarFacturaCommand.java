package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class ConfigurarFacturaCommand {

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    private String direccion;
    private String ciudad;
    private String telefono;
    private String regimen;

    // Resolución DIAN (puede estar vacía si aún no la tiene)
    private String resolucionNumero;
    private LocalDate resolucionFecha;
    private String resolucionPrefijo;
    private Integer resolucionDesde;
    private Integer resolucionHasta;

    public ConfigurarFacturaCommand() {}

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRegimen() { return regimen; }
    public void setRegimen(String regimen) { this.regimen = regimen; }

    public String getResolucionNumero() { return resolucionNumero; }
    public void setResolucionNumero(String resolucionNumero) { this.resolucionNumero = resolucionNumero; }

    public LocalDate getResolucionFecha() { return resolucionFecha; }
    public void setResolucionFecha(LocalDate resolucionFecha) { this.resolucionFecha = resolucionFecha; }

    public String getResolucionPrefijo() { return resolucionPrefijo; }
    public void setResolucionPrefijo(String resolucionPrefijo) { this.resolucionPrefijo = resolucionPrefijo; }

    public Integer getResolucionDesde() { return resolucionDesde; }
    public void setResolucionDesde(Integer resolucionDesde) { this.resolucionDesde = resolucionDesde; }

    public Integer getResolucionHasta() { return resolucionHasta; }
    public void setResolucionHasta(Integer resolucionHasta) { this.resolucionHasta = resolucionHasta; }
}
