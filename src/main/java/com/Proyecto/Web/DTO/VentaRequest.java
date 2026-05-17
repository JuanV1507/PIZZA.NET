package com.Proyecto.Web.DTO;

public class VentaRequest {
    private double total;
    private Long id_cliente;
    private Long id_usuario;
    private String tipo_servicio;
    private java.util.List<DetalleRequest> detalles;

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Long getId_cliente() { return id_cliente; }
    public void setId_cliente(Long id_cliente) { this.id_cliente = id_cliente; }
    public Long getId_usuario() { return id_usuario; }
    public void setId_usuario(Long id_usuario) { this.id_usuario = id_usuario; }
    public String getTipo_servicio() { return tipo_servicio; }
    public void setTipo_servicio(String tipo_servicio) { this.tipo_servicio = tipo_servicio; }
    public java.util.List<DetalleRequest> getDetalles() { return detalles; }
    public void setDetalles(java.util.List<DetalleRequest> detalles) { this.detalles = detalles; }
}
