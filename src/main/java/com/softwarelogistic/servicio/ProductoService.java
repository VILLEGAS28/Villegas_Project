package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Producto;
import com.softwarelogistic.repository.ProductoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: CATÁLOGO MAESTRO DE PRODUCTOS
 * ====================================================================
 */
@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto guardarProducto(Producto producto) {
        LocalDateTime ahora = LocalDateTime.now();
        producto.setFechaCreacion(ahora);
        producto.setFechaActualizacion(ahora);

        if (producto.getEstado() == null) {
            producto.setEstado(true);
        }

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Integer id, Producto datos) {
        Optional<Producto> existente = productoRepository.findById(id);

        if (existente.isEmpty()) {
            return null;
        }

        Producto producto = existente.get();
        producto.setCodigo(datos.getCodigo());
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStockMinimo(datos.getStockMinimo());
        producto.setCategoria(datos.getCategoria());
        producto.setProveedor(datos.getProveedor());
        
        if (datos.getEstado() != null) {
            producto.setEstado(datos.getEstado());
        }

        producto.setFechaActualizacion(LocalDateTime.now());
        return productoRepository.save(producto);
    }

    @Transactional
    public boolean eliminarProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findByEstadoTrue();
    }
}