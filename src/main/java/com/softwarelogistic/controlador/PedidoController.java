package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.EstadoPedido;
import com.softwarelogistic.modelo.Pedido;
import com.softwarelogistic.servicio.PedidoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: PEDIDOS Y ÓRDENES
 * ====================================================================
 * Administra las órdenes de compra generadas para los clientes.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * GET /api/pedidos
     * Retorna todas las órdenes registradas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.listar());
    }

    /**
     * GET /api/pedidos/{id}
     * Busca una orden por su identificador primario.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Integer id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        return pedido.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/pedidos
     * Registra una orden asegurando cliente, fecha actual y estado inicial.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Pedido pedido) {
        if (pedido.getIdCliente() == null) {
            return ResponseEntity.badRequest().body("El ID del cliente es obligatorio.");
        }

        // Asignar fecha actual si no viene especificada
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }

        // Asignar estado PENDIENTE por defecto si viene nulo
        if (pedido.getEstado() == null) {
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }

        Pedido nuevoPedido = pedidoService.guardar(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    /**
     * PUT /api/pedidos/{id}
     * Actualiza el estado o cliente de la orden.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        Optional<Pedido> existente = pedidoService.buscarPorId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado.");
        }

        if (pedido.getIdCliente() == null) {
            pedido.setIdCliente(existente.get().getIdCliente());
        }

        Pedido actualizado = pedidoService.actualizar(id, pedido);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/pedidos/{id}
     * Elimina una orden de pedido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = pedidoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado.");
        }

        return ResponseEntity.ok("Pedido eliminado correctamente.");
    }

    /**
     * GET /api/pedidos/cliente/{idCliente}
     * Lista todos los pedidos realizados por un cliente específico.
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pedido>> buscarPorCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(pedidoService.buscarPorCliente(idCliente));
    }

    /**
     * GET /api/pedidos/estado/{estado}
     * Filtra pedidos por estado (PENDIENTE, ENVIADO, ENTREGADO, etc.).
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> buscarPorEstado(@PathVariable String estado) {
        try {
            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(pedidoService.buscarPorEstado(estadoPedido));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado de pedido no válido: " + estado);
        }
    }
}