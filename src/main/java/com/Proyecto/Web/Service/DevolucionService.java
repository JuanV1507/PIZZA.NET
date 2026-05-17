package com.Proyecto.Web.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Web.Model.Devolucion;
import com.Proyecto.Web.Repository.DevolucionRepository;

@Service
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;

    public DevolucionService(DevolucionRepository devolucionRepository) {
        this.devolucionRepository = devolucionRepository;
    }

    public List<Devolucion> listarTodas() {
        return devolucionRepository.findAll();
    }

    @Transactional
    public Devolucion guardar(Devolucion devolucion) {
        return devolucionRepository.save(devolucion);
    }
}
