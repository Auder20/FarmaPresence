package Package.PHARMACY_PROJECT.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String correoElectronico;
    private String telefono;
    private String rol;
}
