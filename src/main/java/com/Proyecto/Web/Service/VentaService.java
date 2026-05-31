package com.Proyecto.Web.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Repository.ProductoRepository;

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
    private ProductoRepository productoRepository;

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
            // Excluir ventas canceladas por devolución del historial
            if (v.getEstado() == Venta.estado.CANCELADA) continue;
            boolean match = false;
            LocalDate fechaVenta = v.getFecha().toLocalDate();
            switch(periodo != null ? periodo : "") {
                case "hoy": match = fechaVenta.isEqual(hoy); break;
                case "ayer": match = fechaVenta.isEqual(hoy.minusDays(1)); break;
                case "semana": match = !fechaVenta.isBefore(hoy.minusDays(6)); break;
                case "mes": match = fechaVenta.getMonthValue() == hoy.getMonthValue() && fechaVenta.getYear() == hoy.getYear(); break;
                case "año": match = fechaVenta.getYear() == hoy.getYear(); break;
                default: match = !fechaVenta.isBefore(hoy.minusDays(6)); break; // default to semana
            }
            if (!match) continue;

            String nombreCliente = "Consumidor Final";
            if (v.getId_cliente() > 0) {
                Clientes c = clienteRepository.findById((long)v.getId_cliente()).orElse(null);
                if (c != null) nombreCliente = c.getNombre();
            }
            LocalDateTime fh = v.getFecha();
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
            // Excluir ventas canceladas por devolución del cálculo de ingresos
            if (v.getEstado() == Venta.estado.CANCELADA) continue;
            boolean match = false;
            LocalDate fechaVenta = v.getFecha().toLocalDate();
            switch(periodo != null ? periodo : "") {
                case "hoy": match = fechaVenta.isEqual(hoy); dias = 1; break;
                case "ayer": match = fechaVenta.isEqual(hoy.minusDays(1)); dias = 1; break;
                case "semana": match = !fechaVenta.isBefore(hoy.minusDays(6)); dias = 7; break;
                case "mes": match = fechaVenta.getMonthValue() == hoy.getMonthValue() && fechaVenta.getYear() == hoy.getYear(); dias = hoy.lengthOfMonth(); break;
                case "año": match = fechaVenta.getYear() == hoy.getYear(); dias = hoy.lengthOfYear(); break;
                default: match = !fechaVenta.isBefore(hoy.minusDays(6)); dias = 7; break;
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
        
        // 1. Calcular Top Productos
        List<ReporteDTO.ProductoReporte> topProductos = new ArrayList<>();
        if (!filtradas.isEmpty()) {
            List<DetalleVenta> detalles = detalleVentaRepository.findByVentas(filtradas);
            Map<Long, Long> unidadesPorProducto = detalles.stream()
                .filter(d -> d.getId_producto() != null)
                .collect(Collectors.groupingBy(DetalleVenta::getId_producto, Collectors.summingLong(DetalleVenta::getCantidad)));
            
            long totalUnidades = unidadesPorProducto.values().stream().mapToLong(Long::longValue).sum();
            
            for (Map.Entry<Long, Long> entry : unidadesPorProducto.entrySet()) {
                Long idProd = entry.getKey();
                Long unidades = entry.getValue();
                String prodNombre = "Producto Desconocido";
                Productos p = productoRepository.findById(idProd).orElse(null);
                if (p != null) {
                    prodNombre = p.getNombre();
                }
                double porcentaje = totalUnidades == 0 ? 0.0 : (double) unidades * 100.0 / totalUnidades;
                topProductos.add(new ReporteDTO.ProductoReporte(prodNombre, unidades, porcentaje));
            }
            // Ordenar por unidades descendiente
            topProductos.sort((a, b) -> Long.compare(b.getUnidades(), a.getUnidades()));
        }
        dto.setTopProductos(topProductos);
        
        // 2. Calcular Ventas por Tipo de Orden
        List<ReporteDTO.TipoServicioReporte> ventasPorTipo = new ArrayList<>();
        if (!filtradas.isEmpty()) {
            Map<Venta.tipo_servicio, List<Venta>> porTipo = filtradas.stream()
                .collect(Collectors.groupingBy(v -> v.getTipo_servicio() != null ? v.getTipo_servicio() : Venta.tipo_servicio.COMEDOR));
            
            for (Venta.tipo_servicio ts : Venta.tipo_servicio.values()) {
                List<Venta> ventasTipo = porTipo.getOrDefault(ts, new ArrayList<>());
                long ordenes = ventasTipo.size();
                double ingresosTipo = ventasTipo.stream().mapToDouble(Venta::getTotal).sum();
                double promedio = ordenes == 0 ? 0.0 : ingresosTipo / ordenes;
                double porcentaje = totalIngresos == 0 ? 0.0 : ingresosTipo * 100.0 / totalIngresos;
                
                // Formatear tipo de servicio con mayúscula inicial
                String tipoNombre = ts.name().charAt(0) + ts.name().substring(1).toLowerCase();
                
                ventasPorTipo.add(new ReporteDTO.TipoServicioReporte(tipoNombre, ordenes, ingresosTipo, promedio, porcentaje));
            }
        }
        dto.setVentasPorTipo(ventasPorTipo);
        
        // 3. Calcular Horas de Mayor Venta
        List<ReporteDTO.HoraReporte> ventasPorHora = new ArrayList<>();
        if (!filtradas.isEmpty()) {
            Map<Integer, Double> ingresosPorHora = filtradas.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().getHour(), Collectors.summingDouble(Venta::getTotal)));
            
            for (Map.Entry<Integer, Double> entry : ingresosPorHora.entrySet()) {
                int h = entry.getKey();
                double totalHora = entry.getValue();
                double porcentaje = totalIngresos == 0 ? 0.0 : totalHora * 100.0 / totalIngresos;
                
                String ampm = h >= 12 ? "PM" : "AM";
                int hour12 = h % 12;
                if (hour12 == 0) hour12 = 12;
                String horaFormateada = String.format("%02d:00 %s", hour12, ampm);
                
                ventasPorHora.add(new ReporteDTO.HoraReporte(horaFormateada, totalHora, porcentaje));
            }
            // Ordenar por ingresos descendiente
            ventasPorHora.sort((a, b) -> Double.compare(b.getTotalIngresos(), a.getTotalIngresos()));
        }
        dto.setVentasPorHora(ventasPorHora);
        
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
        venta.setFecha(LocalDateTime.now());
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
