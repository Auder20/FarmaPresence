package com.farmapresence.services;

import com.farmapresence.models.Usuario_Model;
import com.farmapresence.repository.Usuario_Repository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
            u.setTokenExpiracion(null);
        }

        Usuario_Model usuario = usuarios.get(0); // Tomar uno (el primero)
        String token = UUID.randomUUID().toString();
        usuario.setToken(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusMinutes(30));

        usuarioRepository.saveAll(usuarios);

        String frontendUrl = System.getenv().getOrDefault("FRONTEND_URL", "https://farma-presence.vercel.app/");
        String resetLink = frontendUrl + "/reset-password?token=" + token;

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
        
        // Validar expiración del token
        if (usuario.getTokenExpiracion() == null || LocalDateTime.now().isAfter(usuario.getTokenExpiracion())) {
            throw new Exception("El enlace de recuperación ha expirado. Solicita uno nuevo.");
        }
        
        // Validar fortaleza de contraseña
        validarFortalezaContrasena(nuevaPassword);
        
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setToken(null); // Elimina token usado
        usuario.setTokenExpiracion(null); // Elimina expiración

        usuarioRepository.save(usuario);

        // Limpia posibles duplicados con el mismo token
        for (int i = 1; i < usuarios.size(); i++) {
            Usuario_Model extra = usuarios.get(i);
            extra.setToken(null);
            extra.setTokenExpiracion(null);
            usuarioRepository.save(extra);
        }
    }
    
    private void validarFortalezaContrasena(String password) throws Exception {
        if (password == null || password.length() < 8)
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        if (!password.matches(".*[A-Z].*"))
            throw new Exception("La contraseña debe contener al menos una letra mayúscula.");
        if (!password.matches(".*\\d.*"))
            throw new Exception("La contraseña debe contener al menos un número.");
    }
}
