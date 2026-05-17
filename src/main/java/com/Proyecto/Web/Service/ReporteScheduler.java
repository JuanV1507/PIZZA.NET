package com.Proyecto.Web.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReporteScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReporteScheduler.class);

    @Autowired private ReporteEmailService reporteEmailService;

    /** Reporte diario de ventas - Todos los días a las 11:55 PM */
    @Scheduled(cron = "0 55 23 * * *")
    public void enviarReporteDiarioVentas() {
        log.info("=== SCHEDULER: Enviando reporte diario de ventas ===");
        try {
            reporteEmailService.enviarReporteVentas("hoy");
            log.info("=== SCHEDULER: Reporte de ventas enviado exitosamente ===");
        } catch (Exception e) {
            log.error("=== SCHEDULER ERROR: {}", e.getMessage(), e);
        }
    }

    /** Reporte semanal de empleados - Domingos a las 11:55 PM */
    @Scheduled(cron = "0 55 23 * * SUN")
    public void enviarReporteSemanalEmpleados() {
        log.info("=== SCHEDULER: Enviando reporte semanal de empleados ===");
        try {
            reporteEmailService.enviarReporteEmpleados();
            log.info("=== SCHEDULER: Reporte de empleados enviado exitosamente ===");
        } catch (Exception e) {
            log.error("=== SCHEDULER ERROR: {}", e.getMessage(), e);
        }
    }
}
