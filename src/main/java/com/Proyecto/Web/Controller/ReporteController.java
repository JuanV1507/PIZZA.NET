package com.Proyecto.Web.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Web.DTO.HistorialVentaDTO;
import com.Proyecto.Web.DTO.ReporteDTO;
import com.Proyecto.Web.Service.ReporteEmailService;
import com.Proyecto.Web.Service.VentaService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired private VentaService ventaService;
    @Autowired private ReporteEmailService reporteEmailService;

    @GetMapping
    public ResponseEntity<ReporteDTO> obtenerReporte(@RequestParam(defaultValue = "semana") String periodo) {
        try { return ResponseEntity.ok(ventaService.generarReporte(periodo)); }
        catch (Exception e) { return ResponseEntity.status(500).build(); }
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<HistorialVentaDTO>> obtenerHistorialVentas(@RequestParam(defaultValue = "hoy") String periodo) {
        try { return ResponseEntity.ok(ventaService.obtenerHistorialVentas(periodo)); }
        catch (Exception e) { return ResponseEntity.status(500).build(); }
    }

    @GetMapping("/pdf/ventas")
    public ResponseEntity<byte[]> descargarPdfVentas(@RequestParam(defaultValue = "hoy") String periodo) {
        try {
            byte[] pdf = reporteEmailService.generarPdfVentas(periodo);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("Reporte_Ventas_" + periodo + ".pdf").build());
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) { return ResponseEntity.status(500).build(); }
    }

    @GetMapping("/pdf/empleados")
    public ResponseEntity<byte[]> descargarPdfEmpleados() {
        try {
            byte[] pdf = reporteEmailService.generarPdfEmpleados();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("Reporte_Nomina_Semanal.pdf").build());
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) { return ResponseEntity.status(500).build(); }
    }

    @PostMapping("/enviar/ventas")
    public ResponseEntity<Map<String, String>> enviarReporteVentas(@RequestParam(defaultValue = "hoy") String periodo) {
        try {
            reporteEmailService.enviarReporteVentas(periodo);
            return ResponseEntity.ok(Map.of("mensaje", "Reporte de ventas enviado exitosamente a ingjuanchan03@gmail.com"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al enviar: " + e.getMessage()));
        }
    }

    @PostMapping("/enviar/empleados")
    public ResponseEntity<Map<String, String>> enviarReporteEmpleados() {
        try {
            reporteEmailService.enviarReporteEmpleados();
            return ResponseEntity.ok(Map.of("mensaje", "Reporte de nómina enviado exitosamente a ingjuanchan03@gmail.com"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al enviar: " + e.getMessage()));
        }
    }
}
