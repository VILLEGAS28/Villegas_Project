package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Rol;
import com.softwarelogistic.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: ROLES Y PERMISOS DE USUARIOS
 * ====================================================================
 */
@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorId(Integer id) {
        return rolRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    @Transactional
    public Rol guardarRol(Rol rol) {
        if (rol.getEstado() == null) {
            rol.setEstado(true);
        }
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol actualizarRol(Rol datos) {
        Optional<Rol> existente = rolRepository.findById(datos.getIdRol());

        if (existente.isEmpty()) {
            return null;
        }

        Rol rol = existente.get();

        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            rol.setNombre(datos.getNombre());
        }

        rol.setDescripcion(datos.getDescripcion());

        if (datos.getEstado() != null) {
            rol.setEstado(datos.getEstado());
        }

        return rolRepository.save(rol);
    }

    @Transactional
    public void eliminarRol(Integer id) {
        rolRepository.deleteById(id);
    }
}