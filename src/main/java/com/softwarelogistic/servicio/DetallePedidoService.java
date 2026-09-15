package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.DetallePedido;
import com.softwarelogistic.repository.DetallePedidoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: GESTIÓN DE LÍNEAS DE DETALLE DE PEDIDOS
 * ====================================================================
 */
@Service
public class DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Transactional(readOnly = true)
    public List<DetallePedido> listar() {
        return detallePedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DetallePedido> buscarPorId(Integer id) {
        return detallePedidoRepository.findById(id);
    }

    @Transactional
    public DetallePedido guardar(DetallePedido detalle) {
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }

        return detallePedidoRepository.save(detalle);
    }

    @Transactional
    public DetallePedido actualizar(Integer id, DetallePedido datos) {
        Optional<DetallePedido> detalleExistente = detallePedidoRepository.findById(id);

        if (detalleExistente.isEmpty()) {
            return null;
        }

        DetallePedido detalle = detalleExistente.get();

        if (datos.getCantidad() != null && datos.getCantidad() > 0) {
            detalle.setCantidad(datos.getCantidad());
        }

        if (datos.getPrecioUnitario() != null && datos.getPrecioUnitario().compareTo(BigDecimal.ZERO) >= 0) {
            detalle.setPrecioUnitario(datos.getPrecioUnitario());
        }

        if (datos.getIdPedido() != null) {
            detalle.setIdPedido(datos.getIdPedido());
        }

        if (datos.getIdProducto() != null) {
            detalle.setIdProducto(datos.getIdProducto());
        }

        return detallePedidoRepository.save(detalle);
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!detallePedidoRepository.existsById(id)) {
            return false;
        }
        detallePedidoRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> buscarPorPedido(Integer idPedido) {
        return detallePedidoRepository.findByIdPedido(idPedido);
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> buscarPorProducto(Integer idProducto) {
        return detallePedidoRepository.findByIdProducto(idProducto);
    }
}