package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Proveedor;
import com.softwarelogistic.repository.ProveedorRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: GESTIÓN DE PROVEEDORES
 * ====================================================================
 */
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Transactional(readOnly = true)
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Proveedor> buscarPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Transactional
    public Proveedor guardarProveedor(Proveedor proveedor) {
        LocalDateTime ahora = LocalDateTime.now();
        if (proveedor.getFechaCreacion() == null) {
            proveedor.setFechaCreacion(ahora);
        }
        proveedor.setFechaActualizacion(ahora);
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(true);
        }
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public Proveedor actualizarProveedor(Integer id, Proveedor datos) {
        Optional<Proveedor> existente = proveedorRepository.findById(id);

        if (existente.isEmpty()) {
            return null;
        }

        Proveedor proveedor = existente.get();
        proveedor.setNit(datos.getNit());
        proveedor.setNombre(datos.getNombre());
        proveedor.setTelefono(datos.getTelefono());
        proveedor.setCorreo(datos.getCorreo());
        proveedor.setDireccion(datos.getDireccion());
        proveedor.setCiudad(datos.getCiudad());
        
        if (datos.getEstado() != null) {
            proveedor.setEstado(datos.getEstado());
        }

        proveedor.setFechaActualizacion(LocalDateTime.now());
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public boolean eliminarProveedor(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            return false;
        }
        proveedorRepository.deleteById(id);
        return true;
    }
}