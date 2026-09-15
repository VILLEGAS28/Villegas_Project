package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Inventario;
import com.softwarelogistic.modelo.Movimiento;
import com.softwarelogistic.modelo.TipoMovimiento;
import com.softwarelogistic.repository.InventarioRepository;
import com.softwarelogistic.repository.MovimientoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: KARDEX Y REGISTRO DE MOVIMIENTOS
 * ====================================================================
 * Controla la auditoría de entradas, salidas y ajustes modificando
 * en tiempo real el inventario en una única transacción atómica.
 */
@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Transactional(readOnly = true)
    public List<Movimiento> listar() {
        return movimientoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Movimiento buscarPorId(Integer id) {
        return movimientoRepository.findById(id).orElse(null);
    }

    /**
     * Registra un movimiento y actualiza las existencias en inventario.
     */
    @Transactional
    public Movimiento registrar(Movimiento movimiento) {

        if (movimiento.getIdProducto() == null) {
            throw new RuntimeException("Debe indicar el id del producto.");
        }

        if (movimiento.getIdUsuario() == null) {
            throw new RuntimeException("Debe indicar el id del usuario que realiza la operación.");
        }

        if (movimiento.getTipo() == null) {
            throw new RuntimeException("Debe indicar el tipo de movimiento (ENTRADA, SALIDA o AJUSTE).");
        }

        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor que cero.");
        }

        // Buscar inventario. Si no existe y es una ENTRADA, lo creamos automáticamente en 0
        Inventario inventario = inventarioRepository.findByIdProducto(movimiento.getIdProducto())
                .orElseGet(() -> {
                    if (movimiento.getTipo() == TipoMovimiento.ENTRADA || movimiento.getTipo() == TipoMovimiento.AJUSTE) {
                        Inventario nuevoInv = new Inventario();
                        nuevoInv.setIdProducto(movimiento.getIdProducto());
                        nuevoInv.setCantidad(0);
                        return inventarioRepository.save(nuevoInv);
                    }
                    throw new RuntimeException("No existe registro de inventario para el producto indicado.");
                });

        int stockAnterior = inventario.getCantidad() != null ? inventario.getCantidad() : 0;
        movimiento.setStockAnterior(stockAnterior);

        // 1. ENTRADA (Suma)
        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
            int stockNuevo = stockAnterior + movimiento.getCantidad();
            inventario.setCantidad(stockNuevo);
            movimiento.setStockNuevo(stockNuevo);
        }

        // 2. SALIDA (Resta con validación de stock suficiente)
        else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {
            if (movimiento.getCantidad() > stockAnterior) {
                throw new RuntimeException("Stock insuficiente en bodega. Stock actual: " + stockAnterior + ", Cantidad solicitada: " + movimiento.getCantidad());
            }

            int stockNuevo = stockAnterior - movimiento.getCantidad();
            inventario.setCantidad(stockNuevo);
            movimiento.setStockNuevo(stockNuevo);
        }

        // 3. AJUSTE (Establece el conteo físico exacto)
        else if (movimiento.getTipo() == TipoMovimiento.AJUSTE) {
            int stockNuevo = movimiento.getCantidad();
            inventario.setCantidad(stockNuevo);
            movimiento.setStockNuevo(stockNuevo);
        }

        // Guardar ambos registros en la misma transacción
        inventarioRepository.save(inventario);
        return movimientoRepository.save(movimiento);
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!movimientoRepository.existsById(id)) {
            return false;
        }
        movimientoRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Movimiento> buscarPorProducto(Integer idProducto) {
        return movimientoRepository.findByIdProducto(idProducto);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> buscarPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> buscarPorProductoYTipo(Integer idProducto, TipoMovimiento tipo) {
        return movimientoRepository.findByIdProductoAndTipo(idProducto, tipo);
    }
}