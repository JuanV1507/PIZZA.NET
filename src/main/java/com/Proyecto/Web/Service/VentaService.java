package com.Proyecto.Web.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.Web.DTO.HistorialVentaDTO;
import com.Proyecto.Web.DTO.ReporteDTO;
import com.Proyecto.Web.DTO.VentaRequestDTO;
import com.Proyecto.Web.DTO.DetalleVentaRequestDTO;
import com.Proyecto.Web.Model.Clientes;
import com.Proyecto.Web.Model.DetalleVenta;
import com.Proyecto.Web.Model.Venta;
import com.Proyecto.Web.Repository.ClienteRepository;
import com.Proyecto.Web.Repository.DetalleVentaRepository;
import com.Proyecto.Web.Repository.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private com.Proyecto.Web.Repository.UsuarioRepository usuarioRepository;

    @Autowired
    private ImpresionService impresionService;

    public VentaService(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    public Venta guardar(Venta venta) {
        return ventaRepository.save(venta);
    }

    public Venta buscarPorFolio(String folioVenta) {
        return ventaRepository.findByFolioVenta(folioVenta);
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public String generarSiguienteFolio() {
        try {
            Venta ultima = ventaRepository.obtenerUltimaVenta();
            if (ultima == null || ultima.getFolioVenta() == null || !ultima.getFolioVenta().startsWith("A")) {
                return "A0001";
            }
            String ultimoFolio = ultima.getFolioVenta();
            int num = Integer.parseInt(ultimoFolio.substring(1));
            return String.format("A%04d", num + 1);
        } catch (Exception e) {
            System.err.println("Error obteniendo último folio: " + e.getMessage());
            return "A0001";
        }
    }

    public List<HistorialVentaDTO> obtenerHistorialVentas(String periodo) {
        List<Venta> vents = ventaRepository.findAll();
        LocalDate hoy = LocalDate.now();
        List<HistorialVentaDTO> result = new ArrayList<>();
        
        for (Venta v : vents) {
            if (v.getFecha() == null) continue;
            boolean match = false;
            switch(periodo != null ? periodo : "") {
                case "hoy": match = v.getFecha().isEqual(hoy); break;
                case "ayer": match = v.getFecha().isEqual(hoy.minusDays(1)); break;
                case "semana": match = !v.getFecha().isBefore(hoy.minusDays(6)); break;
                case "mes": match = v.getFecha().getMonthValue() == hoy.getMonthValue() && v.getFecha().getYear() == hoy.getYear(); break;
                case "año": match = v.getFecha().getYear() == hoy.getYear(); break;
                default: match = !v.getFecha().isBefore(hoy.minusDays(6)); break; // default to semana
            }
            if (!match) continue;

            String nombreCliente = "Consumidor Final";
            if (v.getId_cliente() > 0) {
                Clientes c = clienteRepository.findById((long)v.getId_cliente()).orElse(null);
                if (c != null) nombreCliente = c.getNombre();
            }
            LocalDateTime fh = v.getFecha().atStartOfDay();
            String tipo = v.getTipo_servicio() != null ? v.getTipo_servicio().name() : "COMEDOR";
            result.add(new HistorialVentaDTO(v.getFolioVenta(), fh, tipo, v.getTotal(), nombreCliente));
        }
        return result;
    }

    public ReporteDTO generarReporte(String periodo) {
        List<Venta> vents = ventaRepository.findAll();
        LocalDate hoy = LocalDate.now();
        List<Venta> filtradas = new ArrayList<>();
        int dias = 7;
        
        for (Venta v : vents) {
            if (v.getFecha() == null) continue;
            boolean match = false;
            switch(periodo != null ? periodo : "") {
                case "hoy": match = v.getFecha().isEqual(hoy); dias = 1; break;
                case "ayer": match = v.getFecha().isEqual(hoy.minusDays(1)); dias = 1; break;
                case "semana": match = !v.getFecha().isBefore(hoy.minusDays(6)); dias = 7; break;
                case "mes": match = v.getFecha().getMonthValue() == hoy.getMonthValue() && v.getFecha().getYear() == hoy.getYear(); dias = hoy.lengthOfMonth(); break;
                case "año": match = v.getFecha().getYear() == hoy.getYear(); dias = hoy.lengthOfYear(); break;
                default: match = !v.getFecha().isBefore(hoy.minusDays(6)); dias = 7; break;
            }
            if (match) filtradas.add(v);
        }

        ReporteDTO dto = new ReporteDTO();
        double totalIngresos = filtradas.stream().mapToDouble(Venta::getTotal).sum();
        
        dto.setTotalIngresos(totalIngresos);
        dto.setTotalOrdenes(filtradas.size());
        dto.setTicketPromedio(filtradas.isEmpty() ? 0 : totalIngresos / filtradas.size());
        dto.setTicketMinimo(filtradas.stream().mapToDouble(Venta::getTotal).min().orElse(0));
        dto.setTicketMaximo(filtradas.stream().mapToDouble(Venta::getTotal).max().orElse(0));
        dto.setPromedioPorDia(totalIngresos / dias);
        
        dto.setTopProductos(new ArrayList<>());
        dto.setVentasPorTipo(new ArrayList<>());
        dto.setVentasPorHora(new ArrayList<>());
        
        return dto;
    }

    public Venta procesarVentaCompleta(VentaRequestDTO request) {
        // 1. Manejar el Cliente
        int idCliente = 0;
        if (request.getTelefono() != null && !request.getTelefono().trim().isEmpty()) {
            Clientes cliente = clienteRepository.findByTelefono(request.getTelefono()).orElse(null);
            if (cliente == null) {
                cliente = new Clientes();
                cliente.setNombre(request.getNombre());
                cliente.setTelefono(request.getTelefono());
                cliente.setDireccion(request.getDireccion());
                cliente.setReferencias(request.getReferencias());
                cliente.setFechaRegistro(java.time.LocalDateTime.now());
                cliente = clienteRepository.save(cliente);
            } else {
                cliente.setNombre(request.getNombre());
                cliente.setDireccion(request.getDireccion());
                cliente.setReferencias(request.getReferencias());
                cliente = clienteRepository.save(cliente);
            }
            idCliente = Math.toIntExact(cliente.getId_cliente());
        }

        // 2. Crear la Venta
        Venta venta = new Venta();
        venta.setId_cliente(idCliente);
        
        int idUsuario = 1;
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                String username = auth.getName();
                com.Proyecto.Web.Model.Usuario u = usuarioRepository.findByUsername(username).orElse(null);
                if (u != null) {
                    idUsuario = Math.toIntExact(u.getId_usuario());
                } else {
                    java.util.List<com.Proyecto.Web.Model.Usuario> allUsers = usuarioRepository.findAll();
                    if (!allUsers.isEmpty()) idUsuario = Math.toIntExact(allUsers.get(0).getId_usuario());
                }
            } else {
                java.util.List<com.Proyecto.Web.Model.Usuario> allUsers = usuarioRepository.findAll();
                if (!allUsers.isEmpty()) idUsuario = Math.toIntExact(allUsers.get(0).getId_usuario());
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo usuario en sesión: " + e.getMessage());
        }
        
        venta.setId_usuario(idUsuario); 
        venta.setFecha(LocalDate.now());
        venta.setTotal(request.getTotal());
        venta.setEstado(Venta.estado.ACTIVA);

        // Generar folio secuencial A0001, A0002...
        String folio = generarSiguienteFolio();
        venta.setFolioVenta(folio);

        if (request.getTipoOrden() != null) {
            switch (request.getTipoOrden().toLowerCase()) {
                case "domicilio": venta.setTipo_servicio(Venta.tipo_servicio.DOMICILIO); break;
                case "esperando": venta.setTipo_servicio(Venta.tipo_servicio.ESPERANDO); break;
                default: venta.setTipo_servicio(Venta.tipo_servicio.COMEDOR); break;
            }
        } else {
            venta.setTipo_servicio(Venta.tipo_servicio.COMEDOR);
        }

        venta = ventaRepository.save(venta);

        // 3. Guardar el DetalleVenta
        if (request.getCarrito() != null) {
            for (DetalleVentaRequestDTO item : request.getCarrito()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setId_venta(venta);
                detalle.setId_producto(item.getId());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecio_unitario(item.getPrecio());
                detalle.setSubtotal(item.getPrecio() * item.getCantidad());
                
                detalleVentaRepository.save(detalle);
            }
        }

        // --- NUEVO: IMPRIMIR TICKET DESDE EL SERVIDOR ---
        try {
            impresionService.imprimirTicket(venta, request);
        } catch (Exception e) {
            System.err.println("La venta se guardó, pero hubo un error al imprimir el ticket: " + e.getMessage());
        }

        return venta;
    }
}
