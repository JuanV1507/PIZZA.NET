// Variables globales
let carrito = [];

function cambiarCantidad(btn, delta) {
    const id = btn.getAttribute('data-id');
    const input = document.getElementById(`cantidad-${id}`);
    let valor = parseInt(input.value) + delta;
    if (valor < 1) valor = 1;
    if (valor > 99) valor = 99;
    input.value = valor;
}

function agregarAlCarrito(btn) {
    const id = btn.getAttribute('data-id');
    const nombre = btn.getAttribute('data-nombre');
    const precio = parseFloat(btn.getAttribute('data-precio'));
    const input = document.getElementById(`cantidad-${id}`);
    let cantidad = parseInt(input.value);

    // VALIDACIÓN CLAVE
    if (cantidad < 1) {
        cantidad = 1;
        input.value = 1;
    }

    const existente = carrito.find(item => item.id === id);

    if (existente) {
        existente.cantidad += cantidad;
    } else {
        carrito.push({
            id: id,
            nombre: nombre,
            precio: precio,
            cantidad: cantidad
        });
    }

    actualizarCarrito();

    // Reset opcional (UX PRO)
    input.value = 1;

    Swal.fire({
        icon: 'success',
        title: 'Agregado',
        text: `${nombre} x${cantidad} agregado`,
        timer: 1200,
        showConfirmButton: false,
        position: 'top-end',
        toast: true
    });
}

function cambiarCantidadCarrito(id, delta) {
    const item = carrito.find(p => p.id === id);
    if (!item) return;

    item.cantidad += delta;

    if (item.cantidad <= 0) {
        carrito = carrito.filter(p => p.id !== id);
    }

    actualizarCarrito();
}


function eliminarDelCarrito(id) {
    carrito = carrito.filter(item => item.id !== id);
    actualizarCarrito();
}

function actualizarCarrito() {
    const lista = document.getElementById('carrito-lista');
    const totalSpan = document.getElementById('total-carrito');
    const contador = document.getElementById('contador-items');
    
    if (carrito.length === 0) {
        lista.innerHTML = `
            <li class="text-center text-muted py-3">
                <i class="fas fa-shopping-basket fa-2x mb-2"></i>
                <p class="mb-0">Carrito vacío</p>
            </li>
        `;
        totalSpan.textContent = '$0.00';
        contador.textContent = '0';
        return;
    }
    
    let html = '';
    let total = 0;
    let totalItems = 0;
    
    carrito.forEach(item => {
        const subtotal = item.precio * item.cantidad;
        total += subtotal;
        totalItems += item.cantidad;
        
        html += `
            <li class="d-flex justify-content-between align-items-center py-2 border-bottom">
                <div style="flex: 2;">
                    <span class="small fw-semibold">${item.nombre}</span>
                    <span class="badge bg-secondary ms-1">x${item.cantidad}</span>
                </div>
                <div class="d-flex align-items-center gap-1">
                    <button class="btn btn-sm btn-warning py-0 px-2" onclick="cambiarCantidadCarrito('${item.id}', -1)" style="min-width: 28px;">
                        <i class="fas fa-minus"></i>
                    </button>
                    <span class="small fw-bold text-primary" style="min-width: 50px; text-align: center;">$${subtotal.toFixed(2)}</span>
                    <button class="btn btn-sm btn-success py-0 px-2" onclick="cambiarCantidadCarrito('${item.id}', 1)" style="min-width: 28px;">
                        <i class="fas fa-plus"></i>
                    </button>
                    <button class="btn btn-sm btn-danger py-0 px-2" onclick="eliminarDelCarrito('${item.id}')" style="min-width: 28px;">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </li>
        `;
    });
    
    lista.innerHTML = html;
    totalSpan.textContent = `$${total.toFixed(2)}`;
    contador.textContent = totalItems;
}


