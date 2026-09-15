package com.softwarelogistic.servicio;

import com.softwarelogistic.dto.DashboardDTO;
import com.softwarelogistic.modelo.EstadoPedido;
import com.softwarelogistic.modelo.Inventario;
import com.softwarelogistic.modelo.Producto;
import com.softwarelogistic.repository.EntregaRepository;
import com.softwarelogistic.repository.InventarioRepository;
import com.softwarelogistic.repository.PedidoRepository;
import com.softwarelogistic.repository.ProductoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: CÁLCULO DE KPIS DEL DASHBOARD
 * ====================================================================
 */
@Service
public class DashboardService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public DashboardDTO obtenerDashboard() {

        // 1. Total unidades en bodega (Suma optimizada por base de datos)
        Integer inventarioTotal = inventarioRepository.sumarTotalUnidadesInventario();
        if (inventarioTotal == null) inventarioTotal = 0;

        // 2. Órdenes en proceso
        long ordenesEnProceso = pedidoRepository.countByEstado(EstadoPedido.PROCESANDO)
                + pedidoRepository.countByEstado(EstadoPedido.PREPARADO)
                + pedidoRepository.countByEstado(EstadoPedido.ENVIADO);

        // 3. Entregas del día
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        long entregasHoy = entregaRepository.countByFechaEntregaBetween(inicio, fin);

        // 4. Alertas críticas de stock
        long alertasCriticas = calcularAlertasCriticas();

        return new DashboardDTO(
                inventarioTotal,
                ordenesEnProceso,
                entregasHoy,
                alertasCriticas
        );
    }

    private long calcularAlertasCriticas() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        long alertas = 0;

        for (Inventario inv : inventarios) {
            if (inv.getIdProducto() == null) continue;

            Producto prod = productoRepository.findById(inv.getIdProducto()).orElse(null);
            if (prod == null) continue;

            Integer cantidad = inv.getCantidad();
            Integer stockMinimo = prod.getStockMinimo();

            if (cantidad != null && stockMinimo != null && cantidad <= stockMinimo) {
                alertas++;
            }
        }
        return alertas;
    }
}