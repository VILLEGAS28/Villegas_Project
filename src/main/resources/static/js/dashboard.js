/*
 * ==========================================================
 * SOFTWARE LOGISTIC - DASHBOARD.JS
 * GRÁFICOS INTERACTIVOS Y PANELES OPERATIVOS EN TIEMPO REAL
 * ==========================================================
 */

const DASHBOARD_API = "";

const graficoInventarioCanvas = document.getElementById("graficoInventario");
const graficoPedidosCanvas = document.getElementById("graficoPedidos");

let graficoInventario = null;
let graficoPedidos = null;

/**
 * Carga todos los datos necesarios para gráficos y paneles
 */
async function cargarDashboardVisual() {
    try {
        const [respProductos, respInventario, respPedidos, respClientes] = await Promise.all([
            fetch(DASHBOARD_API + "/api/productos"),
            fetch(DASHBOARD_API + "/api/inventario"),
            fetch(DASHBOARD_API + "/api/pedidos"),
            fetch(DASHBOARD_API + "/api/clientes")
        ]);

        if (!respProductos.ok || !respInventario.ok || !respPedidos.ok) {
            throw new Error("No fue posible consultar los datos del Dashboard.");
        }

        const [productos, inventarios, pedidos, clientes] = await Promise.all([
            respProductos.json(),
            respInventario.json(),
            respPedidos.json(),
            respClientes.ok ? respClientes.json() : []
        ]);

        // 1. Construir gráficos con Chart.js
        construirGraficoInventario(productos, inventarios);
        construirGraficoPedidos(pedidos);

        // 2. Construir los paneles operativos en vivo
        construirPanelesOperativos(productos, inventarios, pedidos, clientes);

    } catch (error) {
        console.error("Error cargando Dashboard visual:", error);
        mostrarErrorGrafico(graficoInventarioCanvas, "No fue posible cargar el gráfico de inventario.");
        mostrarErrorGrafico(graficoPedidosCanvas, "No fue posible cargar el gráfico de órdenes.");
    }
}

/**
 * Gráfico de barras: Existencias por producto
 */
