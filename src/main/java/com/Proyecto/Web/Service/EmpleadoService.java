package com.Proyecto.Web.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Empleado;
import com.Proyecto.Web.Repository.EmpleadoRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public Optional<Empleado> buscarPorId(Long id) {
        return empleadoRepository.findById(id);
    }

    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }
}
