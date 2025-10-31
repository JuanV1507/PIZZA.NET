package com.Proyecto.Web.Service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List <Productos> listarTodos() {
        return productoRepository.findAll();
    }
    
    public Productos guardar(Productos producto) {
        return productoRepository.save(producto);
    }   

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public Productos buscarPorNombre(String nombre) {
        return productoRepository.findAll()
            .stream()
            .filter(p -> p.getNombre() != null && p.getNombre().equalsIgnoreCase(nombre))
            .findFirst()
            .orElse(null);
    }

    public java.util.Optional<Productos> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }
    
}
