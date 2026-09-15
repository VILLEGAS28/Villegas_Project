package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Cliente;
import com.softwarelogistic.servicio.ClienteService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: CLIENTES
 * ====================================================================
 * Gestiona el directorio maestro de clientes del sistema.
 * Rutas base: /api/clientes
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * GET /api/clientes
     * Lista todos los clientes registrados.
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    /**
     * GET /api/clientes/{id}
     * Obtiene los datos detallados de un cliente por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);

        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado.");
        }

        return ResponseEntity.ok(cliente.get());
    }

    /**
     * POST /api/clientes
     * Crea un cliente validando nombre obligatorio y correo único.
     */
    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {
        // Validar nombre obligatorio
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del cliente es obligatorio.");
        }

        // Validar que el correo no esté repetido en el sistema
        if (cliente.getCorreo() != null && !cliente.getCorreo().trim().isEmpty()) {
            if (clienteService.existeCorreo(cliente.getCorreo().trim())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya se encuentra registrado.");
            }
        }

        // Si no se especifica estado, por defecto queda Activo
        if (cliente.getEstado() == null) {
            cliente.setEstado(true);
        }

        Cliente nuevoCliente = clienteService.guardarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    /**
     * PUT /api/clientes/{id}
     * Actualiza los datos de un cliente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Integer id, @RequestBody Cliente cliente) {
        Optional<Cliente> existente = clienteService.buscarPorId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado.");
        }

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del cliente es obligatorio.");
        }

        // Validar si cambió de correo y el nuevo correo ya pertenece a otro cliente
        if (cliente.getCorreo() != null && !cliente.getCorreo().trim().isEmpty()) {
            String correoActual = existente.get().getCorreo();
            if (correoActual == null || !correoActual.equalsIgnoreCase(cliente.getCorreo().trim())) {
                if (clienteService.existeCorreo(cliente.getCorreo().trim())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("El nuevo correo ya pertenece a otro cliente.");
                }
            }
        }

        Cliente actualizado = clienteService.actualizarCliente(id, cliente);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/clientes/{id}
     * Elimina un cliente de forma definitiva de la base de datos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Integer id) {
        boolean eliminado = clienteService.eliminarCliente(id);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado para eliminar.");
        }

        return ResponseEntity.ok("Cliente eliminado completamente.");
    }

    /**
     * GET /api/clientes/buscar/nombre/{nombre}
     * Búsqueda dinámica de clientes por coincidencia de nombre.
     */
    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<Cliente>> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(nombre));
    }

    /**
     * GET /api/clientes/buscar/ciudad/{ciudad}
     * Filtra clientes por ciudad.
     */
    @GetMapping("/buscar/ciudad/{ciudad}")
    public ResponseEntity<List<Cliente>> buscarPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(clienteService.buscarPorCiudad(ciudad));
    }
}