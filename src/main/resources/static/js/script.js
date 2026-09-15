// ==========================================================
// SOFTWARE LOGISTIC
// JAVASCRIPT PRINCIPAL DEL FRONTEND
// ==========================================================

const API_URL = "";

async function leerRespuesta(respuesta) {
    const texto = await respuesta.text();
    if (!texto) return null;
    try {
        return JSON.parse(texto);
    } catch (error) {
        return texto;
    }
}

// ==========================================================
// LOGIN
// ==========================================================
const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", async function(event) {
        event.preventDefault();
        const correo = document.getElementById("usuario").value.trim();
        const password = document.getElementById("password").value;

        if (!correo || !password) {
            alert("Ingrese correo y contraseña.");
            return;
        }

        const boton = loginForm.querySelector("button");
        boton.disabled = true;
        boton.textContent = "Ingresando...";

        try {
            const respuesta = await fetch(API_URL + "/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ correo: correo, password: password })
            });

            const datos = await leerRespuesta(respuesta);

            if (respuesta.ok) {
                localStorage.setItem("usuarioLogistic", JSON.stringify(datos));
                alert("Inicio de sesión exitoso.");
                window.location.href = "/dashboard.html";
                return;
            }

            if (respuesta.status === 401) {
                alert("Correo o contraseña incorrectos.");
                return;
            }

            if (respuesta.status === 403) {
                alert("El usuario está inactivo.");
                return;
            }

            alert(typeof datos === "string" ? datos : "No fue posible iniciar sesión.");
        } catch (error) {
            console.error("Error de conexión:", error);
            alert("Error de conexión con el servidor.");
        } finally {
            boton.disabled = false;
            boton.textContent = "Ingresar al sistema";
        }
    });
}

function cerrarSesion() {
    localStorage.removeItem("usuarioLogistic");
    window.location.href = "/login.html";
}

const paginaActual = window.location.pathname.split("/").pop();
const usuarioSesion = localStorage.getItem("usuarioLogistic");
const paginasProtegidas = [
    "dashboard.html", "inventario.html", "productos.html",
    "clientes.html", "pedidos.html", "entregas.html",
    "movimientos.html", "usuarios.html", "roles.html", "reportes.html"
];

if (paginasProtegidas.includes(paginaActual) && !usuarioSesion) {
    window.location.href = "/login.html";
}

const usuarioNombre = document.getElementById("usuarioNombre");
if (usuarioNombre && usuarioSesion) {
    try {
        const usuario = JSON.parse(usuarioSesion);
        usuarioNombre.textContent = usuario.nombre || "Usuario";
    } catch (error) {
        console.error("Error leyendo sesión:", error);
    }
}

if (localStorage.getItem("logistic_alto_contraste") === "true") {
    document.body.classList.add("alto-contraste");
}

const botonesContraste = document.querySelectorAll("#contraste-btn");
botonesContraste.forEach(function(boton) {
    boton.addEventListener("click", function() {
        document.body.classList.toggle("alto-contraste");
        const activo = document.body.classList.contains("alto-contraste");
        localStorage.setItem("logistic_alto_contraste", activo ? "true" : "false");
    });
});

// ==========================================================
// INVENTARIO (CON BUSCADOR, FILTROS Y KPIS EN TIEMPO REAL)
// ==========================================================
const tablaInventario = document.getElementById("tablaInventario");
const btnNuevoInventario = document.getElementById("btnNuevoInventario");
const formularioInventario = document.getElementById("formularioInventario");
const formInventario = document.getElementById("formInventario");
const btnCancelarInventario = document.getElementById("btnCancelarInventario");
const btnActualizarInventario = document.getElementById("btnActualizarInventario");
const buscadorInventario = document.getElementById("buscadorInventario");
const filtroEstadoInventario = document.getElementById("filtroEstadoInventario");

// Memoria para el buscador
let inventariosMemoria = [];
let mapaProductosMemoria = new Map();

async function cargarProductosParaFormulario() {
    const select = document.getElementById("idProducto");
    if (!select) return;

    try {
        const respuesta = await fetch(API_URL + "/api/productos");
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        const productos = await respuesta.json();
        select.innerHTML = `<option value="">Seleccione un producto</option>`;

        productos.forEach(function(producto) {
            const opcion = document.createElement("option");
            opcion.value = producto.idProducto;
            opcion.textContent = `${producto.codigo} - ${producto.nombre}`;
            select.appendChild(opcion);
        });
    } catch (error) {
        console.error("Error cargando productos:", error);
    }
}

async function cargarInventario() {
    if (!tablaInventario) return;

    try {
        tablaInventario.innerHTML = `<tr><td colspan="7">Cargando existencias...</td></tr>`;

        const [respInv, respProd] = await Promise.all([
            fetch(API_URL + "/api/inventario"),
            fetch(API_URL + "/api/productos")
        ]);

        if (!respInv.ok || !respProd.ok) throw new Error("Error consultando datos de inventario.");

        inventariosMemoria = await respInv.json();
        const productos = await respProd.json();

        mapaProductosMemoria = new Map();
        productos.forEach(p => mapaProductosMemoria.set(p.idProducto, p));

        // Calcular y actualizar tarjetas KPI
        actualizarKpisInventario();

        // Renderizar la tabla con o sin filtros
        renderizarTablaInventario(inventariosMemoria);

    } catch (error) {
        console.error("Error cargando inventario:", error);
        tablaInventario.innerHTML = `<tr><td colspan="7">No fue posible cargar el inventario.</td></tr>`;
    }
}

function actualizarKpisInventario() {
    let totalUnidades = 0;
    let disponibles = 0;
    let bajos = 0;
    let agotados = 0;

    inventariosMemoria.forEach(inv => {
        const cantidad = Number(inv.cantidad || 0);
        const producto = mapaProductosMemoria.get(inv.idProducto);
        const stockMinimo = producto ? (producto.stockMinimo ?? 0) : 0;

        totalUnidades += cantidad;

        if (cantidad <= 0) {
            agotados++;
        } else if (cantidad <= stockMinimo) {
            bajos++;
        } else {
            disponibles++;
        }
    });

    const elTotal = document.getElementById("kpiTotalUnidades");
    const elDisp = document.getElementById("kpiDisponibles");
    const elBajo = document.getElementById("kpiStockBajo");
    const elAgot = document.getElementById("kpiAgotados");

    if (elTotal) elTotal.textContent = totalUnidades.toLocaleString("es-CO");
    if (elDisp) elDisp.textContent = disponibles;
    if (elBajo) elBajo.textContent = bajos;
    if (elAgot) elAgot.textContent = agotados;
}

function renderizarTablaInventario(lista) {
    if (!tablaInventario) return;
    tablaInventario.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaInventario.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron registros de inventario.</td></tr>`;
        return;
    }

    lista.forEach(function(inventario) {
        const producto = mapaProductosMemoria.get(inventario.idProducto);
        const nombre = producto ? producto.nombre : "Producto no encontrado";
        const codigo = producto ? producto.codigo : "N/A";
        const categoria = producto && producto.categoria ? producto.categoria.nombre : "Sin categoría";
        const stockMinimo = producto ? (producto.stockMinimo ?? 0) : 0;
        const cantidad = inventario.cantidad ?? 0;

        let estado = "Disponible";
        let claseEstado = "estado-disponible";

        if (cantidad <= 0) {
            estado = "Agotado";
            claseEstado = "estado-agotado";
        } else if (cantidad <= stockMinimo) {
            estado = "Stock bajo";
            claseEstado = "estado-bajo";
        }

        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${inventario.idInventario}</td>
            <td><strong>${nombre}</strong><br><small style="color: #64748b;">${categoria}</small></td>
            <td><code>${codigo}</code></td>
            <td><strong style="font-size: 1.05rem;">${cantidad}</strong></td>
            <td>${stockMinimo}</td>
            <td><span class="estado-stock ${claseEstado}">● ${estado}</span></td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarInventario(${inventario.idInventario}, ${cantidad})">
                    Editar
                </button>
                <button type="button" class="btn-tabla" style="background:#ef4444; color:white;" onclick="eliminarInventario(${inventario.idInventario})">
                    Eliminar
                </button>
            </td>
        `;
        tablaInventario.appendChild(fila);
    });
}

// BUSCADOR EN TIEMPO REAL Y FILTRO
function filtrarInventarioEnPantalla() {
    const texto = buscadorInventario ? buscadorInventario.value.toLowerCase().trim() : "";
    const filtroEstado = filtroEstadoInventario ? filtroEstadoInventario.value : "TODOS";

    const filtrados = inventariosMemoria.filter(inv => {
        const producto = mapaProductosMemoria.get(inv.idProducto);
        const nombre = producto ? producto.nombre.toLowerCase() : "";
        const codigo = producto ? producto.codigo.toLowerCase() : "";
        const cantidad = inv.cantidad ?? 0;
        const stockMinimo = producto ? (producto.stockMinimo ?? 0) : 0;

        // Estado calculado
        let estado = "Disponible";
        if (cantidad <= 0) estado = "Agotado";
        else if (cantidad <= stockMinimo) estado = "Stock bajo";

        const coincideTexto = nombre.includes(texto) || codigo.includes(texto);
        const coincideEstado = filtroEstado === "TODOS" || estado === filtroEstado;

        return coincideTexto && coincideEstado;
    });

    renderizarTablaInventario(filtrados);
}

if (buscadorInventario) {
    buscadorInventario.addEventListener("input", filtrarInventarioEnPantalla);
}

if (filtroEstadoInventario) {
    filtroEstadoInventario.addEventListener("change", filtrarInventarioEnPantalla);
}

async function editarInventario(id, cantidadActual) {
    const nuevaCantidad = prompt("Ingrese la nueva cantidad en existencia:", cantidadActual);
    if (nuevaCantidad === null) return;

    const cantidad = Number(nuevaCantidad);
    if (isNaN(cantidad) || cantidad < 0) {
        alert("Ingrese una cantidad válida.");
        return;
    }

    try {
        const respuesta = await fetch(API_URL + "/api/inventario/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ cantidad: cantidad })
        });

        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        alert("Existencias actualizadas correctamente.");
        cargarInventario();
        if (typeof cargarDashboard === "function") cargarDashboard();
    } catch (error) {
        console.error("Error actualizando inventario:", error);
        alert("No fue posible actualizar el inventario.");
    }
}

async function eliminarInventario(id) {
    if (!confirm("¿Está seguro de eliminar este registro del inventario?")) return;

    try {
        const respuesta = await fetch(API_URL + "/api/inventario/" + id, { method: "DELETE" });
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        alert("Registro eliminado correctamente.");
        cargarInventario();
        if (typeof cargarDashboard === "function") cargarDashboard();
    } catch (error) {
        console.error("Error eliminando inventario:", error);
        alert("No fue posible eliminar el registro.");
    }
}

if (btnNuevoInventario && formularioInventario) {
    btnNuevoInventario.addEventListener("click", function() {
        formularioInventario.style.display = "block";
        formularioInventario.scrollIntoView({ behavior: "smooth" });
        cargarProductosParaFormulario();
    });
}

if (btnCancelarInventario && formularioInventario) {
    btnCancelarInventario.addEventListener("click", function() {
        formularioInventario.style.display = "none";
        if (formInventario) formInventario.reset();
    });
}

if (formInventario) {
    formInventario.addEventListener("submit", async function(event) {
        event.preventDefault();
        const idProducto = Number(document.getElementById("idProducto").value);
        const cantidad = Number(document.getElementById("cantidad").value);

        if (!idProducto || isNaN(cantidad) || cantidad < 0) {
            alert("Complete correctamente los datos.");
            return;
        }

        try {
            const respuesta = await fetch(API_URL + "/api/inventario", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ idProducto: idProducto, cantidad: cantidad })
            });

            if (!respuesta.ok) {
                const mensaje = await leerRespuesta(respuesta);
                throw new Error(mensaje || "Error HTTP: " + respuesta.status);
            }

            alert("Inventario registrado correctamente.");
            formInventario.reset();
            formularioInventario.style.display = "none";
            cargarInventario();
            if (typeof cargarDashboard === "function") cargarDashboard();
        } catch (error) {
            console.error("Error creando inventario:", error);
            alert("No fue posible crear el inventario.");
        }
    });
}

