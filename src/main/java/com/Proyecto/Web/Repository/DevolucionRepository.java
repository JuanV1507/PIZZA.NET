package com.Proyecto.Web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Web.Model.Devolucion;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
}
