package com.Proyecto.Web.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto.Web.Model.Clientes;
import com.Proyecto.Web.Repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Clientes findByTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }

    public Clientes guardar(Clientes cliente) {
    Optional<Clientes> existente = clienteRepository.findByTelefono(cliente.getTelefono());

    if (existente.isPresent()) {
        Clientes c = existente.get();

        c.setNombre(cliente.getNombre());
        c.setDireccion(cliente.getDireccion());
        c.setReferencias(cliente.getReferencias()); 

        return clienteRepository.save(c);
    }

    return clienteRepository.save(cliente);
}
    
}
