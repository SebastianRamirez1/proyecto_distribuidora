package com.distribuidora.huevos.infrastructure.pdf;

import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.entities.Factura;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Genera el PDF de una factura de venta según los requisitos legales colombianos.
 *
 * Cumplimiento legal:
 * - Incluye número consecutivo, NIT del emisor, resolución DIAN, fecha, datos del comprador.
 * - Los huevos están excluidos de IVA según Art. 424 del E.T. (canasta familiar).
 * - Formato compatible con Res. 042/2020 y circulares DIAN para factura en papel/electrónica.
 */
@Component
public class FacturaPdfGenerator {

    private static final DateTimeFormatter FECHA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("es", "CO"));
    private static final DateTimeFormatter FECHA_CORTA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "CO"));

    public byte[] generar(Factura factura, ConfiguracionFactura cfg) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String html = construirHtml(factura, cfg);   // dentro del try: cualquier excepción queda capturada
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de factura: " + e.getMessage(), e);
        }
    }

    // ── HTML template ─────────────────────────────────────────────────────────

    private String construirHtml(Factura f, ConfiguracionFactura cfg) {
        String fmt2 = "%,.2f";
        BigDecimal subtotal = f.getTotal();  // Huevos: excluidos de IVA

        String resolucionInfo = cfg.getResolucionNumero() != null && !cfg.getResolucionNumero().isBlank()
                ? String.format("Resolución DIAN No. %s del %s - Del %05d al %05d",
                    cfg.getResolucionNumero(),
                    cfg.getResolucionFecha() != null ? cfg.getResolucionFecha().format(FECHA_CORTA) : "-",
                    cfg.getResolucionDesde(),
                    cfg.getResolucionHasta())
                : "Resolución DIAN: pendiente de trámite";

        String tipoLabel = switch (f.getTipoProducto()) {
            case EXTRA -> "Canastas de huevo tipo EXTRA";
            case AA    -> "Canastas de huevo tipo AA";
            case A     -> "Canastas de huevo tipo A";
            case B     -> "Canastas de huevo tipo B";
        };

        String pagoLabel = switch (f.getTipoPago()) {
            case EFECTIVO      -> "Efectivo";
            case TRANSFERENCIA -> "Transferencia bancaria";
            case FIADO         -> "Crédito (fiado)";
            default            -> f.getTipoPago().name();
        };

        String tipoFacturaBadge = f.getTipo().name().equals("ELECTRONICA")
                ? "<span style='background:#1d4ed8;color:#fff;padding:2px 8px;border-radius:4px;font-size:10px;'>FACTURA ELECTRÓNICA</span>"
                : "<span style='background:#374151;color:#fff;padding:2px 8px;border-radius:4px;font-size:10px;'>FACTURA DE VENTA</span>";

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: Arial, sans-serif; font-size: 11px; color: #1f2937; background: #fff; }
                    .page { padding: 28px 32px; max-width: 600px; margin: 0 auto; }

                    /* Encabezado */
                    .header { display: flex; justify-content: space-between; align-items: flex-start;
                              border-bottom: 3px solid #f59e0b; padding-bottom: 14px; margin-bottom: 14px; }
                    .empresa h1 { font-size: 16px; font-weight: bold; color: #111827; }
                    .empresa p  { font-size: 10px; color: #6b7280; margin-top: 2px; }
                    .factura-box { text-align: right; }
                    .factura-box .numero { font-size: 20px; font-weight: bold; color: #f59e0b; }
                    .factura-box .fecha  { font-size: 10px; color: #6b7280; margin-top: 3px; }

                    /* Resolución */
                    .resolucion { background: #fef9c3; border: 1px solid #fde68a;
                                  border-radius: 4px; padding: 6px 10px; font-size: 9.5px;
                                  color: #78350f; margin-bottom: 14px; }

                    /* Datos emisor / receptor */
                    .partes { display: flex; gap: 16px; margin-bottom: 14px; }
                    .parte  { flex: 1; border: 1px solid #e5e7eb; border-radius: 6px; padding: 10px; }
                    .parte h3 { font-size: 9px; font-weight: bold; text-transform: uppercase;
                                color: #9ca3af; margin-bottom: 6px; letter-spacing: 0.5px; }
                    .parte p  { font-size: 10.5px; margin-bottom: 2px; }
                    .parte .nombre { font-size: 12px; font-weight: bold; color: #111827; }
                    .parte .nit    { color: #6b7280; }
                    .parte .regimen { font-size: 9px; color: #6b7280; margin-top: 4px; font-style: italic; }

                    /* Tabla de productos */
                    table { width: 100%; border-collapse: collapse; margin-bottom: 14px; }
                    thead tr { background: #f59e0b; }
                    thead th { color: #fff; font-size: 10px; padding: 7px 10px; text-align: left; }
                    thead th:last-child, thead th:nth-child(3), thead th:nth-child(4) { text-align: right; }
                    tbody tr { border-bottom: 1px solid #f3f4f6; }
                    tbody td { padding: 8px 10px; font-size: 10.5px; }
                    tbody td:last-child, tbody td:nth-child(3), tbody td:nth-child(4) { text-align: right; }
                    tbody tr:nth-child(even) { background: #fafafa; }

                    /* Totales */
                    .totales { margin-left: auto; width: 220px; }
                    .totales-row { display: flex; justify-content: space-between;
                                   padding: 4px 0; border-bottom: 1px solid #f3f4f6; font-size: 10.5px; }
                    .totales-row.total { border-top: 2px solid #111827; border-bottom: none;
                                         padding-top: 8px; font-weight: bold; font-size: 13px; color: #111827; }
                    .totales-row .label { color: #6b7280; }
                    .iva-nota { font-size: 9px; color: #6b7280; font-style: italic;
                                margin-top: 4px; text-align: right; }

                    /* Forma de pago */
                    .pago-box { margin-top: 14px; border: 1px solid #e5e7eb;
                                border-radius: 6px; padding: 8px 12px; font-size: 10.5px; }
                    .pago-box span { font-weight: bold; color: #f59e0b; }

                    /* Footer */
                    .footer { margin-top: 20px; border-top: 1px solid #e5e7eb;
                              padding-top: 10px; text-align: center; font-size: 9px; color: #9ca3af; }
                    .footer p { margin-bottom: 2px; }
                  </style>
                </head>
                <body>
                <div class="page">

                  <!-- Encabezado -->
                  <div class="header">
                    <div class="empresa">
                      <h1>%s</h1>
                      <p>NIT: %s</p>
                      <p>%s, %s</p>
                      <p>Tel: %s</p>
                      <p style="margin-top:4px;">%s</p>
                    </div>
                    <div class="factura-box">
                      %s
                      <div class="numero">N° %s</div>
                      <div class="fecha">%s</div>
                    </div>
                  </div>

                  <!-- Resolución DIAN -->
                  <div class="resolucion">%s</div>

                  <!-- Emisor / Receptor -->
                  <div class="partes">
                    <div class="parte">
                      <h3>Vendedor</h3>
                      <p class="nombre">%s</p>
                      <p class="nit">NIT: %s</p>
                      <p>%s, %s</p>
                      <p class="regimen">%s</p>
                    </div>
                    <div class="parte">
                      <h3>Comprador</h3>
                      <p class="nombre">%s</p>
                      <p class="nit">NIT/CC: %s</p>
                    </div>
                  </div>

                  <!-- Detalle productos -->
                  <table>
                    <thead>
                      <tr>
                        <th style="width:40%%">Descripción</th>
                        <th style="width:12%%">Cant.</th>
                        <th style="width:20%%">Precio unit.</th>
                        <th style="width:18%%">Valor</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>%s</td>
                        <td>%d</td>
                        <td>$ %s</td>
                        <td>$ %s</td>
                      </tr>
                    </tbody>
                  </table>

                  <!-- Totales -->
                  <div class="totales">
                    <div class="totales-row">
                      <span class="label">Subtotal</span>
                      <span>$ %s</span>
                    </div>
                    <div class="totales-row">
                      <span class="label">IVA</span>
                      <span>Excluido</span>
                    </div>
                    <div class="totales-row total">
                      <span>TOTAL</span>
                      <span>$ %s</span>
                    </div>
                    <p class="iva-nota">* Excluido de IVA &mdash; Art. 424 E.T. (canasta familiar)</p>
                  </div>

                  <!-- Forma de pago -->
                  <div class="pago-box">
                    Forma de pago: <span>%s</span>
                  </div>

                  <!-- Footer -->
                  <div class="footer">
                    <p>Esta factura es un documento equivalente según la normatividad tributaria colombiana.</p>
                    <p>Conserve esta factura como soporte contable &mdash; Res. 042/2020 DIAN.</p>
                    <p style="margin-top:6px; font-size:8px;">%s &mdash; %s</p>
                  </div>

                </div>
                </body>
                </html>
                """.formatted(
                // Encabezado empresa
                esc(cfg.getRazonSocial()),
                esc(cfg.getNit()),
                esc(cfg.getDireccion()), esc(cfg.getCiudad()),
                esc(cfg.getTelefono()),
                esc(cfg.getRegimen()),
                // Número / fecha
                tipoFacturaBadge,
                esc(f.getNumero()),
                f.getFechaEmision().format(FECHA_FMT),
                // Resolución
                esc(resolucionInfo),
                // Emisor
                esc(cfg.getRazonSocial()), esc(cfg.getNit()),
                esc(cfg.getDireccion()), esc(cfg.getCiudad()),
                esc(cfg.getRegimen()),
                // Receptor
                esc(f.getNombreCliente()),
                esc(f.getNitCliente()),
                // Línea de producto
                esc(tipoLabel),
                f.getCantidad(),
                String.format(Locale.US, fmt2, f.getPrecioUnitario()),
                String.format(Locale.US, fmt2, f.getTotal()),
                // Totales
                String.format(Locale.US, fmt2, subtotal),
                String.format(Locale.US, fmt2, f.getTotal()),
                // Pago
                esc(pagoLabel),
                // Footer
                esc(cfg.getRazonSocial()),
                f.getFechaEmision().format(FECHA_CORTA)
        );
    }

    private String esc(String s) {
        if (s == null || s.isBlank()) return "-";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
