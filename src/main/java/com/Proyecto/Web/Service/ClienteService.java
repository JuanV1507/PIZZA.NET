package com.Proyecto.Web.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Clientes;
import com.Proyecto.Web.Repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Clientes> listarTodos() {
        return clienteRepository.findAll().stream()
                .filter(c -> c.getTelefono() != null && !c.getTelefono().trim().toUpperCase().contains("SIN_TEL"))
                .filter(c -> c.getDireccion() == null || !c.getDireccion().trim().toUpperCase().contains("SIN DIRECCION"))
                .collect(Collectors.toList());
    }

    public Clientes findByTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }

    public List<Clientes> buscarPorNombreOTelefono(String texto) {
        String q = texto.toLowerCase();
        return listarTodos().stream()
                .filter(c -> (c.getNombre()  != null && c.getNombre().toLowerCase().contains(q))
                          || (c.getTelefono() != null && c.getTelefono().contains(q)))
                .collect(Collectors.toList());
    }

    public Clientes guardar(Clientes cliente) {
        // Si tiene ID, es edición directa
        if (cliente.getId_cliente() != null) {
            if (cliente.getTelefono() != null && !cliente.getTelefono().isBlank()) {
                Optional<Clientes> existente = clienteRepository.findByTelefono(cliente.getTelefono().trim());
                if (existente.isPresent() && !existente.get().getId_cliente().equals(cliente.getId_cliente())) {
                    throw new IllegalArgumentException("El número de teléfono ya se encuentra registrado con otro cliente.");
                }
            }
            return clienteRepository.save(cliente);
        }
        // Si no tiene ID, es cliente nuevo: buscar por teléfono para no duplicar
        if (cliente.getTelefono() != null && !cliente.getTelefono().isBlank()) {
            Optional<Clientes> existente = clienteRepository.findByTelefono(cliente.getTelefono().trim());
            if (existente.isPresent()) {
                throw new IllegalArgumentException("El número de teléfono ya se encuentra registrado con otro cliente.");
            }
        }
        // Asignar fecha y hora de registro automáticamente si es nuevo
        cliente.setFechaRegistro(java.time.LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    public Optional<Clientes> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
}
