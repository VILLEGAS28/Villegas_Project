package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Entrega;
import com.softwarelogistic.servicio.EntregaService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: ENTREGAS Y DESPACHOS
 * ====================================================================
 * Monitorea el estado logístico de cada pedido:
 * PENDIENTE -> EN_RUTA -> ENTREGADA -> DEVUELTA
 */
@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    @Autowired
    private EntregaService entregaService;

    /**
     * GET /api/entregas
     * Lista todos los registros de despachos y rutas.
     */
    @GetMapping
    public ResponseEntity<List<Entrega>> listar() {
        return ResponseEntity.ok(entregaService.listar());
    }

    /**
     * GET /api/entregas/{id}
     * Busca un despacho por su ID único de entrega.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Entrega> buscar(@PathVariable Integer id) {
        Optional<Entrega> entrega = entregaService.buscar(id);
        return entrega.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/entregas/pedido/{idPedido}
     * Busca la entrega asociada a una orden de pedido específica.
     */
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<Entrega> buscarPorPedido(@PathVariable Integer idPedido) {
        Optional<Entrega> entrega = entregaService.buscarPorPedido(idPedido);
        return entrega.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/entregas
     * Crea un despacho validando que el pedido exista y no tenga entrega previa.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Entrega entrega) {
        if (entrega.getIdPedido() == null) {
            return ResponseEntity.badRequest().body("El campo idPedido es obligatorio.");
        }

        // Regla de negocio: Un pedido no puede tener dos entregas duplicadas
        Optional<Entrega> existente = entregaService.buscarPorPedido(entrega.getIdPedido());
        if (existente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El pedido #" + entrega.getIdPedido() + " ya tiene un despacho asignado.");
        }

        Entrega nuevaEntrega = entregaService.guardar(entrega);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEntrega);
    }

    /**
     * PUT /api/entregas/{id}
     * Actualiza el estado (ej: marcar como ENTREGADA), fecha y observaciones.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Entrega entrega) {
        Optional<Entrega> existente = entregaService.buscar(id);

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrega no encontrada.");
        }

        Entrega actualizada = entregaService.actualizar(id, entrega);
        return ResponseEntity.ok(actualizada);
    }

    /**
     * DELETE /api/entregas/{id}
     * Elimina un despacho.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = entregaService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrega no encontrada.");
        }

        return ResponseEntity.ok("Entrega eliminada correctamente.");
    }
}