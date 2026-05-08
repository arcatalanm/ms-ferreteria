package ferrefix.ms_proveedores.mapper;

import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.model.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public Proveedor toEntity(ProveedorRequestDTO requestDTO) {
        return Proveedor.builder()
                .rutProveedor(requestDTO.getRutProveedor())
                .dvProveedor(requestDTO.getDvProveedor().toUpperCase().charAt(0))
                .nombreProveedor(requestDTO.getNombreProveedor())
                .giroProveedor(requestDTO.getGiroProveedor())
                .direccionProveedor(requestDTO.getDireccionProveedor())
                .telefonoProveedor(requestDTO.getTelefonoProveedor())
                .correoProveedor(requestDTO.getCorreoProveedor())
                .build();
    }

    public Proveedor toEntity(ProveedorRequestDTO requestDTO, Integer idProveedor) {
        return Proveedor.builder()
                .idProveedor(idProveedor)
                .rutProveedor(requestDTO.getRutProveedor())
                .dvProveedor(requestDTO.getDvProveedor().toUpperCase().charAt(0))
                .nombreProveedor(requestDTO.getNombreProveedor())
                .giroProveedor(requestDTO.getGiroProveedor())
                .direccionProveedor(requestDTO.getDireccionProveedor())
                .telefonoProveedor(requestDTO.getTelefonoProveedor())
                .correoProveedor(requestDTO.getCorreoProveedor())
                .build();
    }

    public ProveedorResponseDTO toResponseDTO(Proveedor proveedor) {
        return ProveedorResponseDTO.builder()
                .idProveedor(proveedor.getIdProveedor())
                .rutProveedor(proveedor.getRutProveedor() + "-" + proveedor.getDvProveedor())
                .nombreProveedor(proveedor.getNombreProveedor())
                .giroProveedor(proveedor.getGiroProveedor())
                .direccionProveedor(proveedor.getDireccionProveedor())
                .telefonoProveedor(proveedor.getTelefonoProveedor())
                .correoProveedor(proveedor.getCorreoProveedor())
                .build();
    }
}