if (btnActualizarInventario) {
    btnActualizarInventario.addEventListener("click", cargarInventario);
}

if (tablaInventario) {
    cargarInventario();
}

// ==========================================================
// PRODUCTOS (CON BUSCADOR, FILTROS POR CATEGORÍA Y KPIS)
// ==========================================================
const tablaProductos = document.getElementById("tablaProductos");
const formularioProducto = document.getElementById("formularioProducto");
const formProducto = document.getElementById("formProducto");
const btnNuevoProducto = document.getElementById("btnNuevoProducto");
const btnCancelarProducto = document.getElementById("btnCancelarProducto");
const btnActualizarProductos = document.getElementById("btnActualizarProductos");
const buscadorProductos = document.getElementById("buscadorProductos");
const filtroCategoriaProductos = document.getElementById("filtroCategoriaProductos");
const filtroEstadoProductos = document.getElementById("filtroEstadoProductos");

let productosMemoria = [];

async function cargarProductos() {
    if (!tablaProductos) return;

    try {
        tablaProductos.innerHTML = `<tr><td colspan="9">Cargando productos...</td></tr>`;
        const respuesta = await fetch(API_URL + "/api/productos");
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        productosMemoria = await respuesta.json();

        // Actualizar KPIs de productos
        actualizarKpisProductos();

        // Llenar select de filtro de categorías
        cargarFiltroCategorias(productosMemoria);

        // Renderizar tabla
        renderizarTablaProductos(productosMemoria);

    } catch (error) {
        console.error("Error cargando productos:", error);
        tablaProductos.innerHTML = `<tr><td colspan="9">No fue posible cargar los productos.</td></tr>`;
    }
}

function actualizarKpisProductos() {
    const elTotal = document.getElementById("kpiTotalProductos");
    const elActivos = document.getElementById("kpiProductosActivos");
    const elInactivos = document.getElementById("kpiProductosInactivos");

    if (!elTotal) return;

    const total = productosMemoria.length;
    const activos = productosMemoria.filter(p => p.estado).length;
    const inactivos = total - activos;

    elTotal.textContent = total;
    if (elActivos) elActivos.textContent = activos;
    if (elInactivos) elInactivos.textContent = inactivos;
}

function cargarFiltroCategorias(productos) {
    if (!filtroCategoriaProductos) return;
    const categoriasUnicas = new Set();

    productos.forEach(p => {
        if (p.categoria && p.categoria.nombre) {
            categoriasUnicas.add(p.categoria.nombre);
        }
    });

    const valorSeleccionado = filtroCategoriaProductos.value;
    filtroCategoriaProductos.innerHTML = `<option value="TODAS">Todas las categorías</option>`;

    categoriasUnicas.forEach(cat => {
        filtroCategoriaProductos.innerHTML += `<option value="${cat}">${cat}</option>`;
    });

    filtroCategoriaProductos.value = valorSeleccionado || "TODAS";
}

function renderizarTablaProductos(lista) {
    if (!tablaProductos) return;
    tablaProductos.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaProductos.innerHTML = `<tr><td colspan="9" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron productos.</td></tr>`;
        return;
    }

    lista.forEach(function(producto) {
        const fila = document.createElement("tr");
        const categoria = producto.categoria ? producto.categoria.nombre : "Sin categoría";
        const proveedor = producto.proveedor ? producto.proveedor.nombre : "Sin proveedor";
        const estadoTexto = producto.estado ? "ACTIVO" : "INACTIVO";
        const claseEstado = producto.estado ? "estado-disponible" : "estado-agotado";
        const precio = Number(producto.precio || 0).toLocaleString("es-CO", { style: "currency", currency: "COP" });

        fila.innerHTML = `
            <td>${producto.idProducto}</td>
            <td><code>${producto.codigo}</code></td>
            <td><strong>${producto.nombre}</strong><br><small style="color:#64748b;">${producto.descripcion || ""}</small></td>
            <td>${categoria}</td>
            <td>${proveedor}</td>
            <td><strong>${precio}</strong></td>
            <td>${producto.stockMinimo ?? 0}</td>
            <td>
                <span 
                    class="estado-stock ${claseEstado}" 
                    style="cursor: pointer;" 
                    title="Clic para alternar estado"
                    onclick="cambiarEstadoProductoDirecto(${producto.idProducto}, ${!producto.estado})"
                >
                    ● ${estadoTexto} 🔄
                </span>
            </td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarProducto(${producto.idProducto})">Editar</button>
                <button type="button" class="btn-tabla" style="background:#ef4444; color:white;" onclick="eliminarProducto(${producto.idProducto})">Eliminar</button>
            </td>
        `;
        tablaProductos.appendChild(fila);
    });
}

// Búsqueda y Filtros en tiempo real
function filtrarProductosEnPantalla() {
    const texto = buscadorProductos ? buscadorProductos.value.toLowerCase().trim() : "";
    const catFiltro = filtroCategoriaProductos ? filtroCategoriaProductos.value : "TODAS";
    const estFiltro = filtroEstadoProductos ? filtroEstadoProductos.value : "TODOS";

    const filtrados = productosMemoria.filter(p => {
        const nombre = (p.nombre || "").toLowerCase();
        const codigo = (p.codigo || "").toLowerCase();
        const desc = (p.descripcion || "").toLowerCase();
        const categoria = p.categoria ? p.categoria.nombre : "";
        const estado = p.estado ? "ACTIVO" : "INACTIVO";

        const coincideTexto = nombre.includes(texto) || codigo.includes(texto) || desc.includes(texto);
        const coincideCat = catFiltro === "TODAS" || categoria === catFiltro;
        const coincideEstado = estFiltro === "TODOS" || estado === estFiltro;

        return coincideTexto && coincideCat && coincideEstado;
    });

    renderizarTablaProductos(filtrados);
}

if (buscadorProductos) buscadorProductos.addEventListener("input", filtrarProductosEnPantalla);
if (filtroCategoriaProductos) filtroCategoriaProductos.addEventListener("change", filtrarProductosEnPantalla);
if (filtroEstadoProductos) filtroEstadoProductos.addEventListener("change", filtrarProductosEnPantalla);

// Alternar estado activo/inactivo con 1 clic
async function cambiarEstadoProductoDirecto(id, nuevoEstado) {
    try {
        const resp = await fetch(API_URL + "/api/productos/" + id);
        if (!resp.ok) throw new Error("Producto no encontrado.");
        const prod = await resp.json();

        prod.estado = nuevoEstado;

        const respPut = await fetch(API_URL + "/api/productos/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(prod)
        });

        if (!respPut.ok) throw new Error("Error al actualizar estado.");
        cargarProductos();
    } catch (error) {
        console.error("Error:", error);
        alert("No fue posible cambiar el estado del producto.");
    }
}

async function editarProducto(id) {
    try {
        const respuesta = await fetch(API_URL + "/api/productos/" + id);
        if (!respuesta.ok) throw new Error("No fue posible obtener el producto.");

        const producto = await respuesta.json();
        await cargarCategoriasProducto();
        await cargarProveedoresProducto();

        if (!formularioProducto) return;

        formularioProducto.style.display = "block";
        formularioProducto.scrollIntoView({ behavior: "smooth", block: "start" });

        document.getElementById("codigoProducto").value = producto.codigo || "";
        document.getElementById("nombreProducto").value = producto.nombre || "";
        document.getElementById("descripcionProducto").value = producto.descripcion || "";
        document.getElementById("precioProducto").value = producto.precio || 0;
        document.getElementById("stockMinimoProducto").value = producto.stockMinimo ?? 0;

        if (producto.categoria) document.getElementById("categoriaProducto").value = producto.categoria.idCategoria;
        if (producto.proveedor) document.getElementById("proveedorProducto").value = producto.proveedor.idProveedor;

        document.getElementById("estadoProducto").value = producto.estado ? "ACTIVO" : "INACTIVO";
        formProducto.dataset.editandoId = id;

        const titulo = document.getElementById("tituloFormProducto");
        if (titulo) titulo.textContent = "Editar producto";

        const botonGuardar = document.getElementById("btnGuardarProducto");
        if (botonGuardar) botonGuardar.textContent = "Guardar cambios";
    } catch (error) {
        console.error("Error editando producto:", error);
        alert("No fue posible cargar el producto.");
    }
}

async function eliminarProducto(id) {
    if (!confirm("¿Está seguro de eliminar este producto?")) return;

    try {
        const respuesta = await fetch(API_URL + "/api/productos/" + id, { method: "DELETE" });
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        alert("Producto eliminado correctamente.");
        cargarProductos();
        if (typeof cargarInventario === "function") cargarInventario();
        if (typeof cargarDashboard === "function") cargarDashboard();
    } catch (error) {
        console.error("Error eliminando producto:", error);
        alert("No fue posible eliminar el producto.");
    }
}

if (btnNuevoProducto && formularioProducto) {
    btnNuevoProducto.addEventListener("click", function() {
        if (formProducto) {
            formProducto.reset();
            delete formProducto.dataset.editandoId;
        }
        const titulo = document.getElementById("tituloFormProducto");
        if (titulo) titulo.textContent = "Registrar nuevo producto";

        const botonGuardar = document.getElementById("btnGuardarProducto");
        if (botonGuardar) botonGuardar.textContent = "Guardar producto";

        formularioProducto.style.display = "block";
        formularioProducto.scrollIntoView({ behavior: "smooth", block: "start" });

        cargarCategoriasProducto();
        cargarProveedoresProducto();
    });
}

async function cargarCategoriasProducto() {
    const select = document.getElementById("categoriaProducto");
    if (!select) return;

    try {
        const respuesta = await fetch(API_URL + "/api/categorias");
        if (!respuesta.ok) throw new Error("Error categorías HTTP");
        const categorias = await respuesta.json();

        select.innerHTML = `<option value="">Seleccione una categoría</option>`;
        categorias.forEach(function(categoria) {
            const opcion = document.createElement("option");
            opcion.value = categoria.idCategoria;
            opcion.textContent = categoria.nombre;
            select.appendChild(opcion);
        });
    } catch (error) {
        console.error("Error cargando categorías:", error);
    }
}

async function cargarProveedoresProducto() {
    const select = document.getElementById("proveedorProducto");
    if (!select) return;

    try {
        const respuesta = await fetch(API_URL + "/api/proveedores");
        if (!respuesta.ok) throw new Error("Error proveedores HTTP");
        const proveedores = await respuesta.json();

        select.innerHTML = `<option value="">Seleccione un proveedor</option>`;
        proveedores.forEach(function(proveedor) {
            const opcion = document.createElement("option");
            opcion.value = proveedor.idProveedor;
            opcion.textContent = proveedor.nombre;
            select.appendChild(opcion);
        });
    } catch (error) {
        console.error("Error cargando proveedores:", error);
    }
}

if (btnCancelarProducto && formularioProducto) {
    btnCancelarProducto.addEventListener("click", function() {
        formularioProducto.style.display = "none";
        if (formProducto) {
            formProducto.reset();
            delete formProducto.dataset.editandoId;
        }
    });
}

