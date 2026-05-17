package com.Proyecto.Web.DTO;

import java.util.List;

public class VentaRequestDTO {
    private String nombre;
    private String telefono;
    private String direccion;
    private String mesa;
    private String referencias;
    private String nota;
    private String tipoOrden; // "comedor", "esperando", "domicilio"
    private double total;
    private double pago;
    private List<DetalleVentaRequestDTO> carrito;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }
    
    public String getReferencias() { return referencias; }
    public void setReferencias(String referencias) { this.referencias = referencias; }
    
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    
    public String getTipoOrden() { return tipoOrden; }
    public void setTipoOrden(String tipoOrden) { this.tipoOrden = tipoOrden; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public double getPago() { return pago; }
    public void setPago(double pago) { this.pago = pago; }
    
    public List<DetalleVentaRequestDTO> getCarrito() { return carrito; }
    public void setCarrito(List<DetalleVentaRequestDTO> carrito) { this.carrito = carrito; }
}
