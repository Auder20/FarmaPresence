package Package.PHARMACY_PROJECT.Services;

import Package.PHARMACY_PROJECT.Models.Empleado_Model;
import Package.PHARMACY_PROJECT.Response;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SerialReaderService {

    private static final Logger log = 
        org.slf4j.LoggerFactory.getLogger(SerialReaderService.class);
    private static final String ARDUINO_PORT = System.getenv().getOrDefault("ARDUINO_PORT", "COM8");
    private SerialPort port;
    private Set<String> processedFingerprints = new HashSet<>(); // Para evitar duplicados

    @Autowired
    private Empleado_Services empleadoServices;

    @Autowired
    private Asistencia_Services asistenciaServices;

    public void startSerialCommunication() {
        try {
            port = configureSerialPort(ARDUINO_PORT);

            if (port == null || !port.openPort()) {
                log.warn("Puerto serial '{}' no disponible. El registro biométrico automático " +
                         "estará desactivado. El sistema continuará funcionando con registro manual.", 
                         ARDUINO_PORT);
                return;
            }

            log.info("Conectado al puerto serie: {}", ARDUINO_PORT);

            new Thread(this::readSerialData).start();
        } catch (Exception e) {
            log.error("Error al iniciar la comunicación serial: {}", e.getMessage());
        }
    }

    private SerialPort configureSerialPort(String portName) {
        SerialPort port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 2000);
        return port;
    }

    private void readSerialData() {
        log.info("Iniciando lectura de datos serial...");

        try (var inputStream = port.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while (true) {
                if (port.isOpen() && inputStream.available() > 0) {
                    bytesRead = inputStream.read(buffer);
                    if (bytesRead > 0) {
                        String data = new String(buffer, 0, bytesRead).trim();
                        log.debug("Dato recibido del Arduino: {}", data);

                        // Filtrar mensajes no deseados antes de procesar
                        if (data.equalsIgnoreCase("Imagen capturada correctamente.") ||
                                data.equalsIgnoreCase("Huella identificada exitosamente") ||
                                data.contains("Intentando identificar huella")) {
                            log.debug("Mensaje intermedio ignorado: {}", data);
                            continue;
                        }

                        // Procesar datos válidos
                        processBufferedData(data);
                    }
                }

                Thread.sleep(100); // Pequeña pausa
            }
        } catch (Exception e) {
            log.error("Error durante la comunicación serial: {}", e.getMessage());
        } finally {
            closeSerialPort();
        }
    }

    private void processBufferedData(String data) {
        log.debug("Entrando a processBufferedData con el dato: {}", data);

        // Normalización: eliminar puntos y espacios innecesarios
        data = data.replaceAll("\\.+", "").trim();
        log.debug("Dato normalizado: {}", data);

        // Ignorar mensajes específicos que no se procesan
        if (data.equalsIgnoreCase("Imagen capturada correctamente") ||
                data.equalsIgnoreCase("Huella identificada exitosamente") ||
                data.contains("Intentando identificar huella")) {
            log.debug("Mensaje intermedio ignorado: {}", data);
            return; // Salir del método sin procesar
        }

        if (data.startsWith("Huella registrada exitosamente ID:")) {
            log.debug("Detectado inicio de registro de nueva huella.");
            handleRegistroHuella(data);
        } else if (data.startsWith("Huella identificada ID:")) {
            log.debug("Detectada identificación de huella.");
            handleHuellaIdentificada(data);
        } else {
            log.debug("Mensaje no reconocido: {}", data);
        }
    }

    private void handleRegistroHuella(String data) {
        log.debug("Entrando a handleRegistroHuella con el dato: {}", data);
        try {
            String fingerprintId = extractIdFromRegistroMessage(data);
            log.debug("ID extraído del mensaje de registro: {}", fingerprintId);
            if (fingerprintId != null) {
                log.debug("Enviando ID al backend para registro.");
                sendFingerprintToBackend(fingerprintId); // Enviar al endpoint de registro
            } else {
                log.warn("Advertencia: ID de registro no válido. Ignorando.");
            }
        } catch (Exception e) {
            log.error("Error al manejar el registro de huella: {}", e.getMessage());
        }
    }

    private String extractIdFromRegistroMessage(String data) {
        log.debug("Entrando a extractIdFromRegistroMessage con el dato: {}", data);
        try {
            if (data.startsWith("Huella registrada exitosamente ID:")) {
                String id = data.split(":")[1].trim();
                log.debug("ID extraído exitosamente: {}", id);
                return id; // Extraer el ID después de '#'
            }
            log.debug("El dato no contiene un ID válido para registro.");
            return null;
        } catch (Exception e) {
            log.error("Error al extraer ID de registro del mensaje: {}", e.getMessage());
            return null;
        }
    }

    private String extractIdFromMessage(String data) {
        log.debug("Entrando a extractIdFromMessage con el dato: {}", data);
        try {
            if (data.startsWith("Huella identificada ID:")) {
                String id = data.split(":")[1].trim();
                log.debug("ID extraído exitosamente: {}", id);
                return id; // Extraer el ID después de los dos puntos
            }
            log.debug("El dato no contiene un ID válido para identificación.");
            return null;
        } catch (Exception e) {
            log.error("Error al extraer ID de huella del mensaje: {}", e.getMessage());
            return null;
        }
    }

    private void handleHuellaIdentificada(String data) {
        log.debug("Entrando a handleHuellaIdentificada con el dato: {}", data);
        try {
            String fingerprintId = extractIdFromMessage(data);
            log.debug("ID extraído del mensaje de identificación: {}", fingerprintId);
            if (fingerprintId != null) {
                log.debug("Enviando ID al backend para asistencia.");
                sendFingerprintToAsistencia(fingerprintId);
            } else {
                log.warn("Advertencia: ID duplicado o no válido detectado. Ignorando.");
            }
        } catch (Exception e) {
            log.error("Error al manejar huella identificada: {}", e.getMessage());
        }
    }

    private void sendFingerprintToAsistencia(String id) {
        try {
            log.info("Registrando asistencia para ID de huella dactilar: {}", id);
            
            // Llamada directa al servicio de asistencia
            Response<?> response = asistenciaServices.registrarEntrada(id);
            
            if (response != null) {
                log.info("Respuesta del servicio de asistencia: {}", response.getMessage());
            } else {
                log.error("Error: No se recibió respuesta del servicio de asistencia.");
            }
        } catch (Exception e) {
            log.error("Error al registrar asistencia: {}", e.getMessage());
        }
    }

    private void sendFingerprintToBackend(String huellaDactilar) {
        try {
            Empleado_Model empleado = new Empleado_Model();
            empleado.setHuellaDactilar(huellaDactilar);

            log.info("Registrando huella dactilar: {}", huellaDactilar);
            
            // Llamada directa al servicio de empleado
            Response<Empleado_Model> response = empleadoServices.registrarHuella(empleado);

            if (response != null) {
                log.info("Respuesta del servicio de empleado: {}", response.getMessage());
            } else {
                log.error("Error: No se recibió respuesta del servicio de empleado.");
            }
        } catch (Exception e) {
            log.error("Error al registrar huella dactilar: {}", e.getMessage());
        }
    }

    private void closeSerialPort() {
        if (port != null && port.isOpen()) {
            port.closePort();
            log.info("Puerto serial cerrado.");
        }
    }

}




