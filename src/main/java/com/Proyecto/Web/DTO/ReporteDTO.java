package com.Proyecto.Web.DTO;

import java.util.List;

public class ReporteDTO {

    private double totalIngresos;
    private long totalOrdenes;
    private double ticketPromedio;
    private double ticketMinimo;
    private double ticketMaximo;
    private double promedioPorDia;
    private List<ProductoReporte> topProductos;
    private List<TipoServicioReporte> ventasPorTipo;
    private List<HoraReporte> ventasPorHora;

    public static class ProductoReporte {
        private String nombre;
        private long unidades;
        private double porcentaje;
        public ProductoReporte(String nombre, long unidades, double porcentaje) {
            this.nombre = nombre; this.unidades = unidades; this.porcentaje = porcentaje;
        }
        public String getNombre() { return nombre; }
        public long getUnidades() { return unidades; }
        public double getPorcentaje() { return porcentaje; }
    }

    public static class TipoServicioReporte {
        private String tipo;
        private long ordenes;
        private double totalIngresos;
        private double promedio;
        private double porcentaje;
        public TipoServicioReporte(String tipo, long ordenes, double totalIngresos, double promedio, double porcentaje) {
            this.tipo = tipo; this.ordenes = ordenes; this.totalIngresos = totalIngresos;
            this.promedio = promedio; this.porcentaje = porcentaje;
        }
        public String getTipo() { return tipo; }
        public long getOrdenes() { return ordenes; }
        public double getTotalIngresos() { return totalIngresos; }
        public double getPromedio() { return promedio; }
        public double getPorcentaje() { return porcentaje; }
    }

    public static class HoraReporte {
        private String hora;
        private double totalIngresos;
        private double porcentaje;
        public HoraReporte(String hora, double totalIngresos, double porcentaje) {
            this.hora = hora; this.totalIngresos = totalIngresos; this.porcentaje = porcentaje;
        }
        public String getHora() { return hora; }
        public double getTotalIngresos() { return totalIngresos; }
        public double getPorcentaje() { return porcentaje; }
    }

    public double getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(double totalIngresos) { this.totalIngresos = totalIngresos; }
    public long getTotalOrdenes() { return totalOrdenes; }
    public void setTotalOrdenes(long totalOrdenes) { this.totalOrdenes = totalOrdenes; }
    public double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(double ticketPromedio) { this.ticketPromedio = ticketPromedio; }
    public double getTicketMinimo() { return ticketMinimo; }
    public void setTicketMinimo(double ticketMinimo) { this.ticketMinimo = ticketMinimo; }
    public double getTicketMaximo() { return ticketMaximo; }
    public void setTicketMaximo(double ticketMaximo) { this.ticketMaximo = ticketMaximo; }
    public double getPromedioPorDia() { return promedioPorDia; }
    public void setPromedioPorDia(double promedioPorDia) { this.promedioPorDia = promedioPorDia; }
    public List<ProductoReporte> getTopProductos() { return topProductos; }
    public void setTopProductos(List<ProductoReporte> topProductos) { this.topProductos = topProductos; }
    public List<TipoServicioReporte> getVentasPorTipo() { return ventasPorTipo; }
    public void setVentasPorTipo(List<TipoServicioReporte> ventasPorTipo) { this.ventasPorTipo = ventasPorTipo; }
    public List<HoraReporte> getVentasPorHora() { return ventasPorHora; }
    public void setVentasPorHora(List<HoraReporte> ventasPorHora) { this.ventasPorHora = ventasPorHora; }
}
