package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.DetallePedido;
import com.softwarelogistic.servicio.DetallePedidoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: DETALLES DE PEDIDOS
 * ====================================================================
 * Administra las líneas individuales de productos incluidas en cada orden.
 * Soporta ambas rutas: /api/detalle-pedido y /api/detalles-pedidos
 */
@RestController
@RequestMapping({"/api/detalle-pedido", "/api/detalles-pedidos"})
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    /**
     * GET /api/detalle-pedido
     * Lista todos los detalles de órdenes existentes.
     */
    @GetMapping
    public ResponseEntity<List<DetallePedido>> listar() {
        return ResponseEntity.ok(detallePedidoService.listar());
    }

    /**
     * GET /api/detalle-pedido/{id}
     * Obtiene un ítem de detalle por su identificador primario.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        Optional<DetallePedido> detalle = detallePedidoService.buscarPorId(id);

        if (detalle.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Línea de pedido no encontrada.");
        }

        return ResponseEntity.ok(detalle.get());
    }

    /**
     * POST /api/detalle-pedido
     * Registra un producto dentro de una orden con validaciones estrictas.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DetallePedido detalle) {
        try {
            if (detalle.getIdPedido() == null) {
                return ResponseEntity.badRequest().body("El campo idPedido es obligatorio.");
            }

            if (detalle.getIdProducto() == null) {
                return ResponseEntity.badRequest().body("El campo idProducto es obligatorio.");
            }

            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a cero.");
            }

            DetallePedido nuevo = detallePedidoService.guardar(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * PUT /api/detalle-pedido/{id}
     * Actualiza cantidades o valores en una línea de pedido.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody DetallePedido detalle) {
        try {
            Optional<DetallePedido> existente = detallePedidoService.buscarPorId(id);

            if (existente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Línea de pedido no encontrada.");
            }

            detalle.setIdDetalle(id);
            DetallePedido actualizado = detallePedidoService.actualizar(id, detalle);

            return ResponseEntity.ok(actualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/detalle-pedido/{id}
     * Elimina un producto de una orden.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = detallePedidoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle de pedido no encontrado.");
        }

        return ResponseEntity.ok("Detalle de pedido eliminado correctamente.");
    }

    /**
     * GET /api/detalle-pedido/pedido/{idPedido}
     * Obtiene todos los productos que pertenecen a una orden específica.
     */
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<DetallePedido>> buscarPorPedido(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(detallePedidoService.buscarPorPedido(idPedido));
    }

    /**
     * GET /api/detalle-pedido/producto/{idProducto}
     * Obtiene en qué órdenes se ha vendido un producto determinado.
     */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<DetallePedido>> buscarPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(detallePedidoService.buscarPorProducto(idProducto));
    }
}