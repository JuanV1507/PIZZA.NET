package com.Proyecto.Web.DTO;

import java.time.LocalDateTime;

public class HistorialVentaDTO {
    private String folioVenta;
    private LocalDateTime fechaHora;
    private String tipoServicio;
    private double total;
    private String nombreCliente;

    public HistorialVentaDTO(String folioVenta, LocalDateTime fechaHora, String tipoServicio, double total, String nombreCliente) {
        this.folioVenta = folioVenta; this.fechaHora = fechaHora;
        this.tipoServicio = tipoServicio; this.total = total; this.nombreCliente = nombreCliente;
    }

    public String getFolioVenta() { return folioVenta; }
    public void setFolioVenta(String folioVenta) { this.folioVenta = folioVenta; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}
