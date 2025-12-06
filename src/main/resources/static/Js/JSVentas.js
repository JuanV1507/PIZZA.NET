
function activarControles(boton) {
    // Si ya tiene controles, no hacer nada
    if (boton.nextElementSibling && boton.nextElementSibling.classList.contains("cantidad-control")) {
        return;
    }

    // Crear contenedor de cantidad
    const control = document.createElement("div");
    control.classList.add("cantidad-control");

    // Para leer los datos del producto
    const id = boton.dataset.id;
    const nombre = boton.dataset.nombre;
    const precio = boton.dataset.precio;

    // Crear botones e input
    control.innerHTML = `
        <button class="btn-cantidad btn-menos">−</button>
        <input type="number" class="cantidad-input" value="1" min="0" readonly>
        <button class="btn-cantidad btn-mas">+</button>
    `;

    // Insertar después del botón
    boton.insertAdjacentElement("afterend", control);

    // Eventos
    const input = control.querySelector(".cantidad-input");
    const btnMas = control.querySelector(".btn-mas");
    const btnMenos = control.querySelector(".btn-menos");

    btnMas.onclick = () => {
        input.value = parseInt(input.value) + 1;
    };

    btnMenos.onclick = () => {
        const nuevaCantidad = parseInt(input.value) - 1;
        if (nuevaCantidad <= 0) {
            control.remove();          // quitar controles
            boton.style.display = "";  // mostrar botón otra vez
        } else {
            input.value = nuevaCantidad;
        }
    }; 

    // Ocultar el botón agregar inicial
    boton.style.display = "none";
}
