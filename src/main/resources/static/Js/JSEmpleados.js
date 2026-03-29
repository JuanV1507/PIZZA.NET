document.addEventListener("DOMContentLoaded", () => {

    const hamburger = document.querySelector('.hamburger');
    const sidebar = document.querySelector('.sidebar');

    const url = new URL(window.location.href);

    const usuarioCreado = url.searchParams.get("usuarioCreado");
    const idEmpleado = url.searchParams.get("crearUsuario");

    const success = document.getElementById("success")?.value;
    const error = document.getElementById("error")?.value;

    // =============================
    // MENSAJES GENERALES
    // =============================

    if (usuarioCreado === "ok") {
        Swal.fire({
            title: "Usuario creado",
            text: "El usuario se registró correctamente.",
            icon: "success"
        });
    }

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

    // =============================
    // CREAR USUARIO (FIX REAL)
    // =============================

    if (idEmpleado) {

        console.log("crearUsuario:", idEmpleado);

        // delay pequeño para evitar conflicto con otros alerts
        setTimeout(() => {

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
                <form id="formUsuario" class="form-swal" method="POST" action="/usuarios/guardar" enctype="multipart/form-data">

                    <input type="hidden" name="id_empleado" value="${idEmpleado}">

                    <div class="form-group">
                        <label>Usuario</label>
                        <input type="text" name="username" id="username" required minlength="4">
                        <small id="userError" class="error-text"></small>
                    </div>

                    <div class="form-group">
                        <label>Rol</label>
                        <select id="rol" name="rol" required></select>
                    </div>

                    <div class="form-group">
                        <label>Contraseña</label>
                        <input type="password" name="contrasena" id="contrasena" required minlength="8">
                        <small id="passError" class="error-text"></small>
                    </div>

                    <div class="form-group">
                        <label>Correo Electrónico</label>
                        <input type="email" name="correo" id="correo" required>
                        <small id="emailError" class="error-text"></small>
                    </div>

                    <div class="form-group">
                        <label>Foto</label>
                        <input type="file" name="archivoFoto" id="fotoInput">
                    </div>

                </form>
                `,
                        showCancelButton: true,
                        confirmButtonText: "Guardar",
                        focusConfirm: false,
                        preConfirm: () => {

                            const username = document.getElementById("username").value.trim();
                            const password = document.getElementById("contrasena").value.trim();

                            const userError = document.getElementById("userError");
                            const passError = document.getElementById("passError");

                            let valido = true;

                            // Reset errores
                            userError.textContent = "";
                            passError.textContent = "";

                            // VALIDAR USUARIO
                            if (username.length < 4) {
                                userError.textContent = "Mínimo 4 caracteres";
                                valido = false;
                            }

                            // VALIDAR PASSWORD
                            if (password.length < 8) {
                                passError.textContent = "Mínimo 8 caracteres";
                                valido = false;
                            }

                            if (!valido) {
                                return false; // DETIENE el submit
                            }

                            // enviar form
                            document.getElementById("formUsuario").submit();
                        },

                        didOpen: () => {
                            let select = document.getElementById("rol");

                            if (typeof rolesDisponibles !== "undefined") {
                                rolesDisponibles.forEach(r => {
                                    let option = document.createElement("option");
                                    option.value = r;
                                    option.textContent = r;
                                    select.appendChild(option);
                                });
                            }
                        }
                    });
                }

                // LIMPIAR URL
                window.history.replaceState({}, document.title, "/empleados");

            });

        }, 300); //  DELAY
    }

    // =============================
    // MENU
    // =============================

    hamburger?.addEventListener('click', () => {
        sidebar?.classList.toggle('menu-open');
    });

});