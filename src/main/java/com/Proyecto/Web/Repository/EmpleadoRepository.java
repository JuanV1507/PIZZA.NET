package com.Proyecto.Web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
   
    Empleado findByTelefono(String telefono);
    //boolean existsByTelefono(String telefono);
    boolean existsByTelefonoAndIdEmpleadoNot(String telefono, Long idEmpleado);
    
}
