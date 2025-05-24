package Package.PHARMACY_PROJECT.Services;

import Package.PHARMACY_PROJECT.Models.Usuario_Model;
import Package.PHARMACY_PROJECT.Repository.Usuario_Repository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
        List<Usuario_Model> usuarios = usuarioRepository.findAllByCorreoElectronico(email);

        if (usuarios.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        // Limpiar cualquier token anterior
        for (Usuario_Model u : usuarios) {
            u.setToken(null);
        }

        Usuario_Model usuario = usuarios.get(0); // Tomar uno (el primero)
        String token = UUID.randomUUID().toString();
        usuario.setToken(token);

        usuarioRepository.saveAll(usuarios);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        String mensajeHtml = "<html><body>" +
                "<p>Hola <strong>" + usuario.getNombreCompleto() + "</strong>,</p>" +
                "<p>Recibimos una solicitud para restablecer tu contraseña. Para hacerlo, haz clic en el siguiente enlace:</p>" +
                "<p><a href=\"" + resetLink + "\">Restablecer contraseña</a></p>" +
                "<p>Si tú no solicitaste este cambio, puedes ignorar este mensaje.</p>" +
                "<p><em>Equipo de Soporte - Farmacenter</em></p>" +
                "</body></html>";

        emailService.sendHtmlMessage(
                usuario.getCorreoElectronico(),
                "Recuperación de contraseña",
                mensajeHtml
        );
    }

    @Transactional
    public void resetPassword(String token, String nuevaPassword) throws Exception {
        List<Usuario_Model> usuarios = usuarioRepository.findAllByToken(token);

        if (usuarios.isEmpty()) {
            throw new Exception("Token inválido o expirado");
        }

        Usuario_Model usuario = usuarios.get(0); // Usamos el primero
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setToken(null); // Elimina token usado

        usuarioRepository.save(usuario);

        // Limpia posibles duplicados con el mismo token
        for (int i = 1; i < usuarios.size(); i++) {
            Usuario_Model extra = usuarios.get(i);
            extra.setToken(null);
            usuarioRepository.save(extra);
        }
    }
}
