package com.softwarelogistic.servicio;

import com.softwarelogistic.modelo.Categoria;
import com.softwarelogistic.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ====================================================================
 * SERVICIO: LÓGICA DE NEGOCIO PARA CATEGORÍAS
 * ====================================================================
 */
@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public Categoria guardarCategoria(Categoria categoria) {
        if (categoria.getEstado() == null) {
            categoria.setEstado(true);
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Integer id, Categoria datos) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);

        if (categoriaExistente.isEmpty()) {
            return null;
        }

        Categoria categoria = categoriaExistente.get();
        categoria.setNombre(datos.getNombre());
        categoria.setDescripcion(datos.getDescripcion());
        
        if (datos.getEstado() != null) {
            categoria.setEstado(datos.getEstado());
        }

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public boolean eliminarCategoria(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        categoriaRepository.deleteById(id);
        return true;
    }
}