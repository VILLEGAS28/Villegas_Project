package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Proveedor;
import com.softwarelogistic.servicio.ProveedorService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: PROVEEDORES
 * ====================================================================
 * Administra las empresas proveedoras de mercancías e insumos.
 */
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    /**
     * GET /api/proveedores
     * Retorna la lista de proveedores.
     */
    @GetMapping
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }

    /**
     * GET /api/proveedores/{id}
     * Busca un proveedor por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProveedor(@PathVariable Integer id) {
        Optional<Proveedor> proveedor = proveedorService.buscarPorId(id);

        if (proveedor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado.");
        }

        return ResponseEntity.ok(proveedor.get());
    }

    /**
     * POST /api/proveedores
     * Registra un nuevo proveedor validando nombre.
     */
    @PostMapping
    public ResponseEntity<?> crearProveedor(@RequestBody Proveedor proveedor) {
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del proveedor es obligatorio.");
        }

        Proveedor nuevoProveedor = proveedorService.guardarProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    /**
     * PUT /api/proveedores/{id}
     * Actualiza datos de contacto del proveedor.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProveedor(@PathVariable Integer id, @RequestBody Proveedor proveedor) {
        Proveedor actualizado = proveedorService.actualizarProveedor(id, proveedor);

        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado.");
        }

        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/proveedores/{id}
     * Elimina un proveedor protegiendo la integridad referencial.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable Integer id) {
        try {
            boolean eliminado = proveedorService.eliminarProveedor(id);

            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado.");
            }

            return ResponseEntity.ok("Proveedor eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el proveedor porque tiene productos asociados en catálogo.");
        }
    }
}