package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Usuario;
import com.softwarelogistic.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: GESTIÓN DE CUENTAS DE USUARIO Y BCrypt
 * ====================================================================
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();

        if (usuario.getIdUsuario() == null) {
            // Usuario nuevo: Encriptar contraseña en BCrypt
            usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
            usuario.setFechaCreacion(ahora);
        }

        usuario.setFechaActualizacion(ahora);

        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Usuario datos) {
        Optional<Usuario> existente = usuarioRepository.findById(datos.getIdUsuario());

        if (existente.isEmpty()) {
            return null;
        }

        Usuario usuario = existente.get();
        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setCorreo(datos.getCorreo());

        if (datos.getRol() != null) {
            usuario.setRol(datos.getRol());
        }

        if (datos.getEstado() != null) {
            usuario.setEstado(datos.getEstado());
        }

        // Actualizar contraseña solo si se envía una nueva en texto plano
        if (datos.getPasswordHash() != null && !datos.getPasswordHash().isBlank()) {
            String password = datos.getPasswordHash();

            // Comprobar si ya está hasheada en BCrypt para no re-hashear
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                usuario.setPasswordHash(passwordEncoder.encode(password));
            } else {
                usuario.setPasswordHash(password);
            }
        }

        usuario.setFechaActualizacion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }
}