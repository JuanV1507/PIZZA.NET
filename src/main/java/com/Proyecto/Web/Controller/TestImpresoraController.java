package com.Proyecto.Web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PrinterException;

@RestController
public class TestImpresoraController {

    @GetMapping("/test-impresora")
    public String testImpresora() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Diagnóstico de Impresoras (Java)</h1>");

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        sb.append("<h3>Impresoras instaladas encontradas: ").append(services.length).append("</h3><ul>");
        for (PrintService service : services) {
            sb.append("<li>").append(service.getName()).append("</li>");
        }
        sb.append("</ul>");

        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService != null) {
            sb.append("<h3>Impresora predeterminada: <span style='color:green'>").append(defaultService.getName()).append("</span></h3>");
            
            sb.append("<h3>Intentando imprimir prueba...</h3>");
            try {
                // En lugar de usar Graphics2D que causa hojas en blanco,
                // enviamos el texto directamente a la impresora (RAW / ESC/POS)
                String textoPrueba = "--------------------------------\n" +
                                     "      PRUEBA PIZZA.NET          \n" +
                                     "--------------------------------\n" +
                                     "Si puedes leer esto, significa  \n" +
                                     "que la impresora soporta texto  \n" +
                                     "plano directo.\n\n\n\n\n\n\n"; // Saltos para sacar el papel
                
                byte[] bytes = textoPrueba.getBytes("UTF-8"); // o ISO-8859-1 si hay acentos
                javax.print.DocFlavor flavor = javax.print.DocFlavor.BYTE_ARRAY.AUTOSENSE;
                javax.print.Doc doc = new javax.print.SimpleDoc(bytes, flavor, null);
                
                javax.print.DocPrintJob job = defaultService.createPrintJob();
                job.print(doc, null);
                
                sb.append("<p style='color:green'>Comando de TEXTO PLANO enviado con éxito. Revisa la impresora.</p>");
            } catch (Exception e) {
                sb.append("<p style='color:red'>Error al imprimir: ").append(e.getMessage()).append("</p>");
                for(StackTraceElement el : e.getStackTrace()) {
                    sb.append("<br>").append(el.toString());
                }
            }
        } else {
            sb.append("<h3 style='color:red'>No hay ninguna impresora configurada como predeterminada en Windows.</h3>");
        }

        return sb.toString();
    }
}
