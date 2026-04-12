package com.Proyecto.Web.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Web.Model.Clientes;
import com.Proyecto.Web.Service.ClienteService;


@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/guardar")
    public Clientes guardarCliente(@RequestBody Clientes cliente) {
        
    return clienteService.guardar(cliente);
}

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorTelefono(@RequestParam String telefono) {
    Clientes cliente = clienteService.findByTelefono(telefono);

    if (cliente == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(cliente);
}
    
}
