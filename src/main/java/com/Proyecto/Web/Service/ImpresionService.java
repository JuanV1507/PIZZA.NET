package com.Proyecto.Web.Service;

import com.Proyecto.Web.DTO.DetalleVentaRequestDTO;
import com.Proyecto.Web.DTO.VentaRequestDTO;
import com.Proyecto.Web.Model.Venta;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class ImpresionService {

    // Comandos ESC/POS estándar
    private static final byte[] INIT = {0x1B, 0x40};
    private static final byte[] ALINEAR_IZQUIERDA = {0x1B, 0x61, 0x00};
    private static final byte[] ALINEAR_CENTRO = {0x1B, 0x61, 0x01};
    private static final byte[] NEGRITA_ON = {0x1B, 0x45, 0x01};
    private static final byte[] NEGRITA_OFF = {0x1B, 0x45, 0x00};
    
    private static final byte[] TEXTO_NORMAL = {0x1D, 0x21, 0x00};
    private static final byte[] TEXTO_DOBLE_ALTO = {0x1D, 0x21, 0x01};
    private static final byte[] TEXTO_DOBLE_ANCHO_ALTO = {0x1D, 0x21, 0x11};
    
    private static final byte[] CORTAR_PAPEL = {0x1D, 0x56, 0x41, 0x00};

    public void imprimirTicket(Venta venta, VentaRequestDTO request) {
        try {
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            if (defaultPrintService == null) {
                System.out.println("No se encontró impresora predeterminada en el servidor.");
                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Inicializar impresora
            baos.write(INIT);
            
            // TITULO
            baos.write(ALINEAR_CENTRO);
            baos.write(NEGRITA_ON);
            baos.write(TEXTO_DOBLE_ANCHO_ALTO);
            escribir(baos, "PIZZERIA UNICORNIO\n");
            
            // SUBTITULO
            baos.write(TEXTO_DOBLE_ALTO);
            escribir(baos, "PIZZA NET V.1.0.1\n");
            
            // VOLVER A NORMAL
            baos.write(TEXTO_NORMAL);
            baos.write(NEGRITA_OFF);
            
            escribir(baos, "--------------------------------\n");
            escribir(baos, "-Cada pedido es especial!\n");
            escribir(baos, "Gracias por elegirnos.\n");
            escribir(baos, "Tel: 988-912-6258\n");
            escribir(baos, "C.19 x 20 y 22, Col.Centro,\n");
            escribir(baos, "Acanceh, Yucatan\n");
            escribir(baos, "Lunes a Domingo: 6:00 PM-12:00PM\n");
            escribir(baos, "--------------------------------\n");
            
            // DATOS DE LA ORDEN
            baos.write(NEGRITA_ON);
            escribir(baos, "DATOS DE LA ORDEN\n");
            baos.write(NEGRITA_OFF);
            escribir(baos, "--------------------------------\n");

            SimpleDateFormat sdfHeader = new SimpleDateFormat("dd/M/yyyy, hh:mm:ss a");
            String folioStr = venta.getFolioVenta() != null ? venta.getFolioVenta() : "S/F";
            escribir(baos, "Folio : " + folioStr + "\n");
            escribir(baos, "Fecha : " + sdfHeader.format(new Date()) + "\n");
            escribir(baos, "Tipo  : " + (request.getTipoOrden() != null ? request.getTipoOrden().toUpperCase() : "") + "\n");
            escribir(baos, "\n");
            
            // CLIENTE
            baos.write(NEGRITA_ON);
            escribir(baos, "CLIENTE\n");
            baos.write(NEGRITA_OFF);
            escribir(baos, "--------------------------------\n");

            escribir(baos, "Nombre : " + request.getNombre() + "\n");
            if ("domicilio".equalsIgnoreCase(request.getTipoOrden())) {
                escribir(baos, "Tel    : " + request.getTelefono() + "\n");
                String dir = request.getDireccion();
                if (dir != null) {
                    escribir(baos, "Dir    : ");
                    int cont = 9;
                    for (char c : dir.toCharArray()) {
                        escribir(baos, String.valueOf(c));
                        cont++;
                        if (cont >= 32) {
                            escribir(baos, "\n         ");
                            cont = 9;
                        }
                    }
                    escribir(baos, "\n");
                }
            } else if ("comedor".equalsIgnoreCase(request.getTipoOrden())) {
                escribir(baos, "Mesa   : " + request.getMesa() + "\n");
            }
            escribir(baos, "\n");

            // PRODUCTOS
            baos.write(NEGRITA_ON);
            escribir(baos, "PRODUCTOS\n");
            escribir(baos, "--------------------------------\n");
            // Encabezado de productos al estilo Receiptify pero con las columnas necesarias
            escribir(baos, "CANT DESCRIPCION         IMPORTE\n");
            escribir(baos, "--------------------------------\n");
            baos.write(NEGRITA_OFF);

            for (DetalleVentaRequestDTO item : request.getCarrito()) {
                double subtotal = item.getCantidad() * item.getPrecio();
                escribir(baos, formatItemLine(item.getCantidad(), item.getNombre(), subtotal) + "\n");
            }

            escribir(baos, "--------------------------------\n");
            
            // SUBTOTAL Y DESCUENTO
            escribir(baos, componerLineaDerecha("Subtotal:", "$" + String.format(Locale.US, "%.2f", request.getTotal())) + "\n");
            escribir(baos, componerLineaDerecha("Descuento:", "$0.00") + "\n");
            
            // TOTAL (RESALTADO)
            escribir(baos, "================================\n");
            baos.write(NEGRITA_ON);
            baos.write(TEXTO_DOBLE_ALTO);
            escribir(baos, componerLineaDerecha("TOTAL:", "$" + String.format(Locale.US, "%.2f", request.getTotal())) + "\n");
            baos.write(TEXTO_NORMAL);
            baos.write(NEGRITA_OFF);
            escribir(baos, "================================\n");
            
            // Pago y Cambio
            double pago = request.getPago();
            double cambio = pago - request.getTotal();
            if (cambio < 0) cambio = 0;
            
            escribir(baos, componerLineaDerecha("Pago:", "$" + String.format(Locale.US, "%.2f", pago)) + "\n");
            baos.write(NEGRITA_ON);
            escribir(baos, componerLineaDerecha("CAMBIO:", "$" + String.format(Locale.US, "%.2f", cambio)) + "\n");
            baos.write(NEGRITA_OFF);
            
            // DESPEDIDA
            escribir(baos, "--------------------------------\n");
            baos.write(ALINEAR_CENTRO);
            escribir(baos, "-Gracias por su preferencia!\n");
            escribir(baos, "Vuelva pronto :)\n");
            escribir(baos, "--------------------------------\n");
            
            SimpleDateFormat sdfFooter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", new Locale("es", "MX"));
            escribir(baos, sdfFooter.format(new Date()) + "\n");
            
            // AVANCE DE PAPEL Y CORTE
            escribir(baos, "\n\n\n\n\n\n\n");
            baos.write(CORTAR_PAPEL);

            // EJECUTAR IMPRESION
            byte[] bytes = baos.toByteArray();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(bytes, flavor, null);
            
            DocPrintJob job = defaultPrintService.createPrintJob();
            job.print(doc, null);
            
            System.out.println("Ticket ESC/POS impreso correctamente en: " + defaultPrintService.getName());

        } catch (Exception e) {
            System.err.println("Error al intentar imprimir ticket ESC/POS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void escribir(ByteArrayOutputStream baos, String texto) throws IOException {
        baos.write(texto.getBytes("Cp850")); 
    }
    
    // Función que da el diseño Receiptify (una sola línea) a los productos
    private String formatItemLine(int qty, String name, double amount) {
        String q = String.format("%02d   ", qty);
        String a = "$" + String.format(Locale.US, "%.2f", amount);
        
        int spaceForName = 32 - q.length() - a.length(); 
        String n = name;
        if (n.length() > spaceForName) {
            n = n.substring(0, spaceForName - 1) + " ";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(q).append(n);
        int padding = 32 - sb.length() - a.length();
        for(int i=0; i<padding; i++) sb.append(" ");
        sb.append(a);
        return sb.toString();
    }
    
    private String componerLineaDerecha(String izquierda, String derecha) {
        int espacios = 32 - izquierda.length() - derecha.length();
        if (espacios < 1) espacios = 1;
        StringBuilder sb = new StringBuilder(izquierda);
        for(int i=0; i<espacios; i++) sb.append(" ");
        sb.append(derecha);
        return sb.toString();
    }
}