if (formProducto) {
    formProducto.addEventListener("submit", async function(event) {
        event.preventDefault();

        const codigo = document.getElementById("codigoProducto").value.trim();
        const nombre = document.getElementById("nombreProducto").value.trim();
        const descripcion = document.getElementById("descripcionProducto").value.trim();
        const precio = Number(document.getElementById("precioProducto").value);
        const stockMinimo = Number(document.getElementById("stockMinimoProducto").value);
        const idCategoria = Number(document.getElementById("categoriaProducto").value);
        const idProveedor = Number(document.getElementById("proveedorProducto").value);
        const estado = document.getElementById("estadoProducto").value;

        if (!codigo || !nombre || isNaN(precio) || precio < 0 || isNaN(stockMinimo) || stockMinimo < 0 || !idCategoria || !idProveedor) {
            alert("Complete correctamente todos los campos.");
            return;
        }

        const producto = {
            codigo: codigo,
            nombre: nombre,
            descripcion: descripcion,
            precio: precio,
            stockMinimo: stockMinimo,
            categoria: { idCategoria: idCategoria },
            proveedor: { idProveedor: idProveedor },
            estado: estado === "ACTIVO"
        };

        const idEditando = formProducto.dataset.editandoId;

        try {
            let respuesta;
            if (idEditando) {
                respuesta = await fetch(API_URL + "/api/productos/" + idEditando, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(producto)
                });
            } else {
                respuesta = await fetch(API_URL + "/api/productos", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(producto)
                });
            }

            if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

            alert(idEditando ? "Producto actualizado correctamente." : "Producto creado correctamente.");
            formProducto.reset();
            delete formProducto.dataset.editandoId;
            formularioProducto.style.display = "none";
            cargarProductos();
            if (typeof cargarInventario === "function") cargarInventario();
            if (typeof cargarDashboard === "function") cargarDashboard();
        } catch (error) {
            console.error("Error guardando producto:", error);
            alert("No fue posible guardar el producto.");
        }
    });
}

if (btnActualizarProductos) {
    btnActualizarProductos.addEventListener("click", cargarProductos);
}

if (tablaProductos) {
    cargarProductos();
}

// ==========================================================
// DASHBOARD
// ==========================================================
const elementoInventarioTotal = document.getElementById("inventarioTotal");
const elementoOrdenesProceso = document.getElementById("ordenesEnProceso");
const elementoEntregasHoy = document.getElementById("entregasHoy");
const elementoAlertasCriticas = document.getElementById("alertasCriticas");

async function cargarDashboard() {
    if (!elementoInventarioTotal || !elementoOrdenesProceso || !elementoEntregasHoy || !elementoAlertasCriticas) return;

    try {
        const respuesta = await fetch(API_URL + "/api/dashboard");
        if (!respuesta.ok) throw new Error("Error Dashboard HTTP: " + respuesta.status);

        const datos = await respuesta.json();
        elementoInventarioTotal.textContent = datos.inventarioTotal ?? 0;
        elementoOrdenesProceso.textContent = datos.ordenesEnProceso ?? 0;
        elementoEntregasHoy.textContent = datos.entregasHoy ?? 0;
        elementoAlertasCriticas.textContent = datos.alertasCriticas ?? 0;
    } catch (error) {
        console.error("Error cargando Dashboard:", error);
    }
}

if (elementoInventarioTotal) {
    cargarDashboard();
    setInterval(cargarDashboard, 30000);
}

// ==========================================================
// CLIENTES (CON BUSCADOR, KPIS Y FORMULARIO INTEGRADO)
// ==========================================================
const tablaClientes = document.getElementById("tablaClientes");
const formularioCliente = document.getElementById("formularioCliente");
const formCliente = document.getElementById("formCliente");
const btnNuevoCliente = document.getElementById("btnNuevoCliente");
const btnCancelarCliente = document.getElementById("btnCancelarCliente");
const btnActualizarClientes = document.getElementById("btnActualizarClientes");
const buscadorClientes = document.getElementById("buscadorClientes");
const filtroEstadoClientes = document.getElementById("filtroEstadoClientes");

let clientesMemoria = [];

async function cargarClientes() {
    if (!tablaClientes) return;

    try {
        tablaClientes.innerHTML = `<tr><td colspan="8">Cargando clientes...</td></tr>`;
        const respuesta = await fetch(API_URL + "/api/clientes");
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        clientesMemoria = await respuesta.json();

        // Actualizar tarjetas KPI
        actualizarKpisClientes();

        // Renderizar tabla
        renderizarTablaClientes(clientesMemoria);

    } catch (error) {
        console.error("Error cargando clientes:", error);
        tablaClientes.innerHTML = `<tr><td colspan="8">No fue posible cargar los clientes.</td></tr>`;
    }
}

function actualizarKpisClientes() {
    const elTotal = document.getElementById("kpiTotalClientes");
    const elActivos = document.getElementById("kpiClientesActivos");
    const elInactivos = document.getElementById("kpiClientesInactivos");

    if (!elTotal) return;

    const total = clientesMemoria.length;
    const activos = clientesMemoria.filter(c => c.estado).length;
    const inactivos = total - activos;

    elTotal.textContent = total;
    if (elActivos) elActivos.textContent = activos;
    if (elInactivos) elInactivos.textContent = inactivos;
}

function renderizarTablaClientes(lista) {
    if (!tablaClientes) return;
    tablaClientes.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaClientes.innerHTML = `<tr><td colspan="8" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron clientes registrados.</td></tr>`;
        return;
    }

    lista.forEach(function(cliente) {
        const fila = document.createElement("tr");
        const estadoTexto = cliente.estado ? "ACTIVO" : "INACTIVO";
        const claseEstado = cliente.estado ? "estado-disponible" : "estado-agotado";

        fila.innerHTML = `
            <td>${cliente.idCliente}</td>
            <td><strong>${cliente.nombre || ""}</strong></td>
            <td>${cliente.telefono || "N/A"}</td>
            <td>${cliente.correo || "N/A"}</td>
            <td>${cliente.direccion || "N/A"}</td>
            <td>${cliente.ciudad || "N/A"}</td>
            <td>
                <span 
                    class="estado-stock ${claseEstado}" 
                    style="cursor: pointer;" 
                    title="Clic para cambiar estado"
                    onclick="cambiarEstadoClienteDirecto(${cliente.idCliente}, ${!cliente.estado})"
                >
                    ● ${estadoTexto} 🔄
                </span>
            </td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarClienteModal(${cliente.idCliente})">Editar</button>
                <button type="button" class="btn-tabla" style="background-color: #ef4444; color: white;" onclick="eliminarClientePermanente(${cliente.idCliente})">Eliminar</button>
            </td>
        `;
        tablaClientes.appendChild(fila);
    });
}

// Buscador y filtro en tiempo real
function filtrarClientesEnPantalla() {
    const texto = buscadorClientes ? buscadorClientes.value.toLowerCase().trim() : "";
    const estFiltro = filtroEstadoClientes ? filtroEstadoClientes.value : "TODOS";

    const filtrados = clientesMemoria.filter(c => {
        const nombre = (c.nombre || "").toLowerCase();
        const tel = (c.telefono || "").toLowerCase();
        const correo = (c.correo || "").toLowerCase();
        const ciudad = (c.ciudad || "").toLowerCase();
        const direccion = (c.direccion || "").toLowerCase();
        const estado = c.estado ? "ACTIVO" : "INACTIVO";

        const coincideTexto = nombre.includes(texto) || tel.includes(texto) || correo.includes(texto) || ciudad.includes(texto) || direccion.includes(texto);
        const coincideEstado = estFiltro === "TODOS" || estado === estFiltro;

        return coincideTexto && coincideEstado;
    });

    renderizarTablaClientes(filtrados);
}

if (buscadorClientes) buscadorClientes.addEventListener("input", filtrarClientesEnPantalla);
if (filtroEstadoClientes) filtroEstadoClientes.addEventListener("change", filtrarClientesEnPantalla);

// Alternar estado activo/inactivo con 1 clic
async function cambiarEstadoClienteDirecto(id, nuevoEstado) {
    try {
        const resp = await fetch(API_URL + "/api/clientes/" + id);
        if (!resp.ok) throw new Error("Cliente no encontrado.");
        const cliente = await resp.json();

        cliente.estado = nuevoEstado;

        const respPut = await fetch(API_URL + "/api/clientes/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cliente)
        });

        if (!respPut.ok) throw new Error("No fue posible cambiar el estado.");
        cargarClientes();
    } catch (error) {
        console.error("Error:", error);
        alert("No fue posible cambiar el estado del cliente.");
    }
}

// Abrir formulario para Nuevo Cliente
if (btnNuevoCliente && formularioCliente) {
    btnNuevoCliente.addEventListener("click", function() {
        if (formCliente) {
            formCliente.reset();
            delete formCliente.dataset.editandoId;
        }
        const titulo = document.getElementById("tituloFormCliente");
        if (titulo) titulo.textContent = "Registrar nuevo cliente";

        const btnGuardar = document.getElementById("btnGuardarCliente");
        if (btnGuardar) btnGuardar.textContent = "Guardar cliente";

        formularioCliente.style.display = "block";
        formularioCliente.scrollIntoView({ behavior: "smooth", block: "start" });
    });
}

// Abrir formulario para Editar Cliente
async function editarClienteModal(id) {
    try {
        const resp = await fetch(API_URL + "/api/clientes/" + id);
        if (!resp.ok) throw new Error("Cliente no encontrado.");
        const c = await resp.json();

        if (!formularioCliente) return;

        formularioCliente.style.display = "block";
        formularioCliente.scrollIntoView({ behavior: "smooth", block: "start" });

        document.getElementById("nombreCliente").value = c.nombre || "";
        document.getElementById("telefonoCliente").value = c.telefono || "";
        document.getElementById("correoCliente").value = c.correo || "";
        document.getElementById("direccionCliente").value = c.direccion || "";
        document.getElementById("ciudadCliente").value = c.ciudad || "";
        document.getElementById("estadoCliente").value = c.estado ? "ACTIVO" : "INACTIVO";

        formCliente.dataset.editandoId = id;

        const titulo = document.getElementById("tituloFormCliente");
        if (titulo) titulo.textContent = "Editar cliente";

        const btnGuardar = document.getElementById("btnGuardarCliente");
        if (btnGuardar) btnGuardar.textContent = "Guardar cambios";

    } catch (error) {
        console.error("Error editando cliente:", error);
        alert("No fue posible cargar el cliente.");
    }
}

// Cancelar formulario
if (btnCancelarCliente && formularioCliente) {
    btnCancelarCliente.addEventListener("click", function() {
        formularioCliente.style.display = "none";
        if (formCliente) {
            formCliente.reset();
            delete formCliente.dataset.editandoId;
        }
    });
}

