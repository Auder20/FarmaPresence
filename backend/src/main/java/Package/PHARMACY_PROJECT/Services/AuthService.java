package Package.PHARMACY_PROJECT.Services;

import Package.PHARMACY_PROJECT.Models.Usuario_Model;
import Package.PHARMACY_PROJECT.Repository.Usuario_Repository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final Usuario_Repository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(Usuario_Repository usuarioRepository,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void sendPasswordResetToken(String email) throws Exception {
        Optional<Usuario_Model> usuarioOpt = usuarioRepository.findByCorreoElectronico(email);

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        Usuario_Model usuario = usuarioOpt.get();
        String token = UUID.randomUUID().toString();

        usuario.setToken(token);
        usuarioRepository.save(usuario);

        String resetLink = "https://tuapp.com/reset-password?token=" + token;
        String mensaje = "Hola, para restablecer tu contraseña usa el siguiente enlace:\n" + resetLink;

        emailService.sendSimpleMessage(usuario.getCorreoElectronico(), "Recuperar contraseña", mensaje);
    }

    @Transactional
    public void resetPassword(String token, String nuevaPassword) throws Exception {
        Optional<Usuario_Model> usuarioOpt = usuarioRepository.findByToken(token);

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Token inválido o expirado");
        }

        Usuario_Model usuario = usuarioOpt.get();

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setToken(null); // Limpia token después de usar
        usuarioRepository.save(usuario);
    }
}
