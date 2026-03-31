package Package.PHARMACY_PROJECT;

import Package.PHARMACY_PROJECT.Services.SerialReaderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class PharmacyProjectApplication {	

	public static void main(String[] args) {
		// Obteniendo el contexto de Spring
		System.setProperty("java.awt.headless", "true");
		ApplicationContext context = SpringApplication.run(PharmacyProjectApplication.class, args);

		// Invocación manual del servicio e PUERTO COM  (solo si @PostConstruct no funciona)
		/*SerialReaderService serialReaderService = context.getBean(SerialReaderService.class);
		serialReaderService.startSerialCommunication();¨*/
		
		// Forzar ejecución del Dataseeder si no se ejecuta automáticamente
		try {
		    Dataseeder dataseeder = context.getBean(Dataseeder.class);
		    System.out.println("[Main] Dataseeder encontrado, ejecutando manualmente...");
		    new Thread(() -> {
		        try {
		            Thread.sleep(15000); // Esperar 15 segundos para que todo esté listo
		            dataseeder.run();
		        } catch (InterruptedException e) {
		            Thread.currentThread().interrupt();
		        } catch (Exception e) {
		            System.out.println("[Main] Error ejecutando Dataseeder manualmente: " + e.getMessage());
		        }
		    }).start();
		} catch (Exception e) {
		    System.out.println("[Main] Dataseeder no encontrado o error: " + e.getMessage());
		}
	}

}