// Guardar cliente (Crear o Actualizar)
if (formCliente) {
    formCliente.addEventListener("submit", async function(event) {
        event.preventDefault();

        const nombre = document.getElementById("nombreCliente").value.trim();
        const telefono = document.getElementById("telefonoCliente").value.trim();
        const correo = document.getElementById("correoCliente").value.trim();
        const direccion = document.getElementById("direccionCliente").value.trim();
        const ciudad = document.getElementById("ciudadCliente").value.trim();
        const estado = document.getElementById("estadoCliente").value === "ACTIVO";

        if (!nombre) {
            alert("El nombre del cliente es obligatorio.");
            return;
        }

        const idEditando = formCliente.dataset.editandoId;
        const payload = { nombre, telefono, correo, direccion, ciudad, estado };

        try {
            let respuesta;
            if (idEditando) {
                respuesta = await fetch(API_URL + "/api/clientes/" + idEditando, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                respuesta = await fetch(API_URL + "/api/clientes", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!respuesta.ok) throw new Error("Error al guardar cliente.");

            alert(idEditando ? "Cliente actualizado correctamente." : "Cliente registrado correctamente.");
            formCliente.reset();
            delete formCliente.dataset.editandoId;
            formularioCliente.style.display = "none";
            cargarClientes();

        } catch (error) {
            console.error("Error guardando cliente:", error);
            alert("No fue posible guardar el cliente.");
        }
    });
}

// Eliminar definitivamente
async function eliminarClientePermanente(id) {
    const confirmar = confirm("¿Está seguro de ELIMINAR DEFINITIVAMENTE este cliente?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(API_URL + "/api/clientes/" + id, { method: "DELETE" });
        if (!respuesta.ok) throw new Error("Error al eliminar cliente.");

        alert("Cliente eliminado por completo del sistema.");
        cargarClientes();
    } catch (error) {
        console.error("Error eliminando cliente:", error);
        alert("No fue posible eliminar el cliente.");
    }
}

if (btnActualizarClientes) {
    btnActualizarClientes.addEventListener("click", cargarClientes);
}

if (tablaClientes) {
    cargarClientes();
}

// ==========================================================
// PEDIDOS (CON BUSCADOR, KPIS, FILTROS Y MÚLTIPLES PRODUCTOS)
// ==========================================================
const tablaPedidos = document.getElementById("tablaPedidos");
const seccionFormularioPedido = document.getElementById("formularioPedido");
const formPedido = document.getElementById("formPedido");
const btnNuevoPedido = document.getElementById("btnNuevoPedido");
const btnCancelarPedido = document.getElementById("btnCancelarPedido");
const btnActualizarPedidos = document.getElementById("btnActualizarPedidos");
const btnAgregarItemPedido = document.getElementById("btnAgregarItemPedido");
const tablaItemsPedidoBody = document.getElementById("tablaItemsPedidoBody");
const totalPedidoTexto = document.getElementById("totalPedidoTexto");
const buscadorPedidos = document.getElementById("buscadorPedidos");
const filtroEstadoPedidos = document.getElementById("filtroEstadoPedidos");

let pedidosMemoria = [];
let clientesMapaMemoria = new Map();
let itemsDelPedidoActual = [];
let catalogoProductosMemoria = [];

async function obtenerClientes() {
    const respuesta = await fetch(API_URL + "/api/clientes");
    if (!respuesta.ok) throw new Error("No fue posible cargar clientes.");
    return await respuesta.json();
}

async function cargarPedidos() {
    if (!tablaPedidos) return;

    try {
        tablaPedidos.innerHTML = `<tr><td colspan="5">Cargando pedidos...</td></tr>`;
        const [respPedidos, clientes] = await Promise.all([
            fetch(API_URL + "/api/pedidos"),
            obtenerClientes()
        ]);

        if (!respPedidos.ok) throw new Error("Error pedidos HTTP");
        pedidosMemoria = await respPedidos.json();

        clientesMapaMemoria = new Map();
        clientes.forEach(c => clientesMapaMemoria.set(c.idCliente, c.nombre));

        // Actualizar KPIs de pedidos
        actualizarKpisPedidos();

        // Renderizar tabla
        renderizarTablaPedidos(pedidosMemoria);

    } catch (error) {
        console.error("Error cargando pedidos:", error);
        tablaPedidos.innerHTML = `<tr><td colspan="5">No fue posible cargar los pedidos.</td></tr>`;
    }
}

function actualizarKpisPedidos() {
    const elTotal = document.getElementById("kpiTotalPedidos");
    const elProceso = document.getElementById("kpiPedidosEnProceso");
    const elEntregados = document.getElementById("kpiPedidosEntregados");
    const elCancelados = document.getElementById("kpiPedidosCancelados");

    if (!elTotal) return;

    const total = pedidosMemoria.length;
    let proceso = 0;
    let entregados = 0;
    let cancelados = 0;

    pedidosMemoria.forEach(p => {
        const est = (p.estado || "").toUpperCase();
        if (est === "ENTREGADO") entregados++;
        else if (est === "CANCELADO") cancelados++;
        else proceso++;
    });

    elTotal.textContent = total;
    if (elProceso) elProceso.textContent = proceso;
    if (elEntregados) elEntregados.textContent = entregados;
    if (elCancelados) elCancelados.textContent = cancelados;
}

function renderizarTablaPedidos(lista) {
    if (!tablaPedidos) return;
    tablaPedidos.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaPedidos.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron órdenes registradas.</td></tr>`;
        return;
    }

    lista.forEach(function(pedido) {
        const fila = document.createElement("tr");
        const nombreCliente = clientesMapaMemoria.get(pedido.idCliente) || "Cliente #" + pedido.idCliente;
        const fecha = pedido.fecha ? new Date(pedido.fecha).toLocaleString("es-CO") : "N/A";
        const estado = (pedido.estado || "PENDIENTE").toUpperCase();

        let claseEstado = "estado-disponible";
        if (estado === "CANCELADO") claseEstado = "estado-agotado";
        else if (estado === "PENDIENTE" || estado === "PROCESANDO") claseEstado = "estado-bajo";

        fila.innerHTML = `
            <td><strong>#${pedido.idPedido}</strong></td>
            <td>${fecha}</td>
            <td><strong>${nombreCliente}</strong></td>
            <td><span class="estado-stock ${claseEstado}">● ${estado}</span></td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarPedido(${pedido.idPedido})">Editar</button>
                <button type="button" class="btn-tabla" style="background-color: #ef4444; color: white;" onclick="eliminarPedido(${pedido.idPedido})">Eliminar</button>
            </td>
        `;
        tablaPedidos.appendChild(fila);
    });
}

// Buscador en tiempo real y filtro por estado
function filtrarPedidosEnPantalla() {
    const texto = buscadorPedidos ? buscadorPedidos.value.toLowerCase().trim() : "";
    const estFiltro = filtroEstadoPedidos ? filtroEstadoPedidos.value : "TODOS";

    const filtrados = pedidosMemoria.filter(p => {
        const idTexto = String(p.idPedido || "").toLowerCase();
        const clienteNombre = (clientesMapaMemoria.get(p.idCliente) || "").toLowerCase();
        const estado = (p.estado || "").toUpperCase();

        const coincideTexto = idTexto.includes(texto) || clienteNombre.includes(texto);
        const coincideEstado = estFiltro === "TODOS" || estado === estFiltro;

        return coincideTexto && coincideEstado;
    });

    renderizarTablaPedidos(filtrados);
}

if (buscadorPedidos) buscadorPedidos.addEventListener("input", filtrarPedidosEnPantalla);
if (filtroEstadoPedidos) filtroEstadoPedidos.addEventListener("change", filtrarPedidosEnPantalla);

// Cargar listas desplegables de Clientes y Productos
async function cargarDatosParaFormularioPedido() {
    const selectCliente = document.getElementById("clientePedido");
    const selectProducto = document.getElementById("productoPedido");

    try {
        const [respClientes, respProductos] = await Promise.all([
            fetch(API_URL + "/api/clientes"),
            fetch(API_URL + "/api/productos")
        ]);

        if (selectCliente && respClientes.ok) {
            const clientes = await respClientes.json();
            selectCliente.innerHTML = `<option value="">Seleccione un cliente</option>`;
            clientes.forEach(function(c) {
                selectCliente.innerHTML += `<option value="${c.idCliente}">${c.nombre} (${c.ciudad || "Sin ciudad"})</option>`;
            });
        }

        if (selectProducto && respProductos.ok) {
            catalogoProductosMemoria = await respProductos.json();
            selectProducto.innerHTML = `<option value="">Seleccione un producto</option>`;
            catalogoProductosMemoria.forEach(function(p) {
                const precioFmt = Number(p.precio || 0).toLocaleString("es-CO");
                selectProducto.innerHTML += `<option value="${p.idProducto}">${p.nombre} [${p.codigo}] - $${precioFmt}</option>`;
            });
        }

    } catch (error) {
        console.error("Error cargando datos para el pedido:", error);
    }
}

// Renderizar la tabla de productos seleccionados
function renderizarTablaItemsPedido() {
    if (!tablaItemsPedidoBody) return;
    tablaItemsPedidoBody.innerHTML = "";

    if (itemsDelPedidoActual.length === 0) {
        tablaItemsPedidoBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; color: #64748b; padding: 20px;">
                    No has agregado productos a esta orden aún.
                </td>
            </tr>
        `;
        if (totalPedidoTexto) totalPedidoTexto.textContent = "$ 0";
        return;
    }

    let totalCalculado = 0;

    itemsDelPedidoActual.forEach(function(item, index) {
        const subtotal = item.precio * item.cantidad;
        totalCalculado += subtotal;

        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td><strong>${item.nombre}</strong></td>
            <td><code>${item.codigo}</code></td>
            <td>$ ${item.precio.toLocaleString("es-CO")}</td>
            <td>
                <input 
                    type="number" 
                    min="1" 
                    value="${item.cantidad}" 
                    style="width: 70px; padding: 4px; border: 1px solid #cbd5e1; border-radius: 4px;"
                    onchange="actualizarCantidadItem(${index}, this.value)"
                >
            </td>
            <td><strong>$ ${subtotal.toLocaleString("es-CO")}</strong></td>
            <td>
                <button type="button" class="btn-tabla" style="background:#ef4444; color:white;" onclick="quitarItemPedido(${index})">
                    ✕ Quitar
                </button>
            </td>
        `;
        tablaItemsPedidoBody.appendChild(fila);
    });

    if (totalPedidoTexto) {
        totalPedidoTexto.textContent = "$ " + totalCalculado.toLocaleString("es-CO");
    }
}

// Agregar producto a la lista
if (btnAgregarItemPedido) {
    btnAgregarItemPedido.addEventListener("click", function() {
        const selectProd = document.getElementById("productoPedido");
        const inputCant = document.getElementById("cantidadPedido");

        const idProd = Number(selectProd.value);
        const cantidad = Number(inputCant.value);

        if (!idProd || isNaN(cantidad) || cantidad <= 0) {
            alert("Seleccione un producto y una cantidad válida.");
            return;
        }

        const producto = catalogoProductosMemoria.find(p => p.idProducto === idProd);
        if (!producto) return;

        const itemExistente = itemsDelPedidoActual.find(i => i.idProducto === idProd);
        if (itemExistente) {
            itemExistente.cantidad += cantidad;
        } else {
            itemsDelPedidoActual.push({
                idProducto: producto.idProducto,
                nombre: producto.nombre,
                codigo: producto.codigo || "N/A",
                precio: Number(producto.precio || 0),
                cantidad: cantidad
            });
        }

        selectProd.value = "";
        inputCant.value = 1;
        renderizarTablaItemsPedido();
    });
}

function actualizarCantidadItem(index, nuevaCantidad) {
    const cant = Number(nuevaCantidad);
    if (isNaN(cant) || cant <= 0) {
        alert("La cantidad debe ser al menos 1.");
        renderizarTablaItemsPedido();
        return;
    }
    itemsDelPedidoActual[index].cantidad = cant;
    renderizarTablaItemsPedido();
}

function quitarItemPedido(index) {
    itemsDelPedidoActual.splice(index, 1);
    renderizarTablaItemsPedido();
}

// Botón Nuevo Pedido
if (btnNuevoPedido && seccionFormularioPedido) {
    btnNuevoPedido.addEventListener("click", async function() {
        if (formPedido) {
            formPedido.reset();
            delete formPedido.dataset.editandoId;
        }
        itemsDelPedidoActual = [];
        renderizarTablaItemsPedido();

        seccionFormularioPedido.style.display = "block";
        seccionFormularioPedido.scrollIntoView({ behavior: "smooth", block: "start" });
        await cargarDatosParaFormularioPedido();
    });
}

// Botón Cancelar Pedido
if (btnCancelarPedido && seccionFormularioPedido) {
    btnCancelarPedido.addEventListener("click", function() {
        seccionFormularioPedido.style.display = "none";
        itemsDelPedidoActual = [];
        if (formPedido) {
            formPedido.reset();
            delete formPedido.dataset.editandoId;
        }
    });
}

// Confirmar y Guardar Pedido Completo
if (formPedido) {
    formPedido.addEventListener("submit", async function(event) {
        event.preventDefault();

        const idCliente = Number(document.getElementById("clientePedido").value);
        const estado = document.getElementById("estadoPedido").value;

        if (!idCliente) {
            alert("Seleccione un cliente.");
            return;
        }

        if (itemsDelPedidoActual.length === 0) {
            alert("Debe agregar al menos un producto a la orden antes de guardar.");
            return;
        }

        const sesion = localStorage.getItem("usuarioLogistic");
        const usuario = sesion ? JSON.parse(sesion) : { idUsuario: 1 };

        try {
            const respPedido = await fetch(API_URL + "/api/pedidos", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ idCliente: idCliente, estado: estado })
            });

            if (!respPedido.ok) throw new Error("No se pudo crear el pedido.");
            const nuevoPedido = await respPedido.json();

            // Salida automática de cada producto en Movimientos
            for (const item of itemsDelPedidoActual) {
                try {
                    await fetch(API_URL + "/api/movimientos", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            idProducto: item.idProducto,
                            idUsuario: usuario.idUsuario,
                            tipo: "SALIDA",
                            cantidad: item.cantidad,
                            observacion: `Salida de ${item.nombre} por Pedido #${nuevoPedido.idPedido || ""}`
                        })
                    });
                } catch (errMov) {
                    console.warn("Aviso:", errMov);
                }
            }

            alert("¡Orden de pedido registrada con éxito!");

            itemsDelPedidoActual = [];
            formPedido.reset();
            seccionFormularioPedido.style.display = "none";
            cargarPedidos();
            if (typeof cargarDashboard === "function") cargarDashboard();

        } catch (error) {
            console.error("Error guardando pedido:", error);
            alert("No fue posible registrar la orden.");
        }
    });
}

