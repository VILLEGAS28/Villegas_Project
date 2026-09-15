package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Cliente;
import com.softwarelogistic.repository.ClienteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: LÓGICA DE NEGOCIO PARA CLIENTES
 * ====================================================================
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return clienteRepository.existsByCorreo(correo);
    }

    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getEstado() == null) {
            cliente.setEstado(true);
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(Integer id, Cliente datos) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        if (clienteExistente.isEmpty()) {
            return null;
        }

        Cliente cliente = clienteExistente.get();
        cliente.setNombre(datos.getNombre());
        cliente.setTelefono(datos.getTelefono());
        cliente.setCorreo(datos.getCorreo());
        cliente.setDireccion(datos.getDireccion());
        cliente.setCiudad(datos.getCiudad());

        if (datos.getEstado() != null) {
            cliente.setEstado(datos.getEstado());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public boolean eliminarCliente(Integer id) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        if (clienteExistente.isEmpty()) {
            return false;
        }

        try {
            // Eliminación física directa
            clienteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Protección: Si tiene órdenes amarradas, se inactiva para no romper integridad
            Cliente cliente = clienteExistente.get();
            cliente.setEstado(false);
            clienteRepository.save(cliente);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorCiudad(String ciudad) {
        return clienteRepository.findByCiudadContainingIgnoreCase(ciudad);
    }
}