function actualizarValidaciones() {
    const tipo = document.querySelector('input[name="tipo-orden"]:checked').value;

    const nombre = document.getElementById('nombre-cliente');
    const telefono = document.getElementById('telefono-cliente');
    const direccion = document.getElementById('direccion');
    const mesa = document.getElementById('mesa-cliente');
    const referencias = document.getElementById('referencias');
    // Resetear TODO
    nombre.required = false;
    telefono.required = false;
    direccion.required = false;
    mesa.required = false;
    referencias.required = false;

    // Opcional: ocultar
    direccion.parentElement.style.display = "none";
    mesa.parentElement.style.display = "none";
    referencias.parentElement.style.display = "none";

    // Reglas
    if (tipo === "comedor") {
        nombre.required = true;
        mesa.required = true;

        mesa.parentElement.style.display = "block";
    }

    if (tipo === "esperando") {
        nombre.required = true;
    }

    if (tipo === "domicilio") {
        nombre.required = true;
        telefono.required = true;
        direccion.required = true;

        referencias.parentElement.style.display = "block";
        direccion.parentElement.style.display = "block";
    }
}
const radios = document.querySelectorAll('input[name="tipo-orden"]');

radios.forEach(radio => {
    radio.addEventListener("change", actualizarValidaciones);
});

document.addEventListener("DOMContentLoaded", actualizarValidaciones);

function confirmarVenta() {
    const nombre = document.getElementById('nombre-cliente').value;
    const telefono = document.getElementById('telefono-cliente').value;
    const tipoOrden = document.querySelector('input[name="tipo-orden"]:checked').value;
    const direccion = document.getElementById('direccion'); 
    const mesa = document.getElementById('mesa-cliente');
    const referencias = document.getElementById('referencias');
    
if (!nombre) {
    Swal.fire("Error", "El nombre es obligatorio", "warning");
    return;
}

if (tipoOrden === "domicilio") {
    if (!telefono) {
        Swal.fire("Error", "El teléfono es obligatorio para domicilio", "warning");
        return;
    }

    if (!direccion.value) {
        Swal.fire("Error", "La dirección es obligatoria", "warning");
        return;
    }
}

if (tipoOrden === "comedor") {
    if (!mesa.value) {
        Swal.fire("Error", "La mesa es obligatoria", "warning");
        return;
    }
}
    
    if (carrito.length === 0) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'warning',
                title: 'Carrito vacío',
                text: 'Agregue productos al carrito antes de confirmar',
                confirmButtonColor: '#f37a18'
            });
        } else {
            alert('Carrito vacío');
        }
        return;
    }

       if (tipoOrden === "domicilio") {
        fetch('/clientes/guardar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                telefono: telefono,
                nombre: nombre,
                direccion: direccion.value,
                direccion: direccion.value
            })
        });
    }
    
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'success',
            title: '¡Venta confirmada!',
            text: 'La orden se ha procesado correctamente',
            confirmButtonColor: '#28a745'
        }).then(() => {
            // Limpiar carrito
            carrito = [];
            actualizarCarrito();
            document.getElementById('nombre-cliente').value = '';
            document.getElementById('telefono-cliente').value = '';
            document.getElementById('mesa-cliente').value = '';
            document.getElementById('nota-cliente').value = '';
            document.getElementById('direccion').value = '';
            document.getElementById('referencias').value = '';
        });
    } else {
        // Fallback sin SweetAlert
        alert('Venta confirmada');
        carrito = [];
        actualizarCarrito();
        document.getElementById('nombre-cliente').value = '';
        document.getElementById('telefono-cliente').value = '';
        document.getElementById('nota-cliente').value = '';
        document.getElementById('mesa-cliente').value = '';
        document.getElementById('direccion').value = '';
        document.getElementById('referencias').value = '';
    }
}

document.getElementById("telefono-cliente").addEventListener("blur", function () {
    const telefono = this.value;

    if (telefono.length < 10) return;

   fetch(`/clientes/buscar?telefono=${telefono}`)
    .then(res => {
        if (!res.ok) {
            throw new Error("Cliente no encontrado");
        }
        return res.json();
    })
    .then(cliente => {
        console.log(cliente);

        document.getElementById("nombre-cliente").value = cliente.nombre || "";
        document.getElementById("direccion").value = cliente.direccion || "";
        document.getElementById("referencias").value = cliente.referencias || ""

        Swal.fire({
            icon: 'info',
            title: 'Cliente encontrado',
            message: 'Información cargada automáticamente',
            timer: 1400,
            showConfirmButton: false
        });
    })
    .catch(() => {
        //  Cliente no existe
        document.getElementById("nombre-cliente").value = "";
        document.getElementById("direccion").value = "";
        document.getElementById("referencias").value = ""

        Swal.fire({
            icon: 'warning',
            title: 'Cliente nuevo',
            message: 'Registre su información para futuras compras',
            timer: 1400,
            showConfirmButton: false
        });
    });
        
});