package com.Proyecto.Web.Model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

   @Column(name = "folio_venta")
   private String folioVenta;


    private int id_cliente;

    private int id_usuario;

    // ENUMS
    public enum tipo_servicio {
        COMEDOR,
        ESPERANDO,
        DOMICILIO
    }

    public enum estado {
        ACTIVA,
        CANCELADA
    }

    // CAMPOS EN BD (USAMOS COLUMN NAME PARA EVITAR DUPLICADOS)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio")
    private tipo_servicio tipo_servicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private estado estado;

    @Column(name = "fecha", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private double total;

    // GETTERS Y SETTERS
    public Long getId_venta() {
        return id_venta;
    }

    public void setId_venta(Long id_venta) {
        this.id_venta = id_venta;
    }

    public String getFolioVenta() {
        return folioVenta;
    }
    public void setFolioVenta(String folioVenta) {
        this.folioVenta = folioVenta;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public tipo_servicio getTipo_servicio() {
        return tipo_servicio;
    }

    public void setTipo_servicio(tipo_servicio tipo_servicio) {
        this.tipo_servicio = tipo_servicio;
    }

    public estado getEstado() {
        return estado;
    }

    public void setEstado(estado estado) {
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
