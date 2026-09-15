package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Categoria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ====================================================================
 * REPOSITORIO JPA: CATEGORÍAS
 * ====================================================================
 * Provee métodos CRUD y búsquedas personalizadas sobre la tabla 'categorias'.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    /**
     * Busca una categoría por su nombre exacto (retorna Optional para evitar NullPointerException).
     */
    Optional<Categoria> findByNombre(String nombre);

    /**
     * Comprueba si ya existe una categoría con ese nombre.
     */
    boolean existsByNombre(String nombre);
}