async function editarPedido(id) {
    try {
        const respuesta = await fetch(API_URL + "/api/pedidos/" + id);
        if (!respuesta.ok) throw new Error("Pedido no encontrado.");

        const pedido = await respuesta.json();
        const estados = ["PENDIENTE", "PROCESANDO", "PREPARADO", "ENVIADO", "ENTREGADO", "CANCELADO"];
        const estado = prompt("Nuevo estado de la orden:\n\n" + estados.join("\n"), pedido.estado);

        if (!estado) return;
        const estadoFinal = estado.toUpperCase();
        if (!estados.includes(estadoFinal)) {
            alert("Estado no válido.");
            return;
        }

        const respuestaPut = await fetch(API_URL + "/api/pedidos/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ idCliente: pedido.idCliente, estado: estadoFinal })
        });

        if (!respuestaPut.ok) throw new Error("Error actualizando pedido.");

        alert("Estado del pedido actualizado correctamente.");
        cargarPedidos();
        if (typeof cargarDashboard === "function") cargarDashboard();
    } catch (error) {
        console.error("Error editando pedido:", error);
        alert("No fue posible actualizar el pedido.");
    }
}

async function eliminarPedido(id) {
    const confirmar = confirm("¿Está seguro de eliminar este pedido?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(API_URL + "/api/pedidos/" + id, { method: "DELETE" });
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        alert("Pedido eliminado correctamente.");
        cargarPedidos();
        if (typeof cargarDashboard === "function") cargarDashboard();
    } catch (error) {
        console.error("Error eliminando pedido:", error);
        alert("No fue posible eliminar el pedido.");
    }
}

if (btnActualizarPedidos) btnActualizarPedidos.addEventListener("click", cargarPedidos);
if (tablaPedidos) cargarPedidos();

// ==========================================================
// ENTREGAS (CON BUSCADOR, KPIS Y FORMULARIO INTEGRADO)
// ==========================================================
const tablaEntregas = document.getElementById("tablaEntregas");
const formularioEntrega = document.getElementById("formularioEntrega");
const formEntrega = document.getElementById("formEntrega");
const btnNuevaEntrega = document.getElementById("btnNuevaEntrega");
const btnCancelarEntrega = document.getElementById("btnCancelarEntrega");
const btnActualizarEntregas = document.getElementById("btnActualizarEntregas");
const buscadorEntregas = document.getElementById("buscadorEntregas");
const filtroEstadoEntregas = document.getElementById("filtroEstadoEntregas");

let entregasMemoria = [];

async function cargarEntregas() {
    if (!tablaEntregas) return;

    try {
        tablaEntregas.innerHTML = `<tr><td colspan="6">Cargando entregas...</td></tr>`;
        const respuesta = await fetch(API_URL + "/api/entregas");
        if (!respuesta.ok) throw new Error("Error entregas HTTP: " + respuesta.status);

        entregasMemoria = await respuesta.json();

        // Actualizar tarjetas KPI
        actualizarKpisEntregas();

        // Renderizar tabla
        renderizarTablaEntregas(entregasMemoria);

    } catch (error) {
        console.error("Error cargando entregas:", error);
        tablaEntregas.innerHTML = `<tr><td colspan="6">No fue posible cargar las entregas.</td></tr>`;
    }
}

function actualizarKpisEntregas() {
    const elTotal = document.getElementById("kpiTotalEntregas");
    const elRuta = document.getElementById("kpiEnRuta");
    const elEntregadas = document.getElementById("kpiEntregadas");
    const elPendientes = document.getElementById("kpiPendientesDevueltas");

    if (!elTotal) return;

    const total = entregasMemoria.length;
    let ruta = 0;
    let entregadas = 0;
    let pendientes = 0;

    entregasMemoria.forEach(e => {
        const est = (e.estado || "").toUpperCase();
        if (est === "EN_RUTA") ruta++;
        else if (est === "ENTREGADA") entregadas++;
        else pendientes++;
    });

    elTotal.textContent = total;
    if (elRuta) elRuta.textContent = ruta;
    if (elEntregadas) elEntregadas.textContent = entregadas;
    if (elPendientes) elPendientes.textContent = pendientes;
}

function renderizarTablaEntregas(lista) {
    if (!tablaEntregas) return;
    tablaEntregas.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaEntregas.innerHTML = `<tr><td colspan="6" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron despachos registrados.</td></tr>`;
        return;
    }

    lista.forEach(function(entrega) {
        const fila = document.createElement("tr");
        const est = (entrega.estado || "PENDIENTE").toUpperCase();

        let claseEstado = "estado-disponible";
        if (est === "DEVUELTA") claseEstado = "estado-agotado";
        else if (est === "PENDIENTE" || est === "EN_RUTA") claseEstado = "estado-bajo";

        const fechaFmt = entrega.fechaEntrega 
            ? new Date(entrega.fechaEntrega).toLocaleString("es-CO") 
            : "<span style='color:#94a3b8;'>Pendiente</span>";

        fila.innerHTML = `
            <td><strong>#${entrega.idEntrega}</strong></td>
            <td><strong>Pedido #${entrega.idPedido}</strong></td>
            <td>${fechaFmt}</td>
            <td><span class="estado-stock ${claseEstado}">● ${est}</span></td>
            <td>${entrega.observacion || "Sin novedad"}</td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarEntregaModal(${entrega.idEntrega})">Editar</button>
                <button type="button" class="btn-tabla" style="background:#ef4444; color:white;" onclick="eliminarEntrega(${entrega.idEntrega})">Eliminar</button>
            </td>
        `;
        tablaEntregas.appendChild(fila);
    });
}

// Buscador en tiempo real y filtro por estado
function filtrarEntregasEnPantalla() {
    const texto = buscadorEntregas ? buscadorEntregas.value.toLowerCase().trim() : "";
    const estFiltro = filtroEstadoEntregas ? filtroEstadoEntregas.value : "TODOS";

    const filtrados = entregasMemoria.filter(e => {
        const idTexto = String(e.idEntrega || "").toLowerCase();
        const pedidoTexto = String(e.idPedido || "").toLowerCase();
        const obs = (e.observacion || "").toLowerCase();
        const estado = (e.estado || "").toUpperCase();

        const coincideTexto = idTexto.includes(texto) || pedidoTexto.includes(texto) || obs.includes(texto);
        const coincideEstado = estFiltro === "TODOS" || estado === estFiltro;

        return coincideTexto && coincideEstado;
    });

    renderizarTablaEntregas(filtrados);
}

if (buscadorEntregas) buscadorEntregas.addEventListener("input", filtrarEntregasEnPantalla);
if (filtroEstadoEntregas) filtroEstadoEntregas.addEventListener("change", filtrarEntregasEnPantalla);

// Cargar Pedidos en el select del formulario
async function cargarPedidosParaSelectEntrega() {
    const select = document.getElementById("pedidoEntrega");
    if (!select) return;

    try {
        const resp = await fetch(API_URL + "/api/pedidos");
        if (!resp.ok) throw new Error("Error consultando pedidos");
        const pedidos = await resp.json();

        select.innerHTML = `<option value="">Seleccione un pedido</option>`;
        pedidos.forEach(p => {
            select.innerHTML += `<option value="${p.idPedido}">Pedido #${p.idPedido} - [${p.estado}]</option>`;
        });
    } catch (error) {
        console.error("Error cargando pedidos para entrega:", error);
    }
}

// Abrir formulario para Nueva Entrega
if (btnNuevaEntrega && formularioEntrega) {
    btnNuevaEntrega.addEventListener("click", async function() {
        if (formEntrega) {
            formEntrega.reset();
            delete formEntrega.dataset.editandoId;
        }

        const titulo = document.getElementById("tituloFormEntrega");
        if (titulo) titulo.textContent = "Registrar nueva entrega";

        const btnGuardar = document.getElementById("btnGuardarEntrega");
        if (btnGuardar) btnGuardar.textContent = "Guardar entrega";

        formularioEntrega.style.display = "block";
        formularioEntrega.scrollIntoView({ behavior: "smooth", block: "start" });
        await cargarPedidosParaSelectEntrega();
    });
}

// Abrir formulario para Editar Entrega
async function editarEntregaModal(id) {
    try {
        const resp = await fetch(API_URL + "/api/entregas/" + id);
        if (!resp.ok) throw new Error("Entrega no encontrada.");
        const entrega = await resp.json();

        if (!formularioEntrega) return;

        await cargarPedidosParaSelectEntrega();

        formularioEntrega.style.display = "block";
        formularioEntrega.scrollIntoView({ behavior: "smooth", block: "start" });

        document.getElementById("pedidoEntrega").value = entrega.idPedido || "";
        document.getElementById("estadoEntrega").value = (entrega.estado || "PENDIENTE").toUpperCase();
        document.getElementById("observacionEntrega").value = entrega.observacion || "";

        if (entrega.fechaEntrega) {
            document.getElementById("fechaEntrega").value = entrega.fechaEntrega.slice(0, 16);
        } else {
            document.getElementById("fechaEntrega").value = "";
        }

        formEntrega.dataset.editandoId = id;

        const titulo = document.getElementById("tituloFormEntrega");
        if (titulo) titulo.textContent = "Editar despacho / entrega";

        const btnGuardar = document.getElementById("btnGuardarEntrega");
        if (btnGuardar) btnGuardar.textContent = "Guardar cambios";

    } catch (error) {
        console.error("Error editando entrega:", error);
        alert("No fue posible cargar los datos de la entrega.");
    }
}

// Cancelar formulario
if (btnCancelarEntrega && formularioEntrega) {
    btnCancelarEntrega.addEventListener("click", function() {
        formularioEntrega.style.display = "none";
        if (formEntrega) {
            formEntrega.reset();
            delete formEntrega.dataset.editandoId;
        }
    });
}

