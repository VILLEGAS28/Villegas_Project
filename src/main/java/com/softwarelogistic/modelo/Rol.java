package com.softwarelogistic.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa la tabla roles de la base de datos.
 *
 * Esta clase permite que Spring Boot / JPA trabaje
 * directamente con los registros de la tabla roles.
 */
@Entity
@Table(name = "roles")
public class Rol {

    // ---------------------------------------------------------
    // ID PRINCIPAL DEL ROL
    // ---------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    // ---------------------------------------------------------
    // NOMBRE DEL ROL
    // ---------------------------------------------------------

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    // ---------------------------------------------------------
    // DESCRIPCIÓN DEL ROL
    // ---------------------------------------------------------

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    // ---------------------------------------------------------
    // ESTADO DEL ROL
    // 1 = activo
    // 0 = inactivo
    // ---------------------------------------------------------

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // ---------------------------------------------------------
    // CONSTRUCTOR VACÍO
    // ---------------------------------------------------------

    public Rol() {
    }

    // ---------------------------------------------------------
    // CONSTRUCTOR PRINCIPAL
    // ---------------------------------------------------------

    public Rol(String nombre, String descripcion, Boolean estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DEL ID
    // ---------------------------------------------------------

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DEL NOMBRE
    // ---------------------------------------------------------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DE LA DESCRIPCIÓN
    // ---------------------------------------------------------

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DEL ESTADO
    // ---------------------------------------------------------

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}