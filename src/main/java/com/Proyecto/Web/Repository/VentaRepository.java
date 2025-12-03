package com.Proyecto.Web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

   Venta findByFolioVenta(String folioVenta);
    
}
