// Variables globales
let carrito = JSON.parse(localStorage.getItem('carritoPizzaNet')) || [];

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
    
    // Persistir en local storage
    localStorage.setItem('carritoPizzaNet', JSON.stringify(carrito));

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

document.addEventListener("DOMContentLoaded", () => {
    actualizarValidaciones();
    actualizarCarrito(); // Restaurar UI del carrito guardado
});

async function confirmarVenta() {
    const nombre = document.getElementById('nombre-cliente').value;
    const telefono = document.getElementById('telefono-cliente').value;
    const tipoOrden = document.querySelector('input[name="tipo-orden"]:checked').value;
    const direccion = document.getElementById('direccion').value; 
    const mesa = document.getElementById('mesa-cliente').value;
    const referencias = document.getElementById('referencias').value;
    
    if (!nombre) {
        Swal.fire("Error", "El nombre es obligatorio", "warning");
        return;
    }

    if (tipoOrden === "domicilio") {
        if (!telefono) {
            Swal.fire("Error", "El teléfono es obligatorio para domicilio", "warning");
            return;
        }

        if (!direccion) {
            Swal.fire("Error", "La dirección es obligatoria", "warning");
            return;
        }
    }

    if (tipoOrden === "comedor") {
        if (!mesa) {
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

    // Fetch Siguiente Folio
    let folioAsignado = "POR ASIGNAR";
    try {
        const response = await fetch('/ventas/siguiente-folio');
        if (response.ok) {
            const data = await response.json();
            folioAsignado = data.folio;
        }
    } catch(e) {
        console.error("Error obteniendo folio", e);
    }

    // Calcular Total
    let total = carrito.reduce((sum, item) => sum + (item.precio * item.cantidad), 0);

    // Llenar Modal
    const ahora = new Date();
    document.getElementById('ticket-fecha').textContent = ahora.toLocaleDateString();
    document.getElementById('ticket-hora').textContent = ahora.toLocaleTimeString();
    document.getElementById('ticket-folio').textContent = folioAsignado; 
    document.getElementById('ticket-cliente').textContent = nombre;
    document.getElementById('ticket-tipo').textContent = tipoOrden.toUpperCase();

    if (tipoOrden === 'domicilio') {
        document.getElementById('ticket-direccion-container').style.display = 'block';
        document.getElementById('ticket-direccion').textContent = direccion;
    } else {
        document.getElementById('ticket-direccion-container').style.display = 'none';
    }

    if (tipoOrden === 'comedor') {
        document.getElementById('ticket-mesa-container').style.display = 'block';
        document.getElementById('ticket-mesa').textContent = mesa;
    } else {
        document.getElementById('ticket-mesa-container').style.display = 'none';
    }

    let listHtml = '';
    carrito.forEach(item => {
        listHtml += `<li class="d-flex justify-content-between"><span>${item.cantidad}x ${item.nombre}</span><span>$${(item.precio * item.cantidad).toFixed(2)}</span></li>`;
    });
    document.getElementById('ticket-productos').innerHTML = listHtml;
    
    document.getElementById('ticket-total').textContent = "$" + total.toFixed(2);
    document.getElementById('ticket-total').dataset.val = total;

    // Resetear caja de pago
    document.getElementById('pago-input').value = '';
    document.getElementById('cambio-display').textContent = '$0.00';

    // Abrir Modal
    const modal = new bootstrap.Modal(document.getElementById('ticketModal'));
    modal.show();
}

function calcularCambio() {
    const total = parseFloat(document.getElementById('ticket-total').dataset.val);
    const pago = parseFloat(document.getElementById('pago-input').value);
    const cambioDisplay = document.getElementById('cambio-display');
    
    if (isNaN(pago) || pago < 0) {
        cambioDisplay.textContent = '$0.00';
        cambioDisplay.className = 'fs-4 fw-bold text-primary';
        return;
    }

    const cambio = pago - total;
    if (cambio >= 0) {
        cambioDisplay.textContent = "$" + cambio.toFixed(2);
        cambioDisplay.className = 'fs-4 fw-bold text-success';
    } else {
        cambioDisplay.textContent = "Faltan $" + Math.abs(cambio).toFixed(2);
        cambioDisplay.className = 'fs-4 fw-bold text-danger';
    }
}

async function procesarPagoYGuardar() {
    const total = parseFloat(document.getElementById('ticket-total').dataset.val);
    const pago = parseFloat(document.getElementById('pago-input').value);

    if (isNaN(pago) || pago < total) {
        Swal.fire("Pago Insuficiente", "El cliente debe pagar al menos el total de la cuenta.", "warning");
        return;
    }

    const btn = document.getElementById('btn-procesar-pago');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    const payload = {
        nombre: document.getElementById('nombre-cliente').value,
        telefono: document.getElementById('telefono-cliente').value,
        direccion: document.getElementById('direccion').value,
        mesa: document.getElementById('mesa-cliente').value,
        referencias: document.getElementById('referencias').value,
        nota: document.getElementById('nota-cliente').value,
        tipoOrden: document.querySelector('input[name="tipo-orden"]:checked').value,
        total: total,
        pago: pago,
        carrito: carrito
    };

    try {
        const response = await fetch('/ventas/guardar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            let errorMsg = "No se pudo guardar la venta en la base de datos.";
            try {
                const errData = await response.json();
                if(errData.error) errorMsg = errData.error;
            } catch(e) {}
            throw new Error(errorMsg);
        }
        
        const data = await response.json();
        
        Swal.fire({
            icon: 'success',
            title: '¡Venta Registrada!',
            text: 'El ticket ha sido enviado a la impresora del servidor.',
            timer: 2000,
            showConfirmButton: false
        }).then(() => {
            localStorage.removeItem('carritoPizzaNet'); // Limpiar el carrito solo si la venta fue exitosa
            window.location.reload();
        });

    } catch (error) {
        Swal.fire("Error", error.message, "error");
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-print me-2"></i>Procesar y Emitir Ticket';
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