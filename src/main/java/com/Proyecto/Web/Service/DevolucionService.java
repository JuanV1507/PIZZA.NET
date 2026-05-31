package com.Proyecto.Web.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Web.Model.Devolucion;
import com.Proyecto.Web.Model.Venta;
import com.Proyecto.Web.Repository.DevolucionRepository;
import com.Proyecto.Web.Repository.VentaRepository;

@Service
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;

    @Autowired
    private VentaRepository ventaRepository;

    public DevolucionService(DevolucionRepository devolucionRepository) {
        this.devolucionRepository = devolucionRepository;
    }

    public List<Devolucion> listarTodas() {
        return devolucionRepository.findAll();
    }

    /**
     * Guarda la devolución simple (compatibilidad con método anterior).
     */
    @Transactional
    public Devolucion guardar(Devolucion devolucion) {
        return devolucionRepository.save(devolucion);
    }

    /**
     * Busca la venta de referencia por folio.
     */
    public Venta buscarVentaPorFolio(String folio) {
        return ventaRepository.findByFolioVenta(folio);
    }

    /**
     * Procesa una devolución completa:
     * 1. Rellena los datos de la devolución desde la venta.
     * 2. Guarda la devolución.
     * 3. Marca la venta original como CANCELADA para excluirla de reportes.
     */
    @Transactional
    public Devolucion procesarDevolucion(Devolucion devolucion, String folioVenta) {
        // Cargar la venta de referencia
        Venta venta = ventaRepository.findByFolioVenta(folioVenta);
        if (venta == null) {
            throw new IllegalArgumentException("No se encontró ninguna venta con el folio: " + folioVenta);
        }
        if (venta.getEstado() == Venta.estado.CANCELADA) {
            throw new IllegalStateException("La venta " + folioVenta + " ya fue cancelada anteriormente.");
        }

        // Completar datos de la devolución
        devolucion.setFolioVentaReferencia(folioVenta);
        devolucion.setTipoServicio(
            venta.getTipo_servicio() != null ? venta.getTipo_servicio().name() : "COMEDOR"
        );

        // Guardar la devolución
        Devolucion saved = devolucionRepository.save(devolucion);

        // Marcar la venta original como CANCELADA
        venta.setEstado(Venta.estado.CANCELADA);
        ventaRepository.save(venta);

        return saved;
    }
}