// Guardar entrega (Crear o Editar)
if (formEntrega) {
    formEntrega.addEventListener("submit", async function(event) {
        event.preventDefault();

        const idPedido = Number(document.getElementById("pedidoEntrega").value);
        const estado = document.getElementById("estadoEntrega").value;
        let fechaEntrega = document.getElementById("fechaEntrega").value;
        const observacion = document.getElementById("observacionEntrega").value.trim();

        if (!idPedido) {
            alert("Debe seleccionar un pedido válido.");
            return;
        }

        // Si el estado es ENTREGADA y no tiene fecha, poner la actual
        if (estado === "ENTREGADA" && !fechaEntrega) {
            fechaEntrega = new Date().toISOString().slice(0, 19);
        } else if (fechaEntrega) {
            fechaEntrega = fechaEntrega + ":00";
        }

        const idEditando = formEntrega.dataset.editandoId;
        const payload = {
            idPedido: idPedido,
            estado: estado,
            fechaEntrega: fechaEntrega || null,
            observacion: observacion
        };

        try {
            let respuesta;
            if (idEditando) {
                respuesta = await fetch(API_URL + "/api/entregas/" + idEditando, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                respuesta = await fetch(API_URL + "/api/entregas", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!respuesta.ok) throw new Error("Error al guardar entrega.");

            alert(idEditando ? "Entrega actualizada con éxito." : "Entrega registrada con éxito.");
            formEntrega.reset();
            delete formEntrega.dataset.editandoId;
            formularioEntrega.style.display = "none";
            cargarEntregas();
            if (typeof cargarDashboard === "function") cargarDashboard();

        } catch (error) {
            console.error("Error guardando entrega:", error);
            alert("No fue posible guardar la entrega.");
        }
    });
}

// Eliminar entrega
async function eliminarEntrega(id) {
    const confirmar = confirm("¿Está seguro de eliminar este registro de entrega?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(API_URL + "/api/entregas/" + id, { method: "DELETE" });
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        alert("Entrega eliminada correctamente.");
        cargarEntregas();
        if (typeof cargarDashboard === "function") cargarDashboard();
    } catch (error) {
        console.error("Error eliminando entrega:", error);
        alert("No fue posible eliminar la entrega.");
    }
}

if (btnActualizarEntregas) btnActualizarEntregas.addEventListener("click", cargarEntregas);
if (tablaEntregas) cargarEntregas();

// ==========================================================
// MOVIMIENTOS (CON BUSCADOR, KPIS Y FORMULARIO INTEGRADO)
// ==========================================================
const tablaMovimientos = document.getElementById("tablaMovimientos");
const formularioMovimiento = document.getElementById("formularioMovimiento");
const formMovimiento = document.getElementById("formMovimiento");
const btnNuevoMovimiento = document.getElementById("btnNuevoMovimiento");
const btnCancelarMovimiento = document.getElementById("btnCancelarMovimiento");
const btnActualizarMovimientos = document.getElementById("btnActualizarMovimientos");
const buscadorMovimientos = document.getElementById("buscadorMovimientos");
const filtroTipoMovimientos = document.getElementById("filtroTipoMovimientos");

let movimientosMemoria = [];
let productosMapaMovMemoria = new Map();
let usuariosMapaMovMemoria = new Map();

async function cargarMovimientos() {
    if (!tablaMovimientos) return;

    try {
        tablaMovimientos.innerHTML = `<tr><td colspan="9">Cargando historial de movimientos...</td></tr>`;

        const [respMov, respProd, respUser] = await Promise.all([
            fetch(API_URL + "/api/movimientos"),
            fetch(API_URL + "/api/productos"),
            fetch(API_URL + "/api/usuarios")
        ]);

        movimientosMemoria = await respMov.json();
        const productos = await respProd.json();
        const usuarios = await respUser.json();

        productosMapaMovMemoria = new Map(productos.map(p => [p.idProducto, p.nombre]));
        usuariosMapaMovMemoria = new Map(usuarios.map(u => [u.idUsuario, `${u.nombre} ${u.apellido || ''}`]));

        // Actualizar KPIs de movimientos
        actualizarKpisMovimientos();

        // Renderizar tabla
        renderizarTablaMovimientos(movimientosMemoria);

    } catch (error) {
        console.error("Error cargando movimientos:", error);
        tablaMovimientos.innerHTML = `<tr><td colspan="9">No fue posible cargar los movimientos.</td></tr>`;
    }
}

function actualizarKpisMovimientos() {
    const elTotal = document.getElementById("kpiTotalMovimientos");
    const elEntradas = document.getElementById("kpiEntradas");
    const elSalidas = document.getElementById("kpiSalidas");
    const elAjustes = document.getElementById("kpiAjustes");

    if (!elTotal) return;

    const total = movimientosMemoria.length;
    let entradas = 0;
    let salidas = 0;
    let ajustes = 0;

    movimientosMemoria.forEach(m => {
        const tipo = (m.tipo || "").toUpperCase();
        if (tipo === "ENTRADA") entradas++;
        else if (tipo === "SALIDA") salidas++;
        else ajustes++;
    });

    elTotal.textContent = total;
    if (elEntradas) elEntradas.textContent = entradas;
    if (elSalidas) elSalidas.textContent = salidas;
    if (elAjustes) elAjustes.textContent = ajustes;
}

function renderizarTablaMovimientos(lista) {
    if (!tablaMovimientos) return;
    tablaMovimientos.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaMovimientos.innerHTML = `<tr><td colspan="9" style="text-align:center; padding: 20px; color:#64748b;">No existen movimientos registrados en el kardex.</td></tr>`;
        return;
    }

    lista.forEach(function(mov) {
        const fila = document.createElement("tr");

        const prodNombre = mov.producto ? mov.producto.nombre : (productosMapaMovMemoria.get(mov.idProducto) || `Producto #${mov.idProducto}`);
        const userNombre = mov.usuario ? `${mov.usuario.nombre} ${mov.usuario.apellido || ''}` : (usuariosMapaMovMemoria.get(mov.idUsuario) || `Usuario #${mov.idUsuario}`);

        const tipo = (mov.tipo || "ENTRADA").toUpperCase();
        let claseBadge = "estado-disponible";
        let signo = "+";

        if (tipo === "SALIDA") {
            claseBadge = "estado-agotado";
            signo = "-";
        } else if (tipo === "AJUSTE") {
            claseBadge = "estado-bajo";
            signo = "⚙";
        }

        const fechaFmt = mov.fechaMovimiento ? new Date(mov.fechaMovimiento).toLocaleString("es-CO") : "N/A";

        fila.innerHTML = `
            <td>#${mov.idMovimiento}</td>
            <td><strong>${prodNombre}</strong></td>
            <td>👤 ${userNombre}</td>
            <td><span class="estado-stock ${claseBadge}">${signo} ${tipo}</span></td>
            <td><strong style="font-size: 1.05rem;">${mov.cantidad}</strong></td>
            <td>${mov.stockAnterior}</td>
            <td><strong>${mov.stockNuevo}</strong></td>
            <td>${fechaFmt}</td>
            <td><small style="color: #64748b;">${mov.observacion || "Sin observación"}</small></td>
        `;
        tablaMovimientos.appendChild(fila);
    });
}

// Buscador en vivo y filtro por tipo
function filtrarMovimientosEnPantalla() {
    const texto = buscadorMovimientos ? buscadorMovimientos.value.toLowerCase().trim() : "";
    const tipoFiltro = filtroTipoMovimientos ? filtroTipoMovimientos.value : "TODOS";

    const filtrados = movimientosMemoria.filter(m => {
        const prod = (m.producto ? m.producto.nombre : (productosMapaMovMemoria.get(m.idProducto) || "")).toLowerCase();
        const user = (m.usuario ? `${m.usuario.nombre} ${m.usuario.apellido}` : (usuariosMapaMovMemoria.get(m.idUsuario) || "")).toLowerCase();
        const obs = (m.observacion || "").toLowerCase();
        const tipo = (m.tipo || "").toUpperCase();

        const coincideTexto = prod.includes(texto) || user.includes(texto) || obs.includes(texto);
        const coincideTipo = tipoFiltro === "TODOS" || tipo === tipoFiltro;

        return coincideTexto && coincideTipo;
    });

    renderizarTablaMovimientos(filtrados);
}

if (buscadorMovimientos) buscadorMovimientos.addEventListener("input", filtrarMovimientosEnPantalla);
if (filtroTipoMovimientos) filtroTipoMovimientos.addEventListener("change", filtrarMovimientosEnPantalla);

// Cargar productos en el select del formulario
async function cargarProductosParaSelectMovimiento() {
    const select = document.getElementById("productoMovimiento");
    if (!select) return;

    try {
        const resp = await fetch(API_URL + "/api/productos");
        if (!resp.ok) throw new Error("Error consultando productos");
        const productos = await resp.json();

        select.innerHTML = `<option value="">Seleccione un producto</option>`;
        productos.forEach(p => {
            select.innerHTML += `<option value="${p.idProducto}">${p.nombre} [${p.codigo}]</option>`;
        });
    } catch (error) {
        console.error("Error cargando productos para movimiento:", error);
    }
}

// Abrir formulario para Nuevo Movimiento
if (btnNuevoMovimiento && formularioMovimiento) {
    btnNuevoMovimiento.addEventListener("click", async function() {
        if (formMovimiento) formMovimiento.reset();
        formularioMovimiento.style.display = "block";
        formularioMovimiento.scrollIntoView({ behavior: "smooth", block: "start" });
        await cargarProductosParaSelectMovimiento();
    });
}

// Cancelar formulario
if (btnCancelarMovimiento && formularioMovimiento) {
    btnCancelarMovimiento.addEventListener("click", function() {
        formularioMovimiento.style.display = "none";
        if (formMovimiento) formMovimiento.reset();
    });
}

// Guardar Movimiento en BD
if (formMovimiento) {
    formMovimiento.addEventListener("submit", async function(event) {
        event.preventDefault();

        const idProducto = Number(document.getElementById("productoMovimiento").value);
        const tipo = document.getElementById("tipoMovimiento").value;
        const cantidad = Number(document.getElementById("cantidadMovimiento").value);
        const observacion = document.getElementById("observacionMovimiento").value.trim();

        if (!idProducto || isNaN(cantidad) || cantidad <= 0) {
            alert("Seleccione un producto y una cantidad válida.");
            return;
        }

        // Obtener ID del usuario logueado en sesión
        const sesion = localStorage.getItem("usuarioLogistic");
        const usuario = sesion ? JSON.parse(sesion) : { idUsuario: 1 };

        try {
            const respuesta = await fetch(API_URL + "/api/movimientos", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    idProducto: idProducto,
                    idUsuario: usuario.idUsuario,
                    tipo: tipo,
                    cantidad: cantidad,
                    observacion: observacion || "Registro manual de kardex"
                })
            });

            if (!respuesta.ok) {
                const mensaje = await leerRespuesta(respuesta);
                throw new Error(mensaje || "Error al registrar movimiento.");
            }

            alert("¡Movimiento registrado con éxito en el kardex!");
            formMovimiento.reset();
            formularioMovimiento.style.display = "none";
            cargarMovimientos();
            if (typeof cargarInventario === "function") cargarInventario();
            if (typeof cargarDashboard === "function") cargarDashboard();

        } catch (error) {
            console.error("Error guardando movimiento:", error);
            alert(error.message || "No fue posible registrar el movimiento.");
        }
    });
}

if (btnActualizarMovimientos) btnActualizarMovimientos.addEventListener("click", cargarMovimientos);
if (tablaMovimientos) cargarMovimientos();

// ==========================================================
// USUARIOS (CON BUSCADOR, KPIS, FILTROS Y ELIMINAR)
// ==========================================================
const tablaUsuarios = document.getElementById("tablaUsuarios");
const formularioUsuario = document.getElementById("formularioUsuario");
const formUsuario = document.getElementById("formUsuario");
const btnNuevoUsuario = document.getElementById("btnNuevoUsuario");
const btnCancelarUsuario = document.getElementById("btnCancelarUsuario");
const btnActualizarUsuarios = document.getElementById("btnActualizarUsuarios");
const buscadorUsuarios = document.getElementById("buscadorUsuarios");
const filtroRolUsuarios = document.getElementById("filtroRolUsuarios");
const filtroEstadoUsuarios = document.getElementById("filtroEstadoUsuarios");

let usuariosMemoria = [];

async function cargarRolesUsuario() {
    const select = document.getElementById("rolUsuario");
    if (!select) return;

    try {
        const respuesta = await fetch(API_URL + "/api/roles");
        if (!respuesta.ok) throw new Error("Error cargando roles.");

        const roles = await respuesta.json();
        select.innerHTML = `<option value="">Seleccione un rol</option>`;

        roles.forEach(function(rol) {
            const opcion = document.createElement("option");
            opcion.value = rol.idRol;
            opcion.textContent = rol.nombre;
            select.appendChild(opcion);
        });
    } catch (error) {
        console.error("Error cargando roles:", error);
    }
}

