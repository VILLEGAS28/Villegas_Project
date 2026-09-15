package com.softwarelogistic.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa la tabla categorias
 * de la base de datos software_logistic.
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    // ---------------------------------------------------------
    // ID DE LA CATEGORÍA
    // ---------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    // ---------------------------------------------------------
    // NOMBRE
    // ---------------------------------------------------------

    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    // ---------------------------------------------------------
    // DESCRIPCIÓN
    // ---------------------------------------------------------

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    // ---------------------------------------------------------
    // ESTADO
    // ---------------------------------------------------------

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    // ---------------------------------------------------------
    // GETTERS Y SETTERS
    // ---------------------------------------------------------

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}