
    // Mostrar mensaje cuando el usuario fue creado correctamente
    const url2 = new URL(window.location.href);
    const usuarioCreado = url2.searchParams.get("usuarioCreado");

    if (usuarioCreado === "ok") {
        Swal.fire({
            title: "Usuario creado",
            text: "El usuario se registró correctamente.",
            icon: "success",
            confirmButtonText: "Aceptar"
        });
    }


document.addEventListener("DOMContentLoaded", () => {
    const success = document.getElementById("success")?.value;
    const error = document.getElementById("error")?.value;

    if (success) {
        Swal.fire({
            icon: "success",
            title: "Éxito",
            text: success
        });
    }

    if (error) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error
        });
    }
});

    // Capturar el parámetro crearUsuario
    const url = new URL(window.location.href);
    const idEmpleado = url.searchParams.get("crearUsuario");

    if (idEmpleado) {

        Swal.fire({
            title: "Empleado guardado",
            text: "¿Deseas crear un usuario para este empleado?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Sí, crear usuario",
            cancelButtonText: "No"
        }).then((result) => {

            if (result.isConfirmed) {

                Swal.fire({
                    title: "Crear Usuario",
                    html: `
        <form id="formUsuario" method="POST" action="/usuarios/guardar">
            <input type="hidden" name="id_empleado" value="${idEmpleado}">

            <label class="swal-label">Usuario:</label>
            <input type="text" name="username" class="swal2-input swal-input-custom" required>

            <label class="swal-label">Rol:</label>
            <select id="rol" name="rol" class="swal2-input swal-input-custom" required></select>

            <label class="swal-label">Contraseña:</label>
            <input type="password" name="contrasena" class="swal2-input swal-input-custom" required>
        </form>
    `,
                    didOpen: () => {
                        let select = document.getElementById("rol");
                        rolesDisponibles.forEach(r => {
                            let option = document.createElement("option");
                            option.value = r;
                            option.textContent = r;
                            select.appendChild(option);
                        });
                    },
                    showCancelButton: true,
                    confirmButtonText: "Guardar",
                    cancelButtonText: "Cancelar",
                    preConfirm: () => {
                        document.getElementById("formUsuario").submit();
                    }
                });


            }
        });
    }

        document.addEventListener('DOMContentLoaded', () => {
            const hamburger = document.querySelector('.hamburger');
            const sidebar = document.querySelector('.sidebar');

            hamburger.addEventListener('click', () => {
                sidebar.classList.toggle('menu-open');
            });
        });
