package Package.PHARMACY_PROJECT.Controllers;

import Package.PHARMACY_PROJECT.Models.Usuario_Model;
import Package.PHARMACY_PROJECT.DTOs.UsuarioDTO;
import Package.PHARMACY_PROJECT.Response;
import Package.PHARMACY_PROJECT.Services.Usuario_Services;
import Package.PHARMACY_PROJECT.Services.JwtService;
import Package.PHARMACY_PROJECT.Services.RateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = {"http://localhost:4200", "https://farma-presence.vercel.app", "https://farmapresence.onrender.com"})
public class Usuario_Controller {
    private static final Logger logger = LoggerFactory.getLogger(Usuario_Controller.class);

    @Autowired
    private Usuario_Services usersServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RateLimiterService rateLimiterService;



    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<List<UsuarioDTO>>> getAllUsuarios() {
        List<Usuario_Model> usuarios = usersServices.findAll();
        List<UsuarioDTO> dtos = usuarios.stream()
                .map(this::convertToDTO)
                .toList();
        Response<List<UsuarioDTO>> response = new Response<>("200", "Usuarios obtenidos", dtos, "USUARIOS_GET_OK");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "FarmaPresence Backend");
        response.put("timestamp", new Date().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<Map<String, Object>>> login(
            @RequestBody Usuario_Model loginRequest,
            jakarta.servlet.http.HttpServletRequest request) {
        
        // Rate limiting - 5 intentos por IP por minuto
        String clientIp = getClientIp(request);
        if (!rateLimiterService.tryConsume(clientIp)) {
            long tokensDisponibles = rateLimiterService.getAvailableTokens(clientIp);
            Response<Map<String, Object>> response = new Response<>(
                "429",
                "Demasiados intentos de inicio de sesión. Por favor espera 1 minuto antes de intentarlo de nuevo.",
                null,
                "TOO_MANY_REQUESTS"
            );
            return ResponseEntity.status(429).body(response);
        }
        
        Optional<Usuario_Model> usuario = Optional.empty();

        // Comprobamos si el "username" es un correo electrónico
        if (isValidEmail(loginRequest.getUsername())) {
            // Si es un correo electrónico, buscamos por correo
            usuario = usersServices.findByCorreoElectronico(loginRequest.getUsername());
        } else {
            // Si no es un correo, buscamos por nombre de usuario
            usuario = usersServices.findByUsername(loginRequest.getUsername());
        }

        logger.info("Login intento para usuario: {}", loginRequest.getUsername());

        if (usuario.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), usuario.get().getPassword())) {
            // Generar JWT token
            String token = jwtService.generateToken(usuario.get().getUsername());
            
            // Crear respuesta con DTO y token
            UsuarioDTO userDTO = convertToDTO(usuario.get());
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("usuario", userDTO);
            responseData.put("token", token);
            
            Response<Map<String, Object>> response = new Response<>("200", "Login exitoso", responseData, "LOGIN_SUCCESS");
            return ResponseEntity.ok(response);
        } else {
            Response<Map<String, Object>> response = new Response<>("401", "Usuario o contraseña incorrectos", null, "LOGIN_FAILURE");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


   @PostMapping
   @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Response<UsuarioDTO>> saveUsuarios(@RequestBody Usuario_Model usuario) {
    // Log para verificar que teléfono llega
    logger.info("Telefono recibido: {}", usuario.getTelefono());

    try {
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El nombre no puede ser nulo", null, "NOMBRE_NULO"));
        }

        if (usuario.getCorreoElectronico() == null || usuario.getCorreoElectronico().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El correo electrónico no puede ser nulo", null, "CORREO_NULO"));
        }

        if (usuario.getUsername() == null || usuario.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El ussername no puede ser nulo", null, "USERNAME_NULO"));
        }

        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "la contraseña no puede ser nulo", null, "NOMBRE_NULO"));
        }
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El rol no puede ser nulo", null, "NOMBRE_NULO"));
        }

        Optional<Usuario_Model> usuarioExistentePorUsername = usersServices.findByUsername(usuario.getUsername());
        if (usuarioExistentePorUsername.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El nombre de usuario ya está en uso", null, "USERNAME_DUPLICADO"));
        }

        if (!esCorreoValido(usuario.getCorreoElectronico())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El formato del correo es inválido", null, "CORREO_INVALIDO"));
        }

        Optional<Usuario_Model> usuarioExistentePorCorreo = usersServices.findByCorreoElectronico(usuario.getCorreoElectronico());
        if (usuarioExistentePorCorreo.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "El correo electrónico ya está en uso", null, "CORREO_DUPLICADO"));
        }
         // Encriptar la contraseña
         validarFortalezaContrasena(usuario.getPassword());
         usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario_Model usuarioNuevo = usersServices.save(usuario);
        UsuarioDTO dto = convertToDTO(usuarioNuevo);
        Response<UsuarioDTO> response = new Response<>("200", "Usuario creado satisfactoriamente", dto, "USUARIO_INSERT_OK");
        return ResponseEntity.created(new URI("/usuario/" + usuarioNuevo.getId())).body(response);

    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response<>("400", "Error al crear usuario: " + e.getMessage(), null, "USUARIO_INSERT_ERROR"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>("500", "Error interno del servidor: " + e.getMessage(), null, "INTERNAL_SERVER_ERROR"));
    }
}



    public boolean esCorreoValido(String correo) {
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regexCorreo);
        return pattern.matcher(correo).matches();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Void>> deleteUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario_Model> usuario = usersServices.findById(id);
            if (usuario.isPresent()) {
                usersServices.deleteById(id);
                // Si la eliminación fue exitosa, devolvemos un mensaje indicando que fue exitosa
                Response<Void> response = new Response<>("200", "Usuario eliminado satisfactoriamente", null, "USER_DELETE_OK");
                return ResponseEntity.ok().body(response);
            } else {
                // Si el usuario no fue encontrado, devolvemos un mensaje específico
                Response<Void> response = new Response<>("404", "El usuario no fue encontrado", null, "USER_NOT_FOUND");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            // Si ocurre un error inesperado, devolvemos un mensaje de error interno del servidor
            Response<Void> response = new Response<>("500", "Error interno del servidor: " + e.getMessage(), null, "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

   @PutMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public ResponseEntity<Response<UsuarioDTO>> update(@PathVariable Long id, @RequestBody Usuario_Model newUser) {
    if (id == null) {
        Response<UsuarioDTO> response = new Response<>("400", "El ID no puede ser nulo", null, "ID_NULL_ERROR");
        return ResponseEntity.badRequest().body(response);
    }

    Optional<Usuario_Model> optionalUser = usersServices.findById(id);

    if (optionalUser.isPresent()) {
        Usuario_Model existingUser = optionalUser.get();

        existingUser.setUsername(newUser.getUsername());

        // Solo actualiza la contraseña si NO viene vacía ni nula
         if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
            try {
                validarFortalezaContrasena(newUser.getPassword());
                existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            } catch (Exception e) {
                logger.error("Error de validación de contraseña: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>("400", e.getMessage(), null, "WEAK_PASSWORD"));
            }
        }


        existingUser.setNombreCompleto(newUser.getNombreCompleto());
        existingUser.setTelefono(newUser.getTelefono());
        existingUser.setCorreoElectronico(newUser.getCorreoElectronico());
        existingUser.setRol(newUser.getRol());

        Usuario_Model updatedUser = usersServices.save(existingUser);
        UsuarioDTO dto = convertToDTO(updatedUser);
        Response<UsuarioDTO> response = new Response<>("200", "Usuario actualizado satisfactoriamente", dto, "USUARIO_UPDATE_OK");
        return ResponseEntity.ok(response);
    } else {
        Response<UsuarioDTO> response = new Response<>("404", "Usuario no encontrado con ID: " + id, null, "USER_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

    @GetMapping("/{id}")
    public ResponseEntity<Response<UsuarioDTO>> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario_Model> usuario = usersServices.findById(id);

        if (usuario.isPresent()) {
            UsuarioDTO usuarioDTO = convertToDTO(usuario.get());
            Response<UsuarioDTO> response = new Response<>("200", "Usuario encontrado", usuarioDTO, "USER_FOUND");
            return ResponseEntity.ok(response);
        } else {
            Response<UsuarioDTO> response = new Response<>("404", "Usuario no encontrado", null, "USER_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/update-password/{id}")
public ResponseEntity<Response<String>> updatePasswordById(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload) {
    try {
        String newPassword = payload.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "La nueva contraseña no puede estar vacía", null, "EMPTY_PASSWORD"));
        }
        
        // Validar fortaleza de contraseña
        try {
            validarFortalezaContrasena(newPassword);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", e.getMessage(), null, "WEAK_PASSWORD"));
        }

        Optional<Usuario_Model> userOptional = usersServices.findById(id);

        if (userOptional.isPresent()) {
            Usuario_Model user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));  // <-- cifrar aquí
            usersServices.save(user);

            return ResponseEntity.ok(new Response<>("200", "Contraseña actualizada correctamente", null, "PASSWORD_UPDATED"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>("404", "Usuario no encontrado", null, "USER_NOT_FOUND"));
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>("500", "Error al actualizar la contraseña: " + e.getMessage(), null, "UPDATE_ERROR"));
    }
}

 @PutMapping("/change-password/{id}")
public ResponseEntity<Response<String>> changePassword(
        @PathVariable Long id,
        @RequestBody Map<String, String> passwords) {

    String contrasenaActual = passwords.get("contrasenaActual");
    String nuevaContrasena = passwords.get("nuevaContrasena");

    if (contrasenaActual == null || nuevaContrasena == null || contrasenaActual.isEmpty() || nuevaContrasena.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response<>("400", "Las contraseñas no pueden estar vacías", null, "EMPTY_PASSWORDS"));
    }

    Optional<Usuario_Model> userOptional = usersServices.findById(id);

    if (userOptional.isPresent()) {
        Usuario_Model user = userOptional.get();

        // Validar que la contraseña actual coincida con la guardada (hash)
        if (!passwordEncoder.matches(contrasenaActual, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>("401", "La contraseña actual es incorrecta", null, "INVALID_CURRENT_PASSWORD"));
        }
        
        // Validar fortaleza de nueva contraseña
        try {
            validarFortalezaContrasena(nuevaContrasena);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", e.getMessage(), null, "WEAK_PASSWORD"));
        }

        // Actualizar la contraseña cifrada
        user.setPassword(passwordEncoder.encode(nuevaContrasena));
        usersServices.save(user);

        return ResponseEntity.ok(new Response<>("200", "Contraseña actualizada correctamente", null, "PASSWORD_UPDATED_SUCCESS"));
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response<>("404", "Usuario no encontrado", null, "USER_NOT_FOUND"));
    }
}

    // Helper method to convert Usuario_Model to UsuarioDTO
    private UsuarioDTO convertToDTO(Usuario_Model usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setTelefono(usuario.getTelefono());
        dto.setRol(usuario.getRol());
        return dto;
    }
    
    private void validarFortalezaContrasena(String password) throws Exception {
        if (password == null || password.length() < 8)
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        if (!password.matches(".*[A-Z].*"))
            throw new Exception("La contraseña debe contener al menos una letra mayúscula.");
        if (!password.matches(".*\\d.*"))
            throw new Exception("La contraseña debe contener al menos un número.");
    }
    
    private String getClientIp(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    // Método estático para validar email
    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}