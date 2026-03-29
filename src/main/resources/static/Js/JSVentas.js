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
    const cantidad = parseInt(document.getElementById(`cantidad-${id}`).value);
    
    // Buscar si ya existe en el carrito
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
    
    // SweetAlert de confirmación (si tienes SweetAlert2)
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'success',
            title: 'Agregado',
            text: `${nombre} x${cantidad} agregado al carrito`,
            timer: 1500,
            showConfirmButton: false,
            position: 'top-end',
            toast: true
        });
    }
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
                <div>
                    <span class="small fw-semibold">${item.nombre}</span>
                    <span class="badge bg-secondary ms-1">x${item.cantidad}</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="small fw-bold text-primary">$${subtotal.toFixed(2)}</span>
                    <button class="btn btn-sm btn-danger py-0 px-1" onclick="eliminarDelCarrito('${item.id}')">
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

function confirmarVenta() {
    const nombre = document.getElementById('nombre-cliente').value;
    const telefono = document.getElementById('telefono-cliente').value;
    const tipoOrden = document.querySelector('input[name="tipo-orden"]:checked').value;
    
    if (!nombre || !telefono) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'warning',
                title: 'Campos incompletos',
                text: 'Por favor complete el nombre y teléfono del cliente',
                confirmButtonColor: '#f37a18'
            });
        } else {
            alert('Complete nombre y teléfono');
        }
        return;
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
            document.getElementById('nota-cliente').value = '';
        });
    } else {
        // Fallback sin SweetAlert
        alert('Venta confirmada');
        carrito = [];
        actualizarCarrito();
        document.getElementById('nombre-cliente').value = '';
        document.getElementById('telefono-cliente').value = '';
        document.getElementById('nota-cliente').value = '';
    }
}