package ferrefix.ms_inventorario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_inventorario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventorario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventorario.model.CategoriaProducto;
import ferrefix.ms_inventorario.repository.CategoriaProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaProductoService {
    private final CategoriaProductoRepository categoriaProductoRepository;

    public CategoriaProducto crearCategoriaProducto(CategoriaProductoRequestDTO dto) {
        if (categoriaProductoRepository.existsByNombreCategoria(dto.getNombreCategoria())) {
            throw new IllegalArgumentException("La categoría de producto '" + dto.getNombreCategoria() + "' ya existe");
        }

        CategoriaProducto categoria = CategoriaProducto.builder()
                .nombreCategoria(dto.getNombreCategoria())
                .build();

        return categoriaProductoRepository.save(categoria);
    }

    public List<CategoriaProductoResponseDTO> buscarTodasCategorias() {
        return categoriaProductoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaProductoResponseDTO buscarCategoriaPorId(Integer idCategoria) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la categoría de producto con id: " + idCategoria));
        return toResponse(categoria);
    }

    public CategoriaProducto actualizarCategoriaProducto(Integer idCategoria, CategoriaProductoRequestDTO dto) {
        CategoriaProducto categoriaExistente = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la categoría de producto con id: " + idCategoria));

        if (categoriaProductoRepository.existsByNombreCategoria(dto.getNombreCategoria())
                && !categoriaExistente.getNombreCategoria().equalsIgnoreCase(dto.getNombreCategoria())) {
            throw new IllegalArgumentException("La categoría de producto '" + dto.getNombreCategoria() + "' ya existe");
        }

        categoriaExistente.setNombreCategoria(dto.getNombreCategoria());
        return categoriaProductoRepository.save(categoriaExistente);
    }

    public void eliminarCategoriaProducto(Integer idCategoria) {
        if (!categoriaProductoRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException("No se encontró la categoría de producto con id: " + idCategoria);
        }
        categoriaProductoRepository.deleteById(idCategoria);
    }

    private CategoriaProductoResponseDTO toResponse(CategoriaProducto categoriaProducto) {
        return CategoriaProductoResponseDTO.builder()
                .idCategoria(categoriaProducto.getIdCategoria())
                .nombreCategoria(categoriaProducto.getNombreCategoria())
                .build();
    }
}
