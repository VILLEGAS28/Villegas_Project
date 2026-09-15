package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.EstadoPedido;
import com.softwarelogistic.modelo.Pedido;
import com.softwarelogistic.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: LÓGICA DE NEGOCIO PARA ÓRDENES Y PEDIDOS
 * ====================================================================
 */
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido guardar(Pedido pedido) {
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }
        if (pedido.getEstado() == null) {
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizar(Integer id, Pedido datos) {
        Optional<Pedido> pedidoExistente = pedidoRepository.findById(id);

        if (pedidoExistente.isEmpty()) {
            return null;
        }

        Pedido pedido = pedidoExistente.get();

        if (datos.getIdCliente() != null) {
            pedido.setIdCliente(datos.getIdCliente());
        }

        if (datos.getEstado() != null) {
            pedido.setEstado(datos.getEstado());
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            return false;
        }
        pedidoRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(Integer idCliente) {
        return pedidoRepository.findByIdCliente(idCliente);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorClienteYEstado(Integer idCliente, EstadoPedido estado) {
        return pedidoRepository.findByIdClienteAndEstado(idCliente, estado);
    }
}