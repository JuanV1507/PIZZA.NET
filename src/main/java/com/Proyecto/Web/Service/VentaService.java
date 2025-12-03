package com.Proyecto.Web.Service;

import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Venta;
import com.Proyecto.Web.Repository.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public Venta buscarPorFolio(String folioVenta) {
        return ventaRepository.findByFolioVenta(folioVenta);
    }

    


    
}
