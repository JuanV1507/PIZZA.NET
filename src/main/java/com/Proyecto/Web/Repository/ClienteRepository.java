package com.Proyecto.Web.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Clientes;

@Repository
public interface ClienteRepository extends JpaRepository <Clientes, Long> {
    Optional<Clientes> findByTelefono(String telefono);
    

    
}