function construirGraficoInventario(productos, inventarios) {
    if (!graficoInventarioCanvas) return;

    const mapaProductos = new Map();
    if (Array.isArray(productos)) {
        productos.forEach(p => mapaProductos.set(p.idProducto, p));
    }

    const nombres = [];
    const cantidades = [];
    const registros = Array.isArray(inventarios) ? inventarios.slice(0, 8) : [];

    registros.forEach(function(inv) {
        const prod = mapaProductos.get(inv.idProducto);
        nombres.push(prod ? prod.nombre : "Producto " + inv.idProducto);
        cantidades.push(Number(inv.cantidad || 0));
    });

    if (nombres.length === 0) {
        mostrarMensajeCanvas(graficoInventarioCanvas, "No hay existencias registradas.");
        return;
    }

    if (graficoInventario) graficoInventario.destroy();

    graficoInventario = new Chart(graficoInventarioCanvas, {
        type: "bar",
        data: {
            labels: nombres,
            datasets: [{
                label: "Unidades disponibles",
                data: cantidades,
                backgroundColor: "#3b82f6",
                borderRadius: 8,
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: { beginAtZero: true, ticks: { precision: 0 } },
                x: { ticks: { maxRotation: 35, minRotation: 0 } }
            }
        }
    });
}

/**
 * Gráfico de dona: Distribución de órdenes por estado
 */
function construirGraficoPedidos(pedidos) {
    if (!graficoPedidosCanvas) return;

    const estados = {
        PENDIENTE: 0,
        PROCESANDO: 0,
        PREPARADO: 0,
        ENVIADO: 0,
        ENTREGADO: 0,
        CANCELADO: 0
    };

    if (Array.isArray(pedidos)) {
        pedidos.forEach(p => {
            const est = String(p.estado || "").toUpperCase();
            if (Object.prototype.hasOwnProperty.call(estados, est)) {
                estados[est]++;
            }
        });
    }

    if (graficoPedidos) graficoPedidos.destroy();

    graficoPedidos = new Chart(graficoPedidosCanvas, {
        type: "doughnut",
        data: {
            labels: ["Pendiente", "Procesando", "Preparado", "Enviado", "Entregado", "Cancelado"],
            datasets: [{
                data: [
                    estados.PENDIENTE,
                    estados.PROCESANDO,
                    estados.PREPARADO,
                    estados.ENVIADO,
                    estados.ENTREGADO,
                    estados.CANCELADO
                ],
                backgroundColor: [
                    "#38bdf8", // Pendiente (Celeste)
                    "#f43f5e", // Procesando (Rosa)
                    "#f59e0b", // Preparado (Ámbar)
                    "#fb923c", // Enviado (Naranja)
                    "#10b981", // Entregado (Verde)
                    "#a855f7"  // Cancelado (Morado)
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: "68%",
            plugins: {
                legend: {
                    position: "bottom",
                    labels: { usePointStyle: true, padding: 14 }
                }
            }
        }
    });
}

/**
 * Llena las dos tablas operativas en tiempo real
 */
function construirPanelesOperativos(productos, inventarios, pedidos, clientes) {
    const tablaStock = document.getElementById("tablaDashStockCritico");
    const tablaOrdenes = document.getElementById("tablaDashUltimasOrdenes");

    // 1. Panel de Reposición Urgente (Stock Crítico)
    if (tablaStock && Array.isArray(inventarios) && Array.isArray(productos)) {
        const mapaProd = new Map(productos.map(p => [p.idProducto, p]));
        const criticos = inventarios.filter(inv => {
            const p = mapaProd.get(inv.idProducto);
            if (!p) return false;
            return (Number(inv.cantidad || 0) <= Number(p.stockMinimo ?? 0));
        });

        tablaStock.innerHTML = "";

        if (criticos.length === 0) {
            tablaStock.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align:center; color:#16a34a; padding: 25px; font-weight: bold;">
                        ✓ Niveles de stock óptimos en todas las referencias.
                    </td>
                </tr>
            `;
        } else {
            criticos.slice(0, 5).forEach(inv => {
                const p = mapaProd.get(inv.idProducto);
                const cant = Number(inv.cantidad || 0);
                const min = Number(p.stockMinimo ?? 0);
                const estado = cant === 0 ? "Agotado" : "Stock bajo";
                const clase = cant === 0 ? "estado-agotado" : "estado-bajo";

                tablaStock.innerHTML += `
                    <tr>
                        <td><strong>${p.nombre}</strong></td>
                        <td><code>${p.codigo || 'N/A'}</code></td>
                        <td><strong style="color: ${cant === 0 ? '#dc2626' : '#ea580c'};">${cant}</strong></td>
                        <td>${min}</td>
                        <td><span class="estado-stock ${clase}">● ${estado}</span></td>
                    </tr>
                `;
            });
        }
    }

    // 2. Panel de Últimas Órdenes Registradas
    if (tablaOrdenes && Array.isArray(pedidos)) {
        const mapaCli = new Map((clientes || []).map(c => [c.idCliente, c.nombre]));
        tablaOrdenes.innerHTML = "";

        if (pedidos.length === 0) {
            tablaOrdenes.innerHTML = `
                <tr>
                    <td colspan="4" style="text-align:center; color:#64748b; padding: 25px;">
                        No hay órdenes registradas.
                    </td>
                </tr>
            `;
        } else {
            const ultimos = pedidos.slice().reverse().slice(0, 5);

            ultimos.forEach(ped => {
                const clienteNombre = mapaCli.get(ped.idCliente) || `Cliente #${ped.idCliente}`;
                const fecha = ped.fecha ? new Date(ped.fecha).toLocaleDateString("es-CO", { hour: '2-digit', minute: '2-digit' }) : "Hoy";
                const est = (ped.estado || "PENDIENTE").toUpperCase();

                let claseEst = "estado-disponible";
                if (est === "CANCELADO") claseEst = "estado-agotado";
                else if (est === "PENDIENTE" || est === "PROCESANDO") claseEst = "estado-bajo";

                tablaOrdenes.innerHTML += `
                    <tr>
                        <td><strong>#${ped.idPedido}</strong></td>
                        <td>${clienteNombre}</td>
                        <td><small style="color: #64748b;">${fecha}</small></td>
                        <td><span class="estado-stock ${claseEst}">● ${est}</span></td>
                    </tr>
                `;
            });
        }
    }
}

function mostrarMensajeCanvas(canvas, mensaje) {
    if (!canvas) return;
    const contenedor = canvas.parentElement;
    if (!contenedor) return;

    const anterior = contenedor.querySelector(".dashboard-grafico-mensaje");
    if (anterior) anterior.remove();

    const el = document.createElement("div");
    el.className = "dashboard-grafico-mensaje";
    el.textContent = mensaje;
    contenedor.appendChild(el);
}

function mostrarErrorGrafico(canvas, mensaje) {
    mostrarMensajeCanvas(canvas, mensaje);
}

const botonActualizarDashboard = document.getElementById("btnActualizarDashboard");
if (botonActualizarDashboard) {
    botonActualizarDashboard.addEventListener("click", async function() {
        botonActualizarDashboard.disabled = true;
        botonActualizarDashboard.textContent = "Actualizando...";
        await cargarDashboardVisual();
        botonActualizarDashboard.disabled = false;
        botonActualizarDashboard.textContent = "↻ Actualizar métricas";
    });
}

document.addEventListener("DOMContentLoaded", cargarDashboardVisual);