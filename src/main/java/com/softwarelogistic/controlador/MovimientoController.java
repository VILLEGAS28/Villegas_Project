package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Movimiento;
import com.softwarelogistic.modelo.TipoMovimiento;
import com.softwarelogistic.servicio.MovimientoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: MOVIMIENTOS DE KARDEX
 * ====================================================================
 * Administra el historial de entradas, salidas y ajustes del inventario.
 */
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    /**
     * GET /api/movimientos
     * Lista todos los movimientos históricos.
     */
    @GetMapping
    public ResponseEntity<List<Movimiento>> listar() {
        return ResponseEntity.ok(movimientoService.listar());
    }

    /**
     * GET /api/movimientos/{id}
     * Obtiene un movimiento específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        Movimiento movimiento = movimientoService.buscarPorId(id);

        if (movimiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movimiento no encontrado.");
        }

        return ResponseEntity.ok(movimiento);
    }

    /**
     * POST /api/movimientos
     * Registra una entrada, salida o ajuste y actualiza el stock automáticamente.
     */
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Movimiento movimiento) {
        try {
            if (movimiento.getIdProducto() == null) {
                return ResponseEntity.badRequest().body("El campo idProducto es obligatorio.");
            }

            if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a cero.");
            }

            Movimiento nuevoMovimiento = movimientoService.registrar(movimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/movimientos/producto/{idProducto}
     * Filtra el kardex para ver los movimientos de un solo producto.
     */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<Movimiento>> buscarPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(movimientoService.buscarPorProducto(idProducto));
    }

    /**
     * GET /api/movimientos/tipo/{tipo}
     * Filtra movimientos por tipo: ENTRADA, SALIDA o AJUSTE.
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> buscarPorTipo(@PathVariable String tipo) {
        try {
            TipoMovimiento tipoMovimiento = TipoMovimiento.valueOf(tipo.toUpperCase());
            return ResponseEntity.ok(movimientoService.buscarPorTipo(tipoMovimiento));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tipo de movimiento no válido: " + tipo);
        }
    }

    /**
     * DELETE /api/movimientos/{id}
     * Elimina un movimiento histórico.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = movimientoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movimiento no encontrado.");
        }

        return ResponseEntity.ok("Movimiento eliminado correctamente.");
    }
}