async function cargarUsuarios() {
    if (!tablaUsuarios) return;

    try {
        tablaUsuarios.innerHTML = `<tr><td colspan="7">Cargando usuarios...</td></tr>`;
        const respuesta = await fetch(API_URL + "/api/usuarios");
        if (!respuesta.ok) throw new Error("Error usuarios HTTP: " + respuesta.status);

        usuariosMemoria = await respuesta.json();

        // Actualizar KPIs de usuarios
        actualizarKpisUsuarios();

        // Llenar select de filtro de roles
        cargarFiltroRoles(usuariosMemoria);

        // Renderizar tabla
        renderizarTablaUsuarios(usuariosMemoria);

    } catch (error) {
        console.error("Error cargando usuarios:", error);
        tablaUsuarios.innerHTML = `<tr><td colspan="7">No fue posible cargar los usuarios.</td></tr>`;
    }
}

function actualizarKpisUsuarios() {
    const elTotal = document.getElementById("kpiTotalUsuarios");
    const elActivos = document.getElementById("kpiUsuariosActivos");
    const elInactivos = document.getElementById("kpiUsuariosInactivos");
    const elAdmins = document.getElementById("kpiAdministradores");

    if (!elTotal) return;

    const total = usuariosMemoria.length;
    let activos = 0;
    let admins = 0;

    usuariosMemoria.forEach(u => {
        if (u.estado) activos++;
        const rolNom = u.rol ? (u.rol.nombre || "").toUpperCase() : "";
        if (rolNom === "ADMIN") admins++;
    });

    elTotal.textContent = total;
    if (elActivos) elActivos.textContent = activos;
    if (elInactivos) elInactivos.textContent = total - activos;
    if (elAdmins) elAdmins.textContent = admins;
}

function cargarFiltroRoles(usuarios) {
    if (!filtroRolUsuarios) return;
    const rolesUnicos = new Set();

    usuarios.forEach(u => {
        if (u.rol && u.rol.nombre) rolesUnicos.add(u.rol.nombre);
    });

    const valorActual = filtroRolUsuarios.value;
    filtroRolUsuarios.innerHTML = `<option value="TODOS">Todos los roles</option>`;

    rolesUnicos.forEach(r => {
        filtroRolUsuarios.innerHTML += `<option value="${r}">${r}</option>`;
    });

    filtroRolUsuarios.value = valorActual || "TODOS";
}

function renderizarTablaUsuarios(lista) {
    if (!tablaUsuarios) return;
    tablaUsuarios.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaUsuarios.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron usuarios.</td></tr>`;
        return;
    }

    lista.forEach(function(usuario) {
        const fila = document.createElement("tr");
        const nombreRol = usuario.rol ? usuario.rol.nombre : "Sin rol";
        const estadoTexto = usuario.estado ? "ACTIVO" : "INACTIVO";
        const claseEstado = usuario.estado ? "estado-disponible" : "estado-agotado";

        fila.innerHTML = `
            <td>${usuario.idUsuario}</td>
            <td><strong>${usuario.nombre || ""}</strong></td>
            <td>${usuario.apellido || ""}</td>
            <td>${usuario.correo || ""}</td>
            <td><span class="badge" style="background:#f1f5f9; padding:4px 8px; border-radius:4px; font-weight:bold;">${nombreRol}</span></td>
            <td>
                <span 
                    class="estado-stock ${claseEstado}" 
                    style="cursor: pointer;" 
                    title="Clic para cambiar estado"
                    onclick="cambiarEstadoUsuarioDirecto(${usuario.idUsuario}, ${!usuario.estado})"
                >
                    ● ${estadoTexto} 🔄
                </span>
            </td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarUsuario(${usuario.idUsuario})">Editar</button>
                <button type="button" class="btn-tabla" style="background:#ef4444; color:white;" onclick="eliminarUsuarioPermanente(${usuario.idUsuario})">Eliminar</button>
            </td>
        `;
        tablaUsuarios.appendChild(fila);
    });
}

// Búsqueda y Filtros en tiempo real
function filtrarUsuariosEnPantalla() {
    const texto = buscadorUsuarios ? buscadorUsuarios.value.toLowerCase().trim() : "";
    const rolFiltro = filtroRolUsuarios ? filtroRolUsuarios.value : "TODOS";
    const estFiltro = filtroEstadoUsuarios ? filtroEstadoUsuarios.value : "TODOS";

    const filtrados = usuariosMemoria.filter(u => {
        const nombre = (u.nombre || "").toLowerCase();
        const apellido = (u.apellido || "").toLowerCase();
        const correo = (u.correo || "").toLowerCase();
        const rol = u.rol ? u.rol.nombre : "";
        const estado = u.estado ? "ACTIVO" : "INACTIVO";

        const coincideTexto = nombre.includes(texto) || apellido.includes(texto) || correo.includes(texto);
        const coincideRol = rolFiltro === "TODOS" || rol === rolFiltro;
        const coincideEstado = estFiltro === "TODOS" || estado === estFiltro;

        return coincideTexto && coincideRol && coincideEstado;
    });

    renderizarTablaUsuarios(filtrados);
}

if (buscadorUsuarios) buscadorUsuarios.addEventListener("input", filtrarUsuariosEnPantalla);
if (filtroRolUsuarios) filtroRolUsuarios.addEventListener("change", filtrarUsuariosEnPantalla);
if (filtroEstadoUsuarios) filtroEstadoUsuarios.addEventListener("change", filtrarUsuariosEnPantalla);

// Alternar estado activo/inactivo con 1 clic
async function cambiarEstadoUsuarioDirecto(id, nuevoEstado) {
    try {
        const resp = await fetch(API_URL + "/api/usuarios/" + id);
        if (!resp.ok) throw new Error("Usuario no encontrado.");
        const u = await resp.json();

        u.estado = nuevoEstado;

        const respPut = await fetch(API_URL + "/api/usuarios/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(u)
        });

        if (!respPut.ok) throw new Error("Error al actualizar estado.");
        cargarUsuarios();
    } catch (error) {
        console.error("Error:", error);
        alert("No fue posible cambiar el estado del usuario.");
    }
}

// Abrir formulario Nuevo Usuario
if (btnNuevoUsuario && formularioUsuario) {
    btnNuevoUsuario.addEventListener("click", async function() {
        if (formUsuario) {
            formUsuario.reset();
            delete formUsuario.dataset.editandoId;
        }
        const titulo = document.getElementById("tituloFormUsuario");
        if (titulo) titulo.textContent = "Registrar nuevo usuario";

        const passInput = document.getElementById("passwordUsuario");
        if (passInput) passInput.required = true;

        const passAyuda = document.getElementById("passAyuda");
        if (passAyuda) passAyuda.style.display = "none";

        const botonGuardar = document.getElementById("btnGuardarUsuario");
        if (botonGuardar) botonGuardar.textContent = "Guardar usuario";

        formularioUsuario.style.display = "block";
        formularioUsuario.scrollIntoView({ behavior: "smooth" });
        await cargarRolesUsuario();
    });
}

// Abrir formulario Editar Usuario
async function editarUsuario(id) {
    try {
        const respuesta = await fetch(API_URL + "/api/usuarios/" + id);
        if (!respuesta.ok) throw new Error("Usuario no encontrado.");

        const usuario = await respuesta.json();
        await cargarRolesUsuario();

        if (formularioUsuario) {
            formularioUsuario.style.display = "block";
            formularioUsuario.scrollIntoView({ behavior: "smooth" });
        }

        document.getElementById("nombreUsuario").value = usuario.nombre || "";
        document.getElementById("apellidoUsuario").value = usuario.apellido || "";
        document.getElementById("correoUsuario").value = usuario.correo || "";

        const pass = document.getElementById("passwordUsuario");
        if (pass) {
            pass.value = "";
            pass.required = false;
        }

        const passAyuda = document.getElementById("passAyuda");
        if (passAyuda) passAyuda.style.display = "block";

        const selectRol = document.getElementById("rolUsuario");
        if (selectRol && usuario.rol) selectRol.value = usuario.rol.idRol;

        const selectEstado = document.getElementById("estadoUsuario");
        if (selectEstado) selectEstado.value = usuario.estado ? "ACTIVO" : "INACTIVO";

        if (formUsuario) formUsuario.dataset.editandoId = id;

        const titulo = document.getElementById("tituloFormUsuario");
        if (titulo) titulo.textContent = "Editar usuario";

        const botonGuardar = document.getElementById("btnGuardarUsuario");
        if (botonGuardar) botonGuardar.textContent = "Guardar cambios";
    } catch (error) {
        console.error("Error editando usuario:", error);
        alert("No fue posible cargar el usuario.");
    }
}

// Cancelar formulario
if (btnCancelarUsuario && formularioUsuario) {
    btnCancelarUsuario.addEventListener("click", function() {
        formularioUsuario.style.display = "none";
        if (formUsuario) {
            formUsuario.reset();
            delete formUsuario.dataset.editandoId;
        }
    });
}

// Guardar usuario
if (formUsuario) {
    formUsuario.addEventListener("submit", async function(event) {
        event.preventDefault();

        const nombre = document.getElementById("nombreUsuario").value.trim();
        const apellido = document.getElementById("apellidoUsuario").value.trim();
        const correo = document.getElementById("correoUsuario").value.trim();
        const password = document.getElementById("passwordUsuario").value;
        const idRol = Number(document.getElementById("rolUsuario").value);
        const estado = document.getElementById("estadoUsuario").value;

        if (!nombre || !apellido || !correo || !idRol) {
            alert("Complete los campos obligatorios.");
            return;
        }

        const idEditando = formUsuario.dataset.editandoId;

        try {
            let respuesta;
            if (!idEditando) {
                if (!password) {
                    alert("La contraseña es obligatoria para nuevos usuarios.");
                    return;
                }
                respuesta = await fetch(API_URL + "/api/usuarios", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ nombre, apellido, correo, password, idRol })
                });
            } else {
                const usuarioActualizado = {
                    idUsuario: Number(idEditando),
                    nombre: nombre,
                    apellido: apellido,
                    correo: correo,
                    estado: estado === "ACTIVO",
                    rol: { idRol: idRol }
                };
                if (password) usuarioActualizado.passwordHash = password;

                respuesta = await fetch(API_URL + "/api/usuarios/" + idEditando, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(usuarioActualizado)
                });
            }

            if (!respuesta.ok) {
                const datosError = await leerRespuesta(respuesta);
                
                // Si el GlobalExceptionHandler nos devolvió la lista de errores
                if (typeof datosError === "object" && datosError !== null) {
                    const mensajes = Object.values(datosError).join("\n");
                    throw new Error(mensajes || "Error de validación en los datos.");
                }
                
                throw new Error(datosError || "Error guardando usuario.");
            }

            alert(idEditando ? "Usuario actualizado correctamente." : "Usuario creado correctamente.");
            formUsuario.reset();
            delete formUsuario.dataset.editandoId;
            formularioUsuario.style.display = "none";
            cargarUsuarios();
        } catch (error) {
            console.error("Error guardando usuario:", error);
            alert(error.message || "No fue posible guardar el usuario.");
        }
    });
}

// Eliminar usuario
async function eliminarUsuarioPermanente(id) {
    const confirmar = confirm("¿Está seguro de eliminar este usuario del sistema?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(API_URL + "/api/usuarios/" + id, { method: "DELETE" });
        if (!respuesta.ok) throw new Error("Error al eliminar usuario.");

        alert("Usuario eliminado correctamente.");
        cargarUsuarios();
    } catch (error) {
        console.error("Error eliminando usuario:", error);
        alert("No fue posible eliminar el usuario.");
    }
}

if (btnActualizarUsuarios) btnActualizarUsuarios.addEventListener("click", cargarUsuarios);
if (tablaUsuarios) cargarUsuarios();

// ==========================================================
// ROLES (CON BUSCADOR, KPIS, PERMISOS DINÁMICOS Y ELIMINAR)
// ==========================================================
const tablaRoles = document.getElementById("tablaRoles");
const formularioRol = document.getElementById("formularioRol");
const formRol = document.getElementById("formRol");
const btnNuevoRol = document.getElementById("btnNuevoRol");
const btnCancelarRol = document.getElementById("btnCancelarRol");
const btnActualizarRoles = document.getElementById("btnActualizarRoles");
const buscadorRoles = document.getElementById("buscadorRoles");
const filtroEstadoRoles = document.getElementById("filtroEstadoRoles");

const btnMarcarTodos = document.getElementById("btnMarcarTodos");
const btnDesmarcarTodos = document.getElementById("btnDesmarcarTodos");

let rolesMemoria = [];

async function cargarRoles() {
    if (!tablaRoles) return;

    try {
        tablaRoles.innerHTML = `<tr><td colspan="5">Cargando roles...</td></tr>`;
        const respuesta = await fetch(API_URL + "/api/roles");
        if (!respuesta.ok) throw new Error("Error HTTP: " + respuesta.status);

        rolesMemoria = await respuesta.json();

        // Actualizar tarjetas KPI
        actualizarKpisRoles();

        // Renderizar tabla
        renderizarTablaRoles(rolesMemoria);

    } catch (error) {
        console.error("Error cargando roles:", error);
        tablaRoles.innerHTML = `<tr><td colspan="5">No fue posible cargar los roles.</td></tr>`;
    }
}

function actualizarKpisRoles() {
    const elTotal = document.getElementById("kpiTotalRoles");
    const elActivos = document.getElementById("kpiRolesActivos");
    const elInactivos = document.getElementById("kpiRolesInactivos");

    if (!elTotal) return;

    const total = rolesMemoria.length;
    const activos = rolesMemoria.filter(r => r.estado).length;

    elTotal.textContent = total;
    if (elActivos) elActivos.textContent = activos;
    if (elInactivos) elInactivos.textContent = total - activos;
}

function renderizarTablaRoles(lista) {
    if (!tablaRoles) return;
    tablaRoles.innerHTML = "";

    if (!lista || lista.length === 0) {
        tablaRoles.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 20px; color:#64748b;">No se encontraron roles.</td></tr>`;
        return;
    }

    lista.forEach(function(rol) {
        const fila = document.createElement("tr");
        const estadoTexto = rol.estado ? "ACTIVO" : "INACTIVO";
        const claseEstado = rol.estado ? "estado-disponible" : "estado-agotado";

        fila.innerHTML = `
            <td>${rol.idRol}</td>
            <td><strong>${rol.nombre}</strong></td>
            <td>${rol.descripcion || "Sin descripción"}</td>
            <td>
                <span 
                    class="estado-stock ${claseEstado}" 
                    style="cursor: pointer;" 
                    title="Clic para cambiar estado"
                    onclick="cambiarEstadoRolDirecto(${rol.idRol}, ${!rol.estado})"
                >
                    ● ${estadoTexto} 🔄
                </span>
            </td>
            <td>
                <button type="button" class="btn-tabla" onclick="editarRolModal(${rol.idRol})">Editar</button>
                <button type="button" class="btn-tabla" style="background:#ef4444; color:white;" onclick="eliminarRolPermanente(${rol.idRol})">Eliminar</button>
            </td>
        `;
        tablaRoles.appendChild(fila);
    });
}

