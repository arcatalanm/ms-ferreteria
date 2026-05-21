package ferrefix.ms_proveedores.mapper;


import ferrefix.ms_proveedores.dto.DireccionDTO;
import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.model.Proveedor;
import ferrefix.ms_proveedores.util.RutUtil;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public Proveedor toEntity(ProveedorRequestDTO dto, Integer run, Character dv) {
        return Proveedor.builder()
                .rutProveedor(run)
                .dvProveedor(dv)
                .nombreProveedor(dto.getNombreProveedor())
                .giroProveedor(dto.getGiroProveedor())
                .direccionProveedor(dto.getDireccionProveedor())
                .telefonoProveedor(dto.getTelefonoProveedor())
                .correoProveedor(dto.getCorreoProveedor())
                .build();
    }

    public void updateEntity(Proveedor existing, ProveedorRequestDTO dto, Integer run, Character dv) {
        // El id (PK autoincremental) no se toca pero el rut puede cambiar si pasa por validcaiones
        existing.setRutProveedor(run);
        existing.setDvProveedor(dv);
        existing.setNombreProveedor(dto.getNombreProveedor());
        existing.setGiroProveedor(dto.getGiroProveedor());
        existing.setDireccionProveedor(dto.getDireccionProveedor());
        existing.setTelefonoProveedor(dto.getTelefonoProveedor());
        existing.setCorreoProveedor(dto.getCorreoProveedor());
    }

    public ProveedorResponseDTO toResponseDTO(Proveedor proveedor, DireccionDTO dto) {
        String direccion = "Dirección no disponible";

        if (dto != null) {
            direccion = dto.getCalle() + " " + dto.getNumero();
            // Bug corregido: era isBlank(), debe ser !isBlank()
            if (dto.getDepartamento() != null && !dto.getDepartamento().isBlank()) {
                direccion += " Depto. " + dto.getDepartamento();
            }
        }

        return ProveedorResponseDTO.builder()
                .idProveedor(proveedor.getIdProveedor())
                .rutProveedor(RutUtil.formatear(proveedor.getRutProveedor(), proveedor.getDvProveedor()))
                .nombreProveedor(proveedor.getNombreProveedor())
                .giroProveedor(proveedor.getGiroProveedor())
                .direccionProveedor(direccion)
                .telefonoProveedor(proveedor.getTelefonoProveedor())
                .correoProveedor(proveedor.getCorreoProveedor())
                .build();
    }
}