package com.distribuidora.huevos.infrastructure.pdf;

import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.entities.Factura;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Genera el PDF de una factura de venta segun los requisitos legales colombianos.
 *
 * Cumplimiento legal:
 * - Incluye numero consecutivo, NIT del emisor, resolucion DIAN, fecha, datos del comprador.
 * - Los huevos estan excluidos de IVA segun Art. 424 del E.T. (canasta familiar).
 * - Formato compatible con Res. 042/2020 y circulares DIAN para factura en papel/electronica.
 */
@Component
public class FacturaPdfGenerator {

    private static final Logger log = LoggerFactory.getLogger(FacturaPdfGenerator.class);

    private static final DateTimeFormatter FECHA_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("es", "CO"));
    private static final DateTimeFormatter FECHA_CORTA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "CO"));

    public byte[] generar(Factura factura, ConfiguracionFactura cfg) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String html = construirHtml(factura, cfg);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF de factura #{}: [{}] {}",
                    factura.getNumero(), e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException(
                    "Error generando PDF (" + e.getClass().getSimpleName() + "): " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------

    private String construirHtml(Factura f, ConfiguracionFactura cfg) {
        String fmt2 = "%,.2f";
        BigDecimal subtotal = f.getTotal();

        String resolucionInfo = cfg.getResolucionNumero() != null && !cfg.getResolucionNumero().isBlank()
                ? String.format("Resolucion DIAN No. %s del %s - Del %05d al %05d",
                    cfg.getResolucionNumero(),
                    cfg.getResolucionFecha() != null ? cfg.getResolucionFecha().format(FECHA_CORTA) : "-",
                    cfg.getResolucionDesde(),
                    cfg.getResolucionHasta())
                : "Resolucion DIAN: pendiente de tramite";

        String tipoLabel = switch (f.getTipoProducto()) {
            case EXTRA -> "Canastas de huevo tipo EXTRA";
            case AA    -> "Canastas de huevo tipo AA";
            case A     -> "Canastas de huevo tipo A";
            case B     -> "Canastas de huevo tipo B";
        };

        String pagoLabel = switch (f.getTipoPago()) {
            case EFECTIVO      -> "Efectivo";
            case TRANSFERENCIA -> "Transferencia bancaria";
            case FIADO         -> "Credito (fiado)";
            default            -> f.getTipoPago().name();
        };

        String tipoFacturaTexto = f.getTipo().name().equals("ELECTRONICA")
                ? "FACTURA ELECTRONICA"
                : "FACTURA DE VENTA";

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    * { margin: 0; padding: 0; }
                    body { font-size: 11pt; color: #1f2937; }
                    .page { padding: 28pt 32pt; }

                    /* Encabezado */
                    .header-table { width: 100%%; border-bottom: 3pt solid #f59e0b;
                                    padding-bottom: 12pt; margin-bottom: 12pt; }
                    .empresa-nombre { font-size: 15pt; font-weight: bold; color: #111827; }
                    .empresa-sub { font-size: 9pt; color: #6b7280; margin-top: 2pt; }
                    .factura-badge { background: #374151; color: #fff;
                                     padding: 2pt 7pt; font-size: 9pt; }
                    .factura-badge-elec { background: #1d4ed8; color: #fff;
                                          padding: 2pt 7pt; font-size: 9pt; }
                    .factura-numero { font-size: 18pt; font-weight: bold;
                                      color: #f59e0b; text-align: right; }
                    .factura-fecha { font-size: 9pt; color: #6b7280; text-align: right; }

                    /* Resolucion */
                    .resolucion { background: #fef9c3; border: 1pt solid #fde68a;
                                  padding: 5pt 8pt; font-size: 9pt;
                                  color: #78350f; margin-bottom: 12pt; }

                    /* Partes */
                    .partes-table { width: 100%%; margin-bottom: 12pt; }
                    .parte-cell { border: 1pt solid #e5e7eb; padding: 8pt; width: 50%%; vertical-align: top; }
                    .parte-titulo { font-size: 8pt; font-weight: bold; color: #9ca3af;
                                    margin-bottom: 5pt; }
                    .parte-nombre { font-size: 11pt; font-weight: bold; color: #111827; }
                    .parte-sub { font-size: 10pt; color: #6b7280; margin-top: 2pt; }
                    .parte-regimen { font-size: 8pt; color: #6b7280; margin-top: 4pt;
                                     font-style: italic; }

                    /* Tabla productos */
                    .prod-table { width: 100%%; border-collapse: collapse; margin-bottom: 12pt; }
                    .prod-table thead tr { background: #f59e0b; }
                    .prod-table thead th { color: #fff; font-size: 9pt; padding: 6pt 8pt;
                                           text-align: left; }
                    .prod-table tbody td { padding: 7pt 8pt; font-size: 10pt;
                                           border-bottom: 1pt solid #f3f4f6; }

                    /* Totales */
                    .totales-table { margin-left: auto; width: 200pt; }
                    .totales-table td { padding: 3pt 0; font-size: 10pt;
                                        border-bottom: 1pt solid #f3f4f6; }
                    .totales-label { color: #6b7280; }
                    .totales-final td { border-top: 2pt solid #111827; border-bottom: none;
                                        padding-top: 7pt; font-weight: bold;
                                        font-size: 12pt; color: #111827; }
                    .iva-nota { font-size: 8pt; color: #6b7280; font-style: italic;
                                text-align: right; margin-top: 4pt; }

                    /* Pago */
                    .pago-box { margin-top: 12pt; border: 1pt solid #e5e7eb;
                                padding: 7pt 10pt; font-size: 10pt; }
                    .pago-valor { font-weight: bold; color: #f59e0b; }

                    /* Footer */
                    .footer { margin-top: 18pt; border-top: 1pt solid #e5e7eb;
                              padding-top: 8pt; text-align: center;
                              font-size: 8pt; color: #9ca3af; }
                    .footer p { margin-bottom: 2pt; }
                  </style>
                </head>
                <body>
                <div class="page">

                  <!-- Encabezado -->
                  <table class="header-table" cellpadding="0" cellspacing="0">
                    <tr>
                      <td style="vertical-align:top;">
                        <div class="empresa-nombre">%s</div>
                        <div class="empresa-sub">NIT: %s</div>
                        <div class="empresa-sub">%s, %s</div>
                        <div class="empresa-sub">Tel: %s</div>
                        <div class="empresa-sub" style="margin-top:3pt;">%s</div>
                      </td>
                      <td style="text-align:right; vertical-align:top;">
                        <div class="%s">%s</div>
                        <div class="factura-numero">N.&#176; %s</div>
                        <div class="factura-fecha">%s</div>
                      </td>
                    </tr>
                  </table>

                  <!-- Resolucion DIAN -->
                  <div class="resolucion">%s</div>

                  <!-- Emisor / Receptor -->
                  <table class="partes-table" cellpadding="0" cellspacing="4">
                    <tr>
                      <td class="parte-cell">
                        <div class="parte-titulo">VENDEDOR</div>
                        <div class="parte-nombre">%s</div>
                        <div class="parte-sub">NIT: %s</div>
                        <div class="parte-sub">%s, %s</div>
                        <div class="parte-regimen">%s</div>
                      </td>
                      <td class="parte-cell">
                        <div class="parte-titulo">COMPRADOR</div>
                        <div class="parte-nombre">%s</div>
                        <div class="parte-sub">NIT/CC: %s</div>
                      </td>
                    </tr>
                  </table>

                  <!-- Detalle productos -->
                  <table class="prod-table" cellpadding="0" cellspacing="0">
                    <thead>
                      <tr>
                        <th style="width:40%%">Descripcion</th>
                        <th style="width:12%%">Cant.</th>
                        <th style="width:22%%; text-align:right;">Precio unit.</th>
                        <th style="width:20%%; text-align:right;">Valor</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>%s</td>
                        <td>%d</td>
                        <td style="text-align:right;">$ %s</td>
                        <td style="text-align:right;">$ %s</td>
                      </tr>
                    </tbody>
                  </table>

                  <!-- Totales -->
                  <table class="totales-table" cellpadding="0" cellspacing="0">
                    <tr>
                      <td class="totales-label">Subtotal</td>
                      <td style="text-align:right;">$ %s</td>
                    </tr>
                    <tr>
                      <td class="totales-label">IVA</td>
                      <td style="text-align:right;">Excluido</td>
                    </tr>
                    <tr class="totales-final">
                      <td>TOTAL</td>
                      <td style="text-align:right;">$ %s</td>
                    </tr>
                  </table>
                  <div class="iva-nota">* Excluido de IVA &#8212; Art. 424 E.T. (canasta familiar)</div>

                  <!-- Forma de pago -->
                  <div class="pago-box">
                    Forma de pago: <span class="pago-valor">%s</span>
                  </div>

                  <!-- Footer -->
                  <div class="footer">
                    <p>Esta factura es un documento equivalente segun la normatividad tributaria colombiana.</p>
                    <p>Conserve esta factura como soporte contable &#8212; Res. 042/2020 DIAN.</p>
                    <p style="margin-top:5pt; font-size:7pt;">%s &#8212; %s</p>
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
                // Badge tipo + numero + fecha
                f.getTipo().name().equals("ELECTRONICA") ? "factura-badge-elec" : "factura-badge",
                esc(tipoFacturaTexto),
                esc(f.getNumero()),
                f.getFechaEmision().format(FECHA_FMT),
                // Resolucion
                esc(resolucionInfo),
                // Emisor
                esc(cfg.getRazonSocial()), esc(cfg.getNit()),
                esc(cfg.getDireccion()), esc(cfg.getCiudad()),
                esc(cfg.getRegimen()),
                // Receptor
                esc(f.getNombreCliente()),
                esc(f.getNitCliente()),
                // Linea de producto
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
