package com.softwarelogistic.modelo;

import jakarta.persistence.*;

/**
 * Entidad que representa la tabla clientes
 * de la base de datos software_logistic.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    // ---------------------------------------------------------
    // ID DEL CLIENTE
    // ---------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

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

    /**
     * true  = cliente activo
     * false = cliente inactivo
     */
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // ---------------------------------------------------------
    // CONSTRUCTOR VACÍO
    // ---------------------------------------------------------

    public Cliente() {
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DEL ID
    // ---------------------------------------------------------

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
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
    // GETTER Y SETTER DEL TELÉFONO
    // ---------------------------------------------------------

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DEL CORREO
    // ---------------------------------------------------------

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DE LA DIRECCIÓN
    // ---------------------------------------------------------

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // ---------------------------------------------------------
    // GETTER Y SETTER DE LA CIUDAD
    // ---------------------------------------------------------

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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