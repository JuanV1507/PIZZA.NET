package com.Proyecto.Web.Model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // clave primaria auto incrementable
    private Long id_empleado;

    @Column(nullable=false, length=100)
    private String nombres;

    @Column(nullable=false, length=50)
    private String puesto;

    private Double pagoDiario;

    private int diasTrabajados;

    private Double salario_calculado;

    private String telefono;

    @Column(name="fecha_registro", updatable=false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaRegistro;


    //Método para calcular el salario automáticamente
    @PrePersist
    @PreUpdate
    public void calcularSalario() {
        this.salario_calculado = this.pagoDiario * this.diasTrabajados;
    }

    // Getters y setters
    public Long getId_empleado() { return id_empleado; }
    public void setId_empleado(Long id_empleado) { this.id_empleado = id_empleado; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }


    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public Double getSalario_calculado() { return salario_calculado; }
    public void setSalario_calculado(Double salario_calculado) { this.salario_calculado = salario_calculado; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Double getPagoDiario() { return pagoDiario; }
    public void setPagoDiario(Double pagoDiario) { this.pagoDiario = pagoDiario; }

    public int getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(int diasTrabajados) { this.diasTrabajados = diasTrabajados; }

}