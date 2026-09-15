package com.softwarelogistic.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla proveedores
 * de la base de datos software_logistic.
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {

    // ---------------------------------------------------------
    // ID DEL PROVEEDOR
    // ---------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    // ---------------------------------------------------------
    // NIT
    // ---------------------------------------------------------

    @Column(name = "nit", unique = true, length = 30)
    private String nit;

    // ---------------------------------------------------------
    // NOMBRE
    // ---------------------------------------------------------

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // ---------------------------------------------------------
    // TELÉFONO
    // ---------------------------------------------------------

    @Column(name = "telefono", length = 30)
    private String telefono;

    // ---------------------------------------------------------
    // CORREO
    // ---------------------------------------------------------

    @Column(name = "correo", length = 100)
    private String correo;

    // ---------------------------------------------------------
    // DIRECCIÓN
    // ---------------------------------------------------------

    @Column(name = "direccion", length = 150)
    private String direccion;

    // ---------------------------------------------------------
    // CIUDAD
    // ---------------------------------------------------------

    @Column(name = "ciudad", length = 80)
    private String ciudad;

    // ---------------------------------------------------------
    // ESTADO
    // ---------------------------------------------------------

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    // ---------------------------------------------------------
    // FECHAS AUTOMÁTICAS
    // ---------------------------------------------------------

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ---------------------------------------------------------
    // MÉTODOS DE CICLO DE VIDA DE JPA (AUDITORÍA AUTOMÁTICA)
    // ---------------------------------------------------------

    @PrePersist
    protected void antesDeGuardar() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    // ---------------------------------------------------------
    // GETTERS Y SETTERS
    // ---------------------------------------------------------

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}