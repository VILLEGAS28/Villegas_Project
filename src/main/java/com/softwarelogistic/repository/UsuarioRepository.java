package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * =========================================================
 * REPOSITORIO DE USUARIOS
 * =========================================================
 *
 * Esta interfaz permite realizar las operaciones de acceso
 * a la tabla usuarios de la base de datos.
 *
 */
@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {


    // =========================================================
    // BUSCAR USUARIO POR CORREO
    // =========================================================

    /**
     * Busca un usuario utilizando su correo electrónico.
     *
     * Se utiliza principalmente para el inicio de sesión.
     *
     * @param correo correo electrónico del usuario.
     * @return usuario encontrado.
     */
    Optional<Usuario> findByCorreo(String correo);


    // =========================================================
    // COMPROBAR CORREO
    // =========================================================

    /**
     * Comprueba si ya existe un usuario con determinado correo.
     *
     * @param correo correo electrónico.
     * @return true si el correo ya existe.
     */
    boolean existsByCorreo(String correo);


    // =========================================================
    // COMPROBAR CORREO AL EDITAR
    // =========================================================

    /**
     * Comprueba si un correo pertenece a otro usuario.
     *
     * Esto permite editar un usuario sin generar un error
     * cuando conserva su propio correo.
     *
     * Ejemplo:
     *
     * Usuario 1 -> jhonatan@correo.com
     *
     * Al editar Usuario 1 podemos conservar ese correo.
     *
     * Pero no podemos asignarlo a Usuario 2.
     *
     * @param correo correo que queremos comprobar.
     * @param idUsuario ID que NO debe tenerse en cuenta.
     * @return true si otro usuario ya utiliza ese correo.
     */
    boolean existsByCorreoAndIdUsuarioNot(
            String correo,
            Integer idUsuario
    );

}