// Buscador en tiempo real y filtro
function filtrarRolesEnPantalla() {
    const texto = buscadorRoles ? buscadorRoles.value.toLowerCase().trim() : "";
    const estFiltro = filtroEstadoRoles ? filtroEstadoRoles.value : "TODOS";

    const filtrados = rolesMemoria.filter(r => {
        const nom = (r.nombre || "").toLowerCase();
        const desc = (r.descripcion || "").toLowerCase();
        const estado = r.estado ? "ACTIVO" : "INACTIVO";

        const coincideTexto = nom.includes(texto) || desc.includes(texto);
        const coincideEstado = estFiltro === "TODOS" || estado === estFiltro;

        return coincideTexto && coincideEstado;
    });

    renderizarTablaRoles(filtrados);
}

if (buscadorRoles) buscadorRoles.addEventListener("input", filtrarRolesEnPantalla);
if (filtroEstadoRoles) filtroEstadoRoles.addEventListener("change", filtrarRolesEnPantalla);

// Alternar estado activo/inactivo con 1 clic
async function cambiarEstadoRolDirecto(id, nuevoEstado) {
    try {
        const resp = await fetch(API_URL + "/api/roles/" + id);
        if (!resp.ok) throw new Error("Rol no encontrado.");
        const r = await resp.json();

        r.estado = nuevoEstado;

        const respPut = await fetch(API_URL + "/api/roles/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(r)
        });

        if (!respPut.ok) throw new Error("Error al actualizar estado.");
        cargarRoles();
    } catch (error) {
        console.error("Error:", error);
        alert("No fue posible cambiar el estado del rol.");
    }
}

// Botones para marcar/desmarcar todos los checkboxes
if (btnMarcarTodos) {
    btnMarcarTodos.addEventListener("click", function() {
        document.querySelectorAll("input[name='permisoModulo']").forEach(cb => cb.checked = true);
    });
}

if (btnDesmarcarTodos) {
    btnDesmarcarTodos.addEventListener("click", function() {
        document.querySelectorAll("input[name='permisoModulo']").forEach(cb => cb.checked = false);
    });
}

// Abrir formulario Nuevo Rol
if (btnNuevoRol && formularioRol) {
    btnNuevoRol.addEventListener("click", function() {
        if (formRol) {
            formRol.reset();
            delete formRol.dataset.editandoId;
        }
        document.querySelectorAll("input[name='permisoModulo']").forEach(cb => cb.checked = true);

        const titulo = document.getElementById("tituloFormRol");
        if (titulo) titulo.textContent = "Registrar nuevo rol";

        const btnGuardar = document.getElementById("btnGuardarRol");
        if (btnGuardar) btnGuardar.textContent = "Guardar rol";

        formularioRol.style.display = "block";
        formularioRol.scrollIntoView({ behavior: "smooth" });
    });
}

// Abrir formulario Editar Rol y cargar sus casillas
async function editarRolModal(id) {
    try {
        const respuesta = await fetch(API_URL + "/api/roles/" + id);
        if (!respuesta.ok) throw new Error("Rol no encontrado.");

        const rol = await respuesta.json();

        if (formularioRol) {
            formularioRol.style.display = "block";
            formularioRol.scrollIntoView({ behavior: "smooth" });
        }

        document.getElementById("nombreRol").value = rol.nombre || "";
        document.getElementById("descripcionRol").value = rol.descripcion || "";
        document.getElementById("estadoRol").value = rol.estado ? "ACTIVO" : "INACTIVO";

        formRol.dataset.editandoId = id;

        const titulo = document.getElementById("tituloFormRol");
        if (titulo) titulo.textContent = "Editar rol y permisos";

        const btnGuardar = document.getElementById("btnGuardarRol");
        if (btnGuardar) btnGuardar.textContent = "Guardar cambios";

        // Cargar las casillas guardadas en memoria para este rol
        const checkboxes = document.querySelectorAll("input[name='permisoModulo']");
        checkboxes.forEach(cb => cb.checked = false);

        const permisosRol = localStorage.getItem("permisos_rol_" + rol.nombre.toUpperCase());
        if (permisosRol) {
            const lista = JSON.parse(permisosRol);
            checkboxes.forEach(cb => {
                if (lista.includes(cb.value)) cb.checked = true;
            });
        } else {
            // Por defecto si no se había configurado
            checkboxes.forEach(cb => cb.checked = true);
        }

    } catch (error) {
        console.error("Error editando rol:", error);
        alert("No fue posible cargar el rol.");
    }
}

// Cancelar formulario
if (btnCancelarRol && formularioRol) {
    btnCancelarRol.addEventListener("click", function() {
        formularioRol.style.display = "none";
        if (formRol) {
            formRol.reset();
            delete formRol.dataset.editandoId;
        }
    });
}

// Guardar Rol y sus permisos asignados
if (formRol) {
    formRol.addEventListener("submit", async function(event) {
        event.preventDefault();

        const nombre = document.getElementById("nombreRol").value.trim().toUpperCase();
        const descripcion = document.getElementById("descripcionRol").value.trim();
        const estado = document.getElementById("estadoRol").value === "ACTIVO";

        if (!nombre) {
            alert("El nombre del rol es obligatorio.");
            return;
        }

        // Obtener casillas marcadas
        const modulosPermitidos = [];
        document.querySelectorAll("input[name='permisoModulo']:checked").forEach(cb => {
            modulosPermitidos.push(cb.value);
        });

        if (modulosPermitidos.length === 0) {
            alert("Debe seleccionar al menos un módulo para este rol.");
            return;
        }

        const idEditando = formRol.dataset.editandoId;
        const payload = {
            nombre: nombre,
            descripcion: descripcion || `Acceso a ${modulosPermitidos.length} módulos`,
            estado: estado
        };

        try {
            let respuesta;
            if (idEditando) {
                payload.idRol = Number(idEditando);
                respuesta = await fetch(API_URL + "/api/roles/" + idEditando, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                respuesta = await fetch(API_URL + "/api/roles", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!respuesta.ok) {
                const mensaje = await leerRespuesta(respuesta);
                throw new Error(mensaje || "Error guardando rol.");
            }

            // Guardar configuración de módulos asignados al rol
            localStorage.setItem("permisos_rol_" + nombre, JSON.stringify(modulosPermitidos));

            alert(idEditando ? "Rol y permisos actualizados correctamente." : "Rol y permisos registrados con éxito.");
            formRol.reset();
            delete formRol.dataset.editandoId;
            formularioRol.style.display = "none";
            cargarRoles();

        } catch (error) {
            console.error("Error guardando rol:", error);
            alert(error.message || "No fue posible guardar el rol.");
        }
    });
}

// Eliminar rol
async function eliminarRolPermanente(id) {
    const confirmar = confirm("¿Está seguro de eliminar este rol?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(API_URL + "/api/roles/" + id, { method: "DELETE" });
        if (!respuesta.ok) {
            const mensaje = await leerRespuesta(respuesta);
            throw new Error(mensaje || "No fue posible eliminar el rol.");
        }

        alert("Rol eliminado con éxito.");
        cargarRoles();
    } catch (error) {
        console.error("Error eliminando rol:", error);
        alert(error.message || "No fue posible eliminar el rol.");
    }
}

if (btnActualizarRoles) btnActualizarRoles.addEventListener("click", cargarRoles);
if (tablaRoles) cargarRoles();

// ==========================================================
// CAMBIAR CONTRASEÑA (DESDE EL BOTÓN DEL MENÚ LATERAL)
// ==========================================================
async function cambiarMiPassword() {
    const sesion = localStorage.getItem("usuarioLogistic");
    if (!sesion) {
        alert("Debe iniciar sesión primero.");
        return;
    }

    const usuario = JSON.parse(sesion);
    const passwordActual = prompt("Ingrese su contraseña actual:");
    if (!passwordActual) return;

    const nuevaPassword = prompt("Ingrese su NUEVA contraseña (mínimo 6 caracteres):");
    if (!nuevaPassword || nuevaPassword.length < 6) {
        alert("La nueva contraseña debe tener al menos 6 caracteres.");
        return;
    }

    const confirmar = prompt("Confirme su nueva contraseña:");
    if (nuevaPassword !== confirmar) {
        alert("Las contraseñas no coinciden.");
        return;
    }

    try {
        const respuesta = await fetch(API_URL + "/api/usuarios/cambiar-password", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                idUsuario: usuario.idUsuario,
                passwordActual: passwordActual,
                nuevaPassword: nuevaPassword
            })
        });

        const mensaje = await respuesta.text();

        if (respuesta.ok) {
            alert("¡Contraseña actualizada con éxito! Por seguridad, inicie sesión nuevamente.");
            cerrarSesion();
        } else {
            alert(mensaje || "No fue posible cambiar la contraseña.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error de conexión al intentar cambiar la contraseña.");
    }
}