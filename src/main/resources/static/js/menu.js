/*
 * ============================================================
 * MENU PRINCIPAL DEL SISTEMA LOGISTIC (MENU.JS)
 * ============================================================
 * Genera la barra de navegación lateral dinámicamente en todas las vistas.
 */

function cargarMenu() {
    const sidebar = document.getElementById("sidebar");
    if (!sidebar) {
        console.warn("No se encontró #sidebar en la página.");
        return;
    }

    const paginaActual = window.location.pathname.split("/").pop() || "dashboard.html";

    function activo(pagina) {
        return paginaActual === pagina ? "menu-activo" : "";
    }

    sidebar.innerHTML = `
        <div class="sidebar-brand">
            <h1>LOGISTIC</h1>
            <span>Gestión logística empresarial</span>
        </div>

        <nav class="sidebar-nav">
            <ul>
                <li class="${activo("dashboard.html")}">
                    <a href="dashboard.html">
                        <span class="menu-icon">🏠</span>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="${activo("inventario.html")}">
                    <a href="inventario.html">
                        <span class="menu-icon">📦</span>
                        <span>Inventarios</span>
                    </a>
                </li>
                <li class="${activo("productos.html")}">
                    <a href="productos.html">
                        <span class="menu-icon">🧾</span>
                        <span>Productos</span>
                    </a>
                </li>
                <li class="${activo("clientes.html")}">
                    <a href="clientes.html">
                        <span class="menu-icon">👥</span>
                        <span>Clientes</span>
                    </a>
                </li>
                <li class="${activo("pedidos.html")}">
                    <a href="pedidos.html">
                        <span class="menu-icon">🛒</span>
                        <span>Órdenes</span>
                    </a>
                </li>
                <li class="${activo("entregas.html")}">
                    <a href="entregas.html">
                        <span class="menu-icon">🚚</span>
                        <span>Entregas</span>
                    </a>
                </li>
                <li class="${activo("movimientos.html")}">
                    <a href="movimientos.html">
                        <span class="menu-icon">🔄</span>
                        <span>Movimientos</span>
                    </a>
                </li>
                <li id="menuUsuarios" class="${activo("usuarios.html")}">
                    <a href="usuarios.html">
                        <span class="menu-icon">👤</span>
                        <span>Usuarios</span>
                    </a>
                </li>
                <li id="menuRoles" class="${activo("roles.html")}">
                    <a href="roles.html">
                        <span class="menu-icon">🔐</span>
                        <span>Roles</span>
                    </a>
                </li>
                <li class="${activo("reportes.html")}">
                    <a href="reportes.html">
                        <span class="menu-icon">📊</span>
                        <span>Reportes</span>
                    </a>
                </li>
            </ul>
        </nav>

        <!-- PIE DEL SIDEBAR: CAMBIAR CLAVE Y CERRAR SESIÓN -->
        <div class="sidebar-footer">
            <button type="button" onclick="cambiarMiPassword()" style="margin-bottom: 8px;">
                <span class="menu-icon">🔑</span>
                <span>Cambiar clave</span>
            </button>
            <button type="button" onclick="cerrarSesion()">
                <span class="menu-icon">🚪</span>
                <span>Cerrar sesión</span>
            </button>
        </div>
    `;
}

document.addEventListener("DOMContentLoaded", function () {
    cargarMenu();
});