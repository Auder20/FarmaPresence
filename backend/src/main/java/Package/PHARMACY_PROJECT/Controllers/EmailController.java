package Package.PHARMACY_PROJECT.PRUEBACORREOS;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Endpoint para enviar correo simple
    @PostMapping("/sendSimple")
    public ResponseEntity<String> sendSimpleEmail(@RequestParam String to,
                                                  @RequestParam String subject,
                                                  @RequestParam String text) {
        try {
            emailService.sendSimpleMessage(to, subject, text);
            return ResponseEntity.ok("Correo simple enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar correo simple: " + e.getMessage());
        }
    }

    // Endpoint para enviar correo con archivo adjunto
    @PostMapping("/sendWithAttachment")
    public ResponseEntity<String> sendEmailWithAttachment(@RequestParam String to,
                                                          @RequestParam String subject,
                                                          @RequestParam String text,
                                                          @RequestParam("file") MultipartFile multipartFile) {
        try {
            // Guardar archivo temporalmente
            File file = File.createTempFile("adjunto-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);

            emailService.sendMessageWithAttachment(to, subject, text, file);

            // Borrar archivo temporal después del envío
            file.delete();

            return ResponseEntity.ok("Correo con adjunto enviado correctamente");
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar correo con adjunto: " + e.getMessage());
        }
    }
}
