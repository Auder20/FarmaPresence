package Package.PHARMACY_PROJECT.Services;

import Package.PHARMACY_PROJECT.Models.Empleado_Model;
import Package.PHARMACY_PROJECT.Response;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SerialReaderService {

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
                System.err.println("Error: No se pudo abrir el puerto " + ARDUINO_PORT);
                return;
            }

            System.out.println("Conectado al puerto serie: " + ARDUINO_PORT);

            new Thread(this::readSerialData).start();
        } catch (Exception e) {
            System.err.println("Error al iniciar la comunicación serial: " + e.getMessage());
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
        System.out.println("Iniciando lectura de datos serial...");

        try (var inputStream = port.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while (true) {
                if (port.isOpen() && inputStream.available() > 0) {
                    bytesRead = inputStream.read(buffer);
                    if (bytesRead > 0) {
                        String data = new String(buffer, 0, bytesRead).trim();
                        System.out.println("Dato recibido del Arduino: " + data);

                        // Filtrar mensajes no deseados antes de procesar
                        if (data.equalsIgnoreCase("Imagen capturada correctamente.") ||
                                data.equalsIgnoreCase("Huella identificada exitosamente") ||
                                data.contains("Intentando identificar huella")) {
                            System.out.println("Mensaje intermedio ignorado: " + data);
                            continue;
                        }

                        // Procesar datos válidos
                        processBufferedData(data);
                    }
                }

                Thread.sleep(100); // Pequeña pausa
            }
        } catch (Exception e) {
            System.err.println("Error durante la comunicación serial: " + e.getMessage());
        } finally {
            closeSerialPort();
        }
    }

    private void processBufferedData(String data) {
        System.out.println("Entrando a processBufferedData con el dato: " + data);

        // Normalización: eliminar puntos y espacios innecesarios
        data = data.replaceAll("\\.+", "").trim();
        System.out.println("Dato normalizado: " + data);

        // Ignorar mensajes específicos que no se procesan
        if (data.equalsIgnoreCase("Imagen capturada correctamente") ||
                data.equalsIgnoreCase("Huella identificada exitosamente") ||
                data.contains("Intentando identificar huella")) {
            System.out.println("Mensaje intermedio ignorado: " + data);
            return; // Salir del método sin procesar
        }

        if (data.startsWith("Huella registrada exitosamente ID:")) {
            System.out.println("Detectado inicio de registro de nueva huella.");
            handleRegistroHuella(data);
        } else if (data.startsWith("Huella identificada ID:")) {
            System.out.println("Detectada identificación de huella.");
            handleHuellaIdentificada(data);
        } else {
            System.out.println("Mensaje no reconocido: " + data);
        }
    }

    private void handleRegistroHuella(String data) {
        System.out.println("Entrando a handleRegistroHuella con el dato: " + data);
        try {
            String fingerprintId = extractIdFromRegistroMessage(data);
            System.out.println("ID extraído del mensaje de registro: " + fingerprintId);
            if (fingerprintId != null) {
                System.out.println("Enviando ID al backend para registro.");
                sendFingerprintToBackend(fingerprintId); // Enviar al endpoint de registro
            } else {
                System.out.println("Advertencia: ID de registro no válido. Ignorando.");
            }
        } catch (Exception e) {
            System.err.println("Error al manejar el registro de huella: " + e.getMessage());
        }
    }

    private String extractIdFromRegistroMessage(String data) {
        System.out.println("Entrando a extractIdFromRegistroMessage con el dato: " + data);
        try {
            if (data.startsWith("Huella registrada exitosamente ID:")) {
                String id = data.split(":")[1].trim();
                System.out.println("ID extraído exitosamente: " + id);
                return id; // Extraer el ID después de '#'
            }
            System.out.println("El dato no contiene un ID válido para registro.");
            return null;
        } catch (Exception e) {
            System.err.println("Error al extraer ID de registro del mensaje: " + e.getMessage());
            return null;
        }
    }

    private String extractIdFromMessage(String data) {
        System.out.println("Entrando a extractIdFromMessage con el dato: " + data);
        try {
            if (data.startsWith("Huella identificada ID:")) {
                String id = data.split(":")[1].trim();
                System.out.println("ID extraído exitosamente: " + id);
                return id; // Extraer el ID después de los dos puntos
            }
            System.out.println("El dato no contiene un ID válido para identificación.");
            return null;
        } catch (Exception e) {
            System.err.println("Error al extraer ID de huella del mensaje: " + e.getMessage());
            return null;
        }
    }

    private void handleHuellaIdentificada(String data) {
        System.out.println("Entrando a handleHuellaIdentificada con el dato: " + data);
        try {
            String fingerprintId = extractIdFromMessage(data);
            System.out.println("ID extraído del mensaje de identificación: " + fingerprintId);
            if (fingerprintId != null) {
                System.out.println("Enviando ID al backend para asistencia.");
                sendFingerprintToAsistencia(fingerprintId);
            } else {
                System.out.println("Advertencia: ID duplicado o no válido detectado. Ignorando.");
            }
        } catch (Exception e) {
            System.err.println("Error al manejar huella identificada: " + e.getMessage());
        }
    }

    private void sendFingerprintToAsistencia(String id) {
        try {
            System.out.println("Registrando asistencia para ID de huella dactilar: " + id);
            
            // Llamada directa al servicio de asistencia
            Response<?> response = asistenciaServices.registrarEntrada(id);
            
            if (response != null) {
                System.out.println("Respuesta del servicio de asistencia: " + response.getMessage());
            } else {
                System.err.println("Error: No se recibió respuesta del servicio de asistencia.");
            }
        } catch (Exception e) {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
        }
    }

    private void sendFingerprintToBackend(String huellaDactilar) {
        try {
            Empleado_Model empleado = new Empleado_Model();
            empleado.setHuellaDactilar(huellaDactilar);

            System.out.println("Registrando huella dactilar: " + huellaDactilar);
            
            // Llamada directa al servicio de empleado
            Response<Empleado_Model> response = empleadoServices.registrarHuella(empleado);

            if (response != null) {
                System.out.println("Respuesta del servicio de empleado: " + response.getMessage());
            } else {
                System.err.println("Error: No se recibió respuesta del servicio de empleado.");
            }
        } catch (Exception e) {
            System.err.println("Error al registrar huella dactilar: " + e.getMessage());
        }
    }

    private void closeSerialPort() {
        if (port != null && port.isOpen()) {
            port.closePort();
            System.out.println("Puerto serial cerrado.");
        }
    }

}




