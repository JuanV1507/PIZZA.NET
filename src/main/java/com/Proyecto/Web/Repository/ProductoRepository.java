package com.Proyecto.Web.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Productos;

@Repository
public interface ProductoRepository extends JpaRepository<Productos, Long> {
   
    Productos findByNombre(String nombre);

    // FILTRO POR CATEGORÍA
    List<Productos> findByCategoria(Productos.Categoria categoria);

    // FILTRO POR DISPONIBILIDAD
    List<Productos> findByActivo(Boolean activo);


    // FILTRO COMBINADO (CATEGORÍA + DISPONIBILIDAD)
    List<Productos> findByCategoriaAndActivo(Productos.Categoria categoria, Boolean activo);

    // CATEGORÍAS ÚNICAS (para el select)
    @Query("SELECT DISTINCT p.categoria FROM Productos p")
    List<String> findCategoriasUnicas();

}
