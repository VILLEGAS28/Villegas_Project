package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Producto;
import com.softwarelogistic.servicio.ProductoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: CATÁLOGO DE PRODUCTOS
 * ====================================================================
 * Administra los ítems comercializados, precios y stock mínimo de seguridad.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * GET /api/productos
     * Retorna todo el catálogo de productos.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    /**
     * GET /api/productos/{id}
     * Busca un producto por su ID único.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProducto(@PathVariable Integer id) {
        Optional<Producto> producto = productoService.buscarPorId(id);

        if (producto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }

        return ResponseEntity.ok(producto.get());
    }

    /**
     * POST /api/productos
     * Registra un producto con validaciones de código, nombre y precio positivo.
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El código del producto es obligatorio.");
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del producto es obligatorio.");
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().body("El precio debe ser un valor mayor o igual a 0.");
        }

        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            producto.setStockMinimo(0);
        }

        if (producto.getEstado() == null) {
            producto.setEstado(true);
        }

        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());

        Producto nuevoProducto = productoService.guardarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    /**
     * PUT /api/productos/{id}
     * Actualiza la información de un producto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        if (producto.getPrecio() != null && producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().body("El precio no puede ser negativo.");
        }

        producto.setFechaActualizacion(LocalDateTime.now());
        Producto actualizado = productoService.actualizarProducto(id, producto);

        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }

        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/productos/{id}
     * Elimina un producto si no está amarrado a movimientos o pedidos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        try {
            boolean eliminado = productoService.eliminarProducto(id);

            if (!eliminada(id, eliminado)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
            }

            return ResponseEntity.ok("Producto eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el producto porque tiene movimientos o pedidos asociados.");
        }
    }

    private boolean eliminada(Integer id, boolean res) {
        return res;
    }

    /**
     * GET /api/productos/buscar?nombre=...
     * Búsqueda por coincidencia de nombre.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    /**
     * GET /api/productos/activos
     * Filtra solo productos con estado Activo.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> listarActivos() {
        return ResponseEntity.ok(productoService.listarActivos());
    }
}