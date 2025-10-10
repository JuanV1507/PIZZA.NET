package com.Proyecto.Web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Aquí puedes agregar métodos personalizados si los necesitas
    Empleado findByTelefono(String telefono);
}
