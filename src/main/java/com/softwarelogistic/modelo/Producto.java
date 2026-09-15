package com.softwarelogistic.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla productos
 * de la base de datos software_logistic.
 */
@Entity
@Table(name = "productos")
public class Producto {

    // ---------------------------------------------------------
    // IDENTIFICADOR DEL PRODUCTO
    // ---------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    // ---------------------------------------------------------
    // CÓDIGO
    // ---------------------------------------------------------

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    // ---------------------------------------------------------
    // NOMBRE
    // ---------------------------------------------------------

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // ---------------------------------------------------------
    // DESCRIPCIÓN
    // ---------------------------------------------------------

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // ---------------------------------------------------------
    // PRECIO
    // ---------------------------------------------------------

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    // ---------------------------------------------------------
    // STOCK MÍNIMO
    // ---------------------------------------------------------

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    // ---------------------------------------------------------
    // CATEGORÍA
    // ---------------------------------------------------------

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // ---------------------------------------------------------
    // PROVEEDOR
    // ---------------------------------------------------------

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    // ---------------------------------------------------------
    // ESTADO
    // ---------------------------------------------------------

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    // ---------------------------------------------------------
    // FECHA DE CREACIÓN
    // ---------------------------------------------------------

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // ---------------------------------------------------------
    // FECHA DE ACTUALIZACIÓN
    // ---------------------------------------------------------

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    // ---------------------------------------------------------
    // GETTERS Y SETTERS
    // ---------------------------------------------------------

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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