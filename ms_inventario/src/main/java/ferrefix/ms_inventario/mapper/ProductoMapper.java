package ferrefix.ms_inventario.mapper;

import org.springframework.stereotype.Component;

import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.model.Producto;
import ferrefix.ms_inventario.model.UnidadMedida;

@Component
public class ProductoMapper {
    // Metodo para convertir un DTO a una Entity
    public Producto toEntity(ProductoRequestDTO dto) {
        // Construimos la categoría del producto a partir del ID proporcionado en el DTO
        CategoriaProducto categoriaProducto = CategoriaProducto.builder()
            .idCategoria(dto.getCategoria())
            .build();

        // Construimos la unidad del producto a partir de su id
        UnidadMedida unidadMedida = UnidadMedida.builder()
            .idUnidadMedida(dto.getUnidadMedida())
            .build();

        // Construimos y Retornamos al Entity Producto 
        return Producto.builder()
            .categoriaProducto(categoriaProducto)
            .unidadMedida(unidadMedida)
            .codigoBarrasProducto(dto.getCodigoBarras())
            .nombreProducto(dto.getNombre())
            .stockProducto(dto.getStock())
            .precioVentaProducto(dto.getPrecioVenta())
            .build();   
    }

    // Metodo para convertir una Entity a DTO
    public ProductoResponseDTO toDTO(Producto entity) {
        return ProductoResponseDTO.builder()
            .id(entity.getIdProducto())
            .codigoBarras(entity.getCodigoBarrasProducto())
            .nombre(entity.getNombreProducto())
            .stock(entity.getStockProducto())
            .precioVenta(entity.getPrecioVentaProducto())
            .unidadMedida(entity.getUnidadMedida().getNombreUnidadMedida())
            .categoria(entity.getCategoriaProducto().getNombreCategoria())
            .build();
    }

    // Metodo para actualizar una Entity x un DTO
    public void updateEntity(Producto entity, ProductoRequestDTO dto) {
        // Construimos la categoría del producto a partir del ID proporcionado en el DTO
        CategoriaProducto categoriaProducto = CategoriaProducto.builder()
            .idCategoria(dto.getCategoria())
            .build();

        // Construimos la unidad del producto a partir de su id
        UnidadMedida unidadMedida = UnidadMedida.builder()
            .idUnidadMedida(dto.getUnidadMedida())
            .build();

        // EL ID NO LO CAMBIARE POR MOTIVOS DE EVITAR ERRORES EN LOS ID Y CHOQUES ENTRE ELLOS
        entity.setCodigoBarrasProducto(dto.getCodigoBarras());
        entity.setNombreProducto(dto.getNombre());
        entity.setStockProducto(dto.getStock());
        entity.setPrecioVentaProducto(dto.getPrecioVenta());
        entity.setUnidadMedida(unidadMedida);
        entity.setCategoriaProducto(categoriaProducto);
    }
}
