package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Entrega;
import com.softwarelogistic.modelo.EstadoEntrega;
import com.softwarelogistic.repository.EntregaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: GESTIÓN DE DESPACHOS Y ENTREGAS
 * ====================================================================
 */
@Service
public class EntregaService {

    @Autowired
    private EntregaRepository entregaRepository;

    @Transactional(readOnly = true)
    public List<Entrega> listar() {
        return entregaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Entrega> buscar(Integer id) {
        return entregaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Entrega> buscarPorPedido(Integer idPedido) {
        return entregaRepository.findByIdPedido(idPedido);
    }

    @Transactional
    public Entrega guardar(Entrega entrega) {
        if (entrega.getEstado() == null) {
            entrega.setEstado(EstadoEntrega.PENDIENTE);
        }
        return entregaRepository.save(entrega);
    }

    @Transactional
    public Entrega actualizar(Integer id, Entrega datos) {
        Optional<Entrega> existente = entregaRepository.findById(id);

        if (existente.isEmpty()) {
            return null;
        }

        Entrega entrega = existente.get();

        if (datos.getFechaEntrega() != null) {
            entrega.setFechaEntrega(datos.getFechaEntrega());
        }

        if (datos.getEstado() != null) {
            entrega.setEstado(datos.getEstado());
        }

        if (datos.getObservacion() != null) {
            entrega.setObservacion(datos.getObservacion());
        }

        return entregaRepository.save(entrega);
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!entregaRepository.existsById(id)) {
            return false;
        }
        entregaRepository.deleteById(id);
        return true;
    }
}