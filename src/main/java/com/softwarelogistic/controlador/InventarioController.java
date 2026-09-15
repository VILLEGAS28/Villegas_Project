package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Inventario;
import com.softwarelogistic.servicio.InventarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: INVENTARIO DE BODEGA
 * ====================================================================
 * Administra las existencias físicas de cada producto registrado.
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    /**
     * GET /api/inventario
     * Consulta todas las existencias en inventario.
     */
    @GetMapping
    public ResponseEntity<List<Inventario>> listar() {
        return ResponseEntity.ok(inventarioService.listarInventario());
    }

    /**
     * GET /api/inventario/{id}
     * Busca existencias por ID de inventario.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> buscar(@PathVariable Integer id) {
        return inventarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/inventario
     * Agrega existencias a un producto (si ya existe, suma la cantidad).
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Inventario inventario) {
        if (inventario.getIdProducto() == null) {
            return ResponseEntity.badRequest().body("El campo idProducto es obligatorio.");
        }

        if (inventario.getCantidad() == null || inventario.getCantidad() < 0) {
            return ResponseEntity.badRequest().body("La cantidad no puede ser negativa.");
        }

        Inventario nuevo = inventarioService.guardar(inventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /**
     * PUT /api/inventario/{id}
     * Sobrescribe directamente la cantidad en inventario.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Inventario inventario) {
        if (inventario.getCantidad() == null || inventario.getCantidad() < 0) {
            return ResponseEntity.badRequest().body("La cantidad no puede ser negativa.");
        }

        Inventario actualizado = inventarioService.actualizar(id, inventario);

        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventario no encontrado.");
        }

        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/inventario/{id}
     * Elimina el registro de inventario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}