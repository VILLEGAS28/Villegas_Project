package com.softwarelogistic.controlador;

import com.softwarelogistic.modelo.Categoria;
import com.softwarelogistic.servicio.CategoriaService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ====================================================================
 * CONTROLADOR REST: CATEGORÍAS
 * ====================================================================
 * Administra las clasificaciones de los productos en el inventario.
 * Rutas base: /api/categorias
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * GET /api/categorias
     * Retorna la lista completa de categorías registradas.
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    /**
     * GET /api/categorias/{id}
     * Busca una categoría por su identificador numérico único.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCategoria(@PathVariable Integer id) {
        Optional<Categoria> categoria = categoriaService.buscarPorId(id);

        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La categoría con ID " + id + " no fue encontrada.");
        }

        return ResponseEntity.ok(categoria.get());
    }

    /**
     * POST /api/categorias
     * Registra una nueva categoría validando que tenga nombre.
     */
    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        // Validación: El nombre no puede ser nulo ni estar en blanco
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de la categoría es obligatorio.");
        }

        Categoria nuevaCategoria = categoriaService.guardarCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    /**
     * PUT /api/categorias/{id}
     * Actualiza la información de una categoría existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Integer id, @RequestBody Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de la categoría es obligatorio.");
        }

        Categoria actualizada = categoriaService.actualizarCategoria(id, categoria);

        if (actualizada == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se pudo actualizar: Categoría no encontrada.");
        }

        return ResponseEntity.ok(actualizada);
    }

    /**
     * DELETE /api/categorias/{id}
     * Elimina una categoría si no tiene productos amarrados.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        try {
            boolean eliminada = categoriaService.eliminarCategoria(id);

            if (!eliminada) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe la categoría a eliminar.");
            }

            return ResponseEntity.ok("Categoría eliminada correctamente.");
        } catch (Exception e) {
            // Protección por clave foránea (si hay productos usando esta categoría)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la categoría porque tiene productos asignados.");
        }
    }
}