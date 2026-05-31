package com.Proyecto.Web.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.DetalleVenta;
import com.Proyecto.Web.Model.Venta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    @Query("SELECT d.id_producto, SUM(d.cantidad) FROM DetalleVenta d WHERE d.id_venta.fecha BETWEEN :inicio AND :fin GROUP BY d.id_producto ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> topProductosPorPeriodo(@Param("inicio") java.time.LocalDate inicio, @Param("fin") java.time.LocalDate fin);

    @Query("SELECT d FROM DetalleVenta d WHERE d.id_venta IN :ventas")
    List<DetalleVenta> findByVentas(@Param("ventas") List<Venta> ventas);
}
