package com.Proyecto.Web.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.Proyecto.Web.Model.Productos;
import com.Proyecto.Web.Service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // LISTAR Y FORMULARIO EN LA MISMA VISTA
    @GetMapping
    public String listar(Model model) {

        var productos = productoService.listarTodos();

        model.addAttribute("productos", productos);
        model.addAttribute("producto", new Productos());
        model.addAttribute("categorias", Productos.Categoria.values());

        return "productos"; // <-- tu vista original
    }

    // GUARDAR PRODUCTO (NUEVO O EDITADO)
        @PostMapping("/guardar")
        public String guardarProducto(@ModelAttribute Productos producto,
                                    @RequestParam("imagenFile") MultipartFile imagenFile) {

            try {
                Productos productoExistente = null;

                // Si el producto tiene ID → es edición
                if (producto.getId_producto() != null) {
                    productoExistente = productoService.buscarPorId(producto.getId_producto()).orElse(null);
                }

                // SI ES EDICIÓN Y no se subió una imagen nueva → conservar la existente
                if (productoExistente != null && imagenFile.isEmpty()) {
                    producto.setImagen(productoExistente.getImagen());
                }

                // SI SUBE UNA IMAGEN NUEVA → guardarla
                if (!imagenFile.isEmpty()) {
                    String nombreArchivo = imagenFile.getOriginalFilename();

                    Path ruta = Paths.get("src/main/resources/static/uploads/" + nombreArchivo);
                    Files.createDirectories(ruta.getParent());
                    Files.write(ruta, imagenFile.getBytes());

                    producto.setImagen(nombreArchivo);
                }

                // Guardar (JPA detecta si es nuevo o update según el ID)
                productoService.guardar(producto);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "redirect:/productos";
        }


    // EDITAR PRODUCTO
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        var productos = productoService.listarTodos();

        model.addAttribute("productos", productos);
        model.addAttribute("producto", productoService.buscarPorId(id).orElse(new Productos()));
        model.addAttribute("categorias", Productos.Categoria.values());

        return "productos"; 
    }

    // ELIMINAR PRODUCTO
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}
