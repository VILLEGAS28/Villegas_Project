package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Inventario;
import com.softwarelogistic.repository.InventarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: GESTIÓN DE EXISTENCIAS EN INVENTARIO FÍSICO
 * ====================================================================
 */
@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Transactional(readOnly = true)
    public List<Inventario> listarInventario() {
        return inventarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Inventario> buscarPorId(Integer id) {
        return inventarioRepository.findById(id);
    }

    /**
     * Agrega existencias al producto. Si ya existe, suma la cantidad;
     * si no existe, crea el registro inicial en bodega.
     */
    @Transactional
    public Inventario guardar(Inventario inventario) {
        Optional<Inventario> inventarioExistente = 
                inventarioRepository.findByIdProducto(inventario.getIdProducto());

        if (inventarioExistente.isPresent()) {
            Inventario existente = inventarioExistente.get();
            int cantidadActual = existente.getCantidad() != null ? existente.getCantidad() : 0;
            int cantidadNueva = inventario.getCantidad() != null ? inventario.getCantidad() : 0;

            existente.setCantidad(Math.max(0, cantidadActual + cantidadNueva));
            return inventarioRepository.save(existente);
        }

        if (inventario.getCantidad() == null || inventario.getCantidad() < 0) {
            inventario.setCantidad(0);
        }

        return inventarioRepository.save(inventario);
    }

    /**
     * Reemplaza directamente la cantidad física contada.
     */
    @Transactional
    public Inventario actualizar(Integer id, Inventario datos) {
        Optional<Inventario> existente = inventarioRepository.findById(id);

        if (existente.isEmpty()) {
            return null;
        }

        Inventario inventario = existente.get();

        if (datos.getCantidad() != null) {
            inventario.setCantidad(Math.max(0, datos.getCantidad()));
        }

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public void eliminar(Integer id) {
        inventarioRepository.deleteById(id);
    }
}