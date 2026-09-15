package com.softwarelogistic.dto;

/**
 * ====================================================================
 * DTO: DASHBOARD (Data Transfer Object)
 * ====================================================================
 * Este objeto transporta las métricas consolidadas (KPIs) desde el backend
 * hacia el frontend. No representa una tabla física de la base de datos,
 * por lo cual pertenece arquitectónicamente al paquete DTO.
 */
public class DashboardDTO {

    // Cantidad total de existencias físicas sumadas en inventario
    private Integer inventarioTotal;

    // Conteo de pedidos que están en estado PENDIENTE, PROCESANDO o PREPARADO
    private Long ordenesEnProceso;

    // Conteo de despachos programados para el día de hoy
    private Long entregasHoy;

    // Conteo de productos que están en nivel crítico (por debajo del stock mínimo)
    private Long alertasCriticas;

    // Constructor vacío (Requerido por Jackson para serialización JSON)
    public DashboardDTO() {
        this.inventarioTotal = 0;
        this.ordenesEnProceso = 0L;
        this.entregasHoy = 0L;
        this.alertasCriticas = 0L;
    }

    // Constructor con parámetros
    public DashboardDTO(Integer inventarioTotal, Long ordenesEnProceso, Long entregasHoy, Long alertasCriticas) {
        this.inventarioTotal = inventarioTotal;
        this.ordenesEnProceso = ordenesEnProceso;
        this.entregasHoy = entregasHoy;
        this.alertasCriticas = alertasCriticas;
    }

    // Getters y Setters
    public Integer getInventarioTotal() {
        return inventarioTotal;
    }

    public void setInventarioTotal(Integer inventarioTotal) {
        this.inventarioTotal = inventarioTotal;
    }

    public Long getOrdenesEnProceso() {
        return ordenesEnProceso;
    }

    public void setOrdenesEnProceso(Long ordenesEnProceso) {
        this.ordenesEnProceso = ordenesEnProceso;
    }

    public Long getEntregasHoy() {
        return entregasHoy;
    }

    public void setEntregasHoy(Long entregasHoy) {
        this.entregasHoy = entregasHoy;
    }

    public Long getAlertasCriticas() {
        return alertasCriticas;
    }

    public void setAlertasCriticas(Long alertasCriticas) {
        this.alertasCriticas = alertasCriticas;
    }
}