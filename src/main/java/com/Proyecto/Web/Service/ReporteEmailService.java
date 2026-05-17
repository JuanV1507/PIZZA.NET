package com.Proyecto.Web.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.Proyecto.Web.DTO.HistorialVentaDTO;
import com.Proyecto.Web.DTO.ReporteDTO;
import com.Proyecto.Web.Model.Empleado;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.mail.internet.MimeMessage;

@Service
public class ReporteEmailService {

    @Autowired private JavaMailSender mailSender;
    @Autowired private VentaService ventaService;
    @Autowired private EmpleadoService empleadoService;

    @Value("${reportes.email.destinatario}") private String destinatario;
    @Value("${reportes.email.remitente}")    private String remitente;

    private static final DeviceRgb NARANJA  = new DeviceRgb(249, 115, 22);
    private static final DeviceRgb GRIS_OSC = new DeviceRgb(30, 30, 50);
    private static final DeviceRgb GRIS_CLR = new DeviceRgb(241, 245, 249);

    public byte[] generarPdfVentas(String periodo) throws Exception {
        ReporteDTO reporte = ventaService.generarReporte(periodo);
        List<HistorialVentaDTO> historial = ventaService.obtenerHistorialVentas(periodo);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(36, 36, 36, 36);

        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        doc.add(new Paragraph("PIZZA.NET").setFont(bold).setFontSize(22).setFontColor(NARANJA).setTextAlignment(TextAlignment.CENTER));

        String labelPeriodo = switch (periodo) {
            case "hoy"  -> "Hoy - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case "ayer" -> "Ayer - " + LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case "mes"  -> "Este Mes - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            case "año"  -> "Este Año - " + LocalDate.now().getYear();
            default     -> "Esta Semana";
        };

        doc.add(new Paragraph("Reporte de Ventas | " + labelPeriodo).setFont(bold).setFontSize(14).setFontColor(GRIS_OSC).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFont(regular).setFontSize(9).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        doc.add(new Paragraph("RESUMEN DE METRICAS").setFont(bold).setFontSize(12).setFontColor(NARANJA).setMarginBottom(6));
        Table metricas = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        agregarFilaMetrica(metricas, "Ingresos Totales",  formatDinero(reporte.getTotalIngresos()), bold, regular);
        agregarFilaMetrica(metricas, "Total de Ordenes",  String.valueOf(reporte.getTotalOrdenes()), bold, regular);
        agregarFilaMetrica(metricas, "Ticket Promedio",   formatDinero(reporte.getTicketPromedio()), bold, regular);
        agregarFilaMetrica(metricas, "Ticket Minimo",     formatDinero(reporte.getTicketMinimo()), bold, regular);
        agregarFilaMetrica(metricas, "Ticket Maximo",     formatDinero(reporte.getTicketMaximo()), bold, regular);
        agregarFilaMetrica(metricas, "Promedio por Dia",  formatDinero(reporte.getPromedioPorDia()), bold, regular);
        doc.add(metricas);

        doc.add(new Paragraph("\nTOP PRODUCTOS MAS VENDIDOS").setFont(bold).setFontSize(12).setFontColor(NARANJA).setMarginTop(16).setMarginBottom(6));
        Table tabProd = new Table(UnitValue.createPercentArray(new float[]{10, 50, 20, 20})).useAllAvailableWidth();
        agregarCeldaHeader(tabProd, "#", bold); agregarCeldaHeader(tabProd, "Producto", bold);
        agregarCeldaHeader(tabProd, "Unidades", bold); agregarCeldaHeader(tabProd, "% Ventas", bold);
        List<ReporteDTO.ProductoReporte> productos = reporte.getTopProductos();
        for (int i = 0; i < productos.size(); i++) {
            ReporteDTO.ProductoReporte p = productos.get(i); boolean par = i % 2 == 0;
            agregarCeldaData(tabProd, String.valueOf(i + 1), regular, par);
            agregarCeldaData(tabProd, p.getNombre(), regular, par);
            agregarCeldaData(tabProd, String.valueOf(p.getUnidades()), regular, par);
            agregarCeldaData(tabProd, String.format("%.1f%%", p.getPorcentaje()), regular, par);
        }
        doc.add(tabProd);

        doc.add(new Paragraph("\nHISTORIAL DE VENTAS").setFont(bold).setFontSize(12).setFontColor(NARANJA).setMarginTop(16).setMarginBottom(6));
        Table tabVentas = new Table(UnitValue.createPercentArray(new float[]{15, 25, 25, 20, 15})).useAllAvailableWidth();
        agregarCeldaHeader(tabVentas, "Folio", bold); agregarCeldaHeader(tabVentas, "Fecha/Hora", bold);
        agregarCeldaHeader(tabVentas, "Cliente", bold); agregarCeldaHeader(tabVentas, "Tipo", bold);
        agregarCeldaHeader(tabVentas, "Total", bold);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        for (int i = 0; i < historial.size(); i++) {
            HistorialVentaDTO v = historial.get(i); boolean par = i % 2 == 0;
            agregarCeldaData(tabVentas, v.getFolioVenta() != null ? v.getFolioVenta() : "-", regular, par);
            agregarCeldaData(tabVentas, v.getFechaHora() != null ? v.getFechaHora().format(dtf) : "-", regular, par);
            agregarCeldaData(tabVentas, v.getNombreCliente(), regular, par);
            agregarCeldaData(tabVentas, v.getTipoServicio(), regular, par);
            agregarCeldaData(tabVentas, formatDinero(v.getTotal()), regular, par);
        }
        doc.add(tabVentas);
        doc.add(new Paragraph("\nTotal: " + formatDinero(reporte.getTotalIngresos())).setFont(bold).setFontSize(11).setFontColor(NARANJA).setTextAlignment(TextAlignment.RIGHT).setMarginTop(10));
        doc.close();
        return baos.toByteArray();
    }

    public byte[] generarPdfEmpleados() throws Exception {
        List<Empleado> empleados = empleadoService.listarTodos();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(36, 36, 36, 36);
        PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        doc.add(new Paragraph("PIZZA.NET").setFont(bold).setFontSize(22).setFontColor(NARANJA).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Reporte Semanal de Nomina").setFont(bold).setFontSize(14).setFontColor(GRIS_OSC).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Semana del " + LocalDate.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " al " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFont(regular).setFontSize(10).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        doc.add(new Paragraph("CONTROL DE NOMINA").setFont(bold).setFontSize(12).setFontColor(NARANJA).setMarginBottom(6));
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{5, 30, 20, 15, 15, 15})).useAllAvailableWidth();
        agregarCeldaHeader(tabla, "ID", bold); agregarCeldaHeader(tabla, "Nombre", bold);
        agregarCeldaHeader(tabla, "Puesto", bold); agregarCeldaHeader(tabla, "Dias Trab.", bold);
        agregarCeldaHeader(tabla, "Pago/Dia", bold); agregarCeldaHeader(tabla, "Sueldo Total", bold);

        double totalNomina = 0;
        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i); boolean par = i % 2 == 0;
            agregarCeldaData(tabla, String.valueOf(e.getId_empleado()), regular, par);
            agregarCeldaData(tabla, e.getNombres(), regular, par);
            agregarCeldaData(tabla, e.getPuesto(), regular, par);
            agregarCeldaData(tabla, String.valueOf(e.getDiasTrabajados()), regular, par);
            agregarCeldaData(tabla, formatDinero(e.getPagoDiario()), regular, par);
            agregarCeldaData(tabla, formatDinero(e.getSalario_calculado()), regular, par);
            totalNomina += e.getSalario_calculado() != null ? e.getSalario_calculado() : 0;
        }
        doc.add(tabla);
        doc.add(new Paragraph("Gasto Total de Nomina Semanal: " + formatDinero(totalNomina)).setFont(bold).setFontSize(13).setFontColor(NARANJA).setTextAlignment(TextAlignment.RIGHT).setMarginTop(12));
        doc.close();
        return baos.toByteArray();
    }

    public void enviarReporteVentas(String periodo) throws Exception {
        byte[] pdf = generarPdfVentas(periodo);
        String labelPeriodo = switch (periodo) {
            case "hoy"  -> "Hoy"; case "ayer" -> "Ayer";
            case "mes"  -> "Este Mes"; case "año" -> "Este Año";
            default     -> "Esta Semana";
        };
        String asunto = "Pizza.NET | Reporte de Ventas - " + labelPeriodo;
        String cuerpo = "<html><body style='font-family:Arial,sans-serif'>"
                + "<div style='background:#f97316;padding:20px;text-align:center;border-radius:8px 8px 0 0'>"
                + "<h1 style='color:white;margin:0'>PIZZA.NET</h1><p style='color:white'>Reporte de Ventas</p></div>"
                + "<div style='padding:24px;background:#f1f5f9'><p>Período: <strong>" + labelPeriodo + "</strong></p>"
                + "<ul><li>Métricas generales</li><li>Top productos</li><li>Historial de ventas</li></ul>"
                + "<p style='color:#64748b;font-size:12px'>Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>"
                + "</div></body></html>";
        String nombre = "Reporte_Ventas_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        enviarConAdjunto(asunto, cuerpo, pdf, nombre);
    }

    public void enviarReporteEmpleados() throws Exception {
        byte[] pdf = generarPdfEmpleados();
        String asunto = "Pizza.NET | Reporte Semanal de Nomina - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String cuerpo = "<html><body style='font-family:Arial,sans-serif'>"
                + "<div style='background:#f97316;padding:20px;text-align:center;border-radius:8px 8px 0 0'>"
                + "<h1 style='color:white;margin:0'>PIZZA.NET</h1><p style='color:white'>Reporte Semanal de Nomina</p></div>"
                + "<div style='padding:24px;background:#f1f5f9'>"
                + "<ul><li>Lista de empleados</li><li>Días trabajados y salarios</li><li>Total nómina semanal</li></ul>"
                + "<p style='color:#64748b;font-size:12px'>Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>"
                + "</div></body></html>";
        String nombre = "Reporte_Nomina_Semanal_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        enviarConAdjunto(asunto, cuerpo, pdf, nombre);
    }

    private void enviarConAdjunto(String asunto, String cuerpoHtml, byte[] adjunto, String nombreArchivo) throws Exception {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setFrom(remitente);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(cuerpoHtml, true);
        helper.addAttachment(nombreArchivo, new ByteArrayResource(adjunto));
        mailSender.send(mensaje);
    }

    private void agregarFilaMetrica(Table tabla, String label, String valor, PdfFont bold, PdfFont regular) {
        tabla.addCell(new Cell().add(new Paragraph(label).setFont(bold).setFontSize(10)).setBackgroundColor(GRIS_CLR).setPadding(6));
        tabla.addCell(new Cell().add(new Paragraph(valor).setFont(regular).setFontSize(10)).setPadding(6));
    }

    private void agregarCeldaHeader(Table tabla, String texto, PdfFont bold) {
        tabla.addHeaderCell(new Cell().add(new Paragraph(texto).setFont(bold).setFontSize(10).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(NARANJA).setPadding(6).setTextAlignment(TextAlignment.CENTER));
    }

    private void agregarCeldaData(Table tabla, String texto, PdfFont regular, boolean filaPar) {
        tabla.addCell(new Cell().add(new Paragraph(texto != null ? texto : "-").setFont(regular).setFontSize(9))
                .setBackgroundColor(filaPar ? GRIS_CLR : ColorConstants.WHITE).setPadding(5));
    }

    private String formatDinero(double valor) { return String.format("$%,.2f", valor); }
}
