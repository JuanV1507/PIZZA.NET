package com.Proyecto.Web.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Productos;

@Repository
public interface ProductoRepository extends JpaRepository<Productos, Long> {
   
    Productos findByNombre(String nombre);
}
