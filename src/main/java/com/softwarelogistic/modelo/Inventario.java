package com.softwarelogistic.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa el inventario de cada producto.
 */
@Entity
@Table(name = "inventario")
public class Inventario {

    // ID único del inventario.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    // Producto asociado al inventario.
    @Column(name = "id_producto", nullable = false, unique = true)
    private Integer idProducto;

    // Cantidad disponible del producto.
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;

    // Fecha de última actualización.
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    // Se ejecuta antes de guardar el registro.
    @PrePersist
    protected void antesDeGuardar() {
        fechaActualizacion = LocalDateTime.now();

        if (cantidad == null) {
            cantidad = 0;
        }
    }

    // Se ejecuta antes de actualizar el registro.
    @PreUpdate
    protected void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}