package com.softwarelogistic.controlador;

import com.softwarelogistic.dto.DashboardDTO;
import com.softwarelogistic.servicio.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: DASHBOARD
 * ====================================================================
 * Proporciona las métricas consolidadas (KPIs) en tiempo real para el
 * panel principal de control: total inventario, órdenes activas,
 * entregas del día y alertas de stock bajo.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * GET /api/dashboard
     * Retorna el objeto DTO con las sumatorias e indicadores clave.
     */
    @GetMapping
    public ResponseEntity<DashboardDTO> obtenerDashboard() {
        DashboardDTO dashboard = dashboardService.obtenerDashboard();
        
        // Si el servicio devolviera nulo por base de datos vacía, devolvemos uno inicializado en 0
        if (dashboard == null) {
            dashboard = new DashboardDTO();
        }

        return ResponseEntity.ok(dashboard);
    }
}