/*
 * ============================================================
 * ROLES.JS - ASIGNACIÓN DINÁMICA DE MÓDULOS CON CHECKBOXES
 * ============================================================
 */

function obtenerUsuarioActual() {
    const datos = localStorage.getItem("usuarioLogistic");
    if (!datos) return null;
    try {
        return JSON.parse(datos);
    } catch (error) {
        return null;
    }
}

function obtenerNombreRol(usuario) {
    if (!usuario) return "";
    if (usuario.rol && typeof usuario.rol === "object") {
        return String(usuario.rol.nombre || "").trim().toUpperCase();
    }
    if (typeof usuario.rol === "string") {
        return usuario.rol.trim().toUpperCase();
    }
    return "";
}

// 1. APLICAR PERMISOS AL MENÚ LATERAL Y PROTEGER PÁGINAS
function aplicarPermisosDinamicos() {
    const usuario = obtenerUsuarioActual();
    if (!usuario) return;

    const rol = obtenerNombreRol(usuario);
    const paginaActual = window.location.pathname.split("/").pop() || "dashboard.html";

    // Si es ADMIN, tiene acceso total y ve todo
    if (rol === "ADMIN") return;

    // Obtener los módulos guardados para este Rol
    let permisosGuardados = localStorage.getItem("permisos_rol_" + rol);
    let modulosPermitidos = permisosGuardados ? JSON.parse(permisosGuardados) : null;

    if (!modulosPermitidos) {
        modulosPermitidos = ["dashboard.html", "clientes.html", "inventario.html", "pedidos.html"];
    }

    // Proteger acceso
    if (paginaActual !== "login.html" && paginaActual !== "index.html") {
        if (!modulosPermitidos.includes(paginaActual)) {
            alert("No tienes permisos para acceder al módulo " + paginaActual);
            window.location.replace(modulosPermitidos[0] || "dashboard.html");
            return;
        }
    }

    // Ocultar del menú lo que no tenga permitido
    const itemsMenu = document.querySelectorAll(".sidebar-nav ul li a");
    itemsMenu.forEach(function(enlace) {
        const ruta = enlace.getAttribute("href");
        if (ruta && !modulosPermitidos.includes(ruta)) {
            enlace.parentElement.style.display = "none";
        }
    });
}

// 2. EN LA PANTALLA ROLES.HTML: SINCRONIZAR CHECKBOXES AL EDITAR
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(aplicarPermisosDinamicos, 100);

    const formRol = document.getElementById("formRol");
    if (!formRol) return;

    // Al enviar el formulario de roles, guarda los checkboxes marcados
    formRol.addEventListener("submit", function () {
        const inputNombre = document.getElementById("nombreRol");
        if (!inputNombre) return;
        const nombreRol = inputNombre.value.trim().toUpperCase();

        const modulos = [];
        document.querySelectorAll("input[name='permisoModulo']:checked").forEach(function (cb) {
            modulos.push(cb.value);
        });

        if (modulos.length > 0) {
            localStorage.setItem("permisos_rol_" + nombreRol, JSON.stringify(modulos));
        }
    });

    // Detectar cuando abren editar rol para marcar los checkboxes
    const tablaRoles = document.getElementById("tablaRoles");
    if (tablaRoles) {
        tablaRoles.addEventListener("click", function (e) {
            if (e.target && e.target.textContent.trim() === "Editar") {
                setTimeout(function () {
                    const inputNombre = document.getElementById("nombreRol");
                    if (!inputNombre) return;
                    const nombreRol = inputNombre.value.trim().toUpperCase();

                    const checkboxes = document.querySelectorAll("input[name='permisoModulo']");
                    checkboxes.forEach(cb => cb.checked = false);

                    const permisos = localStorage.getItem("permisos_rol_" + nombreRol);
                    if (permisos) {
                        const lista = JSON.parse(permisos);
                        checkboxes.forEach(cb => {
                            if (lista.includes(cb.value)) cb.checked = true;
                        });
                    } else {
                        checkboxes.forEach(cb => cb.checked = true);
                    }
                }, 200);
            }
        });
    }
});