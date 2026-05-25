package com.distribuidora.huevos.domain.entities;

import java.time.LocalDate;

/**
 * Singleton que guarda los datos legales del emisor (la distribuidora)
 * y el consecutivo de numeración de facturas.
 */
public class ConfiguracionFactura {

    private Long id;

    // Datos del emisor
    private String razonSocial;
    private String nit;
    private String direccion;
    private String ciudad;
    private String telefono;
    private String regimen;

    // Resolución DIAN
    private String resolucionNumero;
    private LocalDate resolucionFecha;
    private String resolucionPrefijo;
    private int resolucionDesde;
    private int resolucionHasta;

    // Consecutivo actual
    private int consecutivoActual;

    public ConfiguracionFactura(Long id, String razonSocial, String nit,
                                 String direccion, String ciudad, String telefono,
                                 String regimen, String resolucionNumero,
                                 LocalDate resolucionFecha, String resolucionPrefijo,
                                 int resolucionDesde, int resolucionHasta,
                                 int consecutivoActual) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.nit = nit;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.regimen = regimen;
        this.resolucionNumero = resolucionNumero;
        this.resolucionFecha = resolucionFecha;
        this.resolucionPrefijo = resolucionPrefijo;
        this.resolucionDesde = resolucionDesde;
        this.resolucionHasta = resolucionHasta;
        this.consecutivoActual = consecutivoActual;
    }

    /** Genera el número de factura formateado y avanza el consecutivo. */
    public String generarYAvanzarConsecutivo() {
        String numero = resolucionPrefijo + String.format("%05d", consecutivoActual);
        this.consecutivoActual++;
        return numero;
    }

    public boolean estaConfigurada() {
        return razonSocial != null && !razonSocial.isBlank()
                && nit != null && !nit.isBlank();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getRazonSocial() { return razonSocial; }
    public String getNit() { return nit; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public String getTelefono() { return telefono; }
    public String getRegimen() { return regimen; }
    public String getResolucionNumero() { return resolucionNumero; }
    public LocalDate getResolucionFecha() { return resolucionFecha; }
    public String getResolucionPrefijo() { return resolucionPrefijo; }
    public int getResolucionDesde() { return resolucionDesde; }
    public int getResolucionHasta() { return resolucionHasta; }
    public int getConsecutivoActual() { return consecutivoActual; }

    // ── Setters para ActualizarConfiguracionFacturaService ────────────────────

    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public void setNit(String nit) { this.nit = nit; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setRegimen(String regimen) { this.regimen = regimen; }
    public void setResolucionNumero(String resolucionNumero) { this.resolucionNumero = resolucionNumero; }
    public void setResolucionFecha(LocalDate resolucionFecha) { this.resolucionFecha = resolucionFecha; }
    public void setResolucionPrefijo(String resolucionPrefijo) { this.resolucionPrefijo = resolucionPrefijo; }
    public void setResolucionDesde(int resolucionDesde) { this.resolucionDesde = resolucionDesde; }
    public void setResolucionHasta(int resolucionHasta) { this.resolucionHasta = resolucionHasta; }
}
