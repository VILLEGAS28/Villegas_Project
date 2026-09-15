package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * =========================================================
 * REPOSITORIO DE ROLES
 * =========================================================
 *
 * Esta interfaz permite realizar las operaciones de acceso
 * a la tabla roles de la base de datos.
 *
 */
@Repository
public interface RolRepository
        extends JpaRepository<Rol, Integer> {


    // =========================================================
    // BUSCAR POR NOMBRE
    // =========================================================

    /**
     * Busca un rol mediante su nombre.
     *
     * Ejemplo:
     *
     * ADMIN
     * SUPERVISOR
     *
     * @param nombre nombre del rol.
     * @return rol encontrado.
     */
    Optional<Rol> findByNombre(String nombre);


    // =========================================================
    // COMPROBAR NOMBRE
    // =========================================================

    /**
     * Comprueba si ya existe un rol con determinado nombre.
     *
     * @param nombre nombre que se desea comprobar.
     * @return true si existe.
     */
    boolean existsByNombre(String nombre);


    // =========================================================
    // COMPROBAR NOMBRE AL EDITAR
    // =========================================================

    /**
     * Comprueba si otro rol utiliza el mismo nombre.
     *
     * Permite editar un rol sin considerar como duplicado
     * su propio nombre.
     *
     * @param nombre nombre del rol.
     * @param idRol ID del rol que estamos editando.
     * @return true si otro rol utiliza ese nombre.
     */
    boolean existsByNombreAndIdRolNot(
            String nombre,
            Integer idRol
    );

}