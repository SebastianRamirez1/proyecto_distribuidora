package com.distribuidora.huevos.application.dto.response;

import java.time.LocalDate;

public class ConfiguracionFacturaResponse {

    private String razonSocial;
    private String nit;
    private String direccion;
    private String ciudad;
    private String telefono;
    private String regimen;
    private String resolucionNumero;
    private LocalDate resolucionFecha;
    private String resolucionPrefijo;
    private int resolucionDesde;
    private int resolucionHasta;
    private int consecutivoActual;
    private boolean configurada;

    public ConfiguracionFacturaResponse() {}

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

    public int getResolucionDesde() { return resolucionDesde; }
    public void setResolucionDesde(int resolucionDesde) { this.resolucionDesde = resolucionDesde; }

    public int getResolucionHasta() { return resolucionHasta; }
    public void setResolucionHasta(int resolucionHasta) { this.resolucionHasta = resolucionHasta; }

    public int getConsecutivoActual() { return consecutivoActual; }
    public void setConsecutivoActual(int consecutivoActual) { this.consecutivoActual = consecutivoActual; }

    public boolean isConfigurada() { return configurada; }
    public void setConfigurada(boolean configurada) { this.configurada = configurada; }
}
