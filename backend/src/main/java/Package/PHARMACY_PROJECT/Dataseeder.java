package Package.PHARMACY_PROJECT;

import Package.PHARMACY_PROJECT.Models.Empleado_Model;
import Package.PHARMACY_PROJECT.Models.Horario_Model;
import Package.PHARMACY_PROJECT.Models.TurnoProgramado_Model;
import Package.PHARMACY_PROJECT.Models.Usuario_Model;
import Package.PHARMACY_PROJECT.Repository.Empleado_Repository;
import Package.PHARMACY_PROJECT.Repository.Horario_Repository;
import Package.PHARMACY_PROJECT.Repository.TurnoProgramado_Repository;
import Package.PHARMACY_PROJECT.Repository.Usuario_Repository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class Dataseeder implements CommandLineRunner {

    private final Usuario_Repository usuarioRepository;
    private final Empleado_Repository empleadoRepository;
    private final Horario_Repository horarioRepository;
    private final TurnoProgramado_Repository turnoProgramadoRepository;
    private final PasswordEncoder passwordEncoder;

    public Dataseeder(Usuario_Repository usuarioRepository,
                      Empleado_Repository empleadoRepository,
                      Horario_Repository horarioRepository,
                      TurnoProgramado_Repository turnoProgramadoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.horarioRepository = horarioRepository;
        this.turnoProgramadoRepository = turnoProgramadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Esperar un momento para que la aplicación esté completamente lista
        try {
            Thread.sleep(5000); // 5 segundos de retraso
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        try {
            // Solo siembra si no hay datos ya cargados
            if (usuarioRepository.count() > 0) {
                System.out.println("[DataSeeder] Ya existen datos. Seed omitido.");
                return;
            }

            System.out.println("[DataSeeder] Iniciando seed de datos de ejemplo...");

            // ── 1. HORARIOS ──────────────────────────────────────────────
            Horario_Model horarioManana = new Horario_Model();
            horarioManana.setDescripcion("Turno Mañana");
            horarioManana.setHoraInicio1(LocalTime.of(7, 0));
            horarioManana.setHoraFin1(LocalTime.of(12, 0));
            horarioManana.setHoraInicio2(LocalTime.of(14, 0));
            horarioManana.setHoraFin2(LocalTime.of(17, 0));
            horarioRepository.save(horarioManana);

            Horario_Model horarioTarde = new Horario_Model();
            horarioTarde.setDescripcion("Turno Tarde");
            horarioTarde.setHoraInicio1(LocalTime.of(13, 0));
            horarioTarde.setHoraFin1(LocalTime.of(18, 0));
            horarioTarde.setHoraInicio2(null);
            horarioTarde.setHoraFin2(null);
            horarioRepository.save(horarioTarde);

            Horario_Model horarioCompleto = new Horario_Model();
            horarioCompleto.setDescripcion("Turno Completo");
            horarioCompleto.setHoraInicio1(LocalTime.of(8, 0));
            horarioCompleto.setHoraFin1(LocalTime.of(17, 0));
            horarioCompleto.setHoraInicio2(null);
            horarioCompleto.setHoraFin2(null);
            horarioRepository.save(horarioCompleto);

            // ── 2. EMPLEADOS ─────────────────────────────────────────────
            Empleado_Model emp1 = new Empleado_Model();
            emp1.setNombre("Carlos Andrés Pérez");
            emp1.setIdentificacion("1001234567");
            emp1.setFechaContratacion(LocalDate.of(2022, 3, 15));
            emp1.setActivo(true);
            emp1.setRol("Químico Farmacéutico");
            emp1.setHuellaDactilar(null);
            emp1.setHorario(horarioManana);
            emp1.setTurnoProgramado(null);
            emp1.setTelefono("3101234567");
            empleadoRepository.save(emp1);

            Empleado_Model emp2 = new Empleado_Model();
            emp2.setNombre("Laura Sofía Martínez");
            emp2.setIdentificacion("1009876543");
            emp2.setFechaContratacion(LocalDate.of(2021, 7, 1));
            emp2.setActivo(true);
            emp2.setRol("Regente de Farmacia");
            emp2.setHuellaDactilar(null);
            emp2.setHorario(horarioTarde);
            emp2.setTurnoProgramado(null);
            emp2.setTelefono("3209876543");
            empleadoRepository.save(emp2);

            Empleado_Model emp3 = new Empleado_Model();
            emp3.setNombre("Jhon Sebastián Gómez");
            emp3.setIdentificacion("1112233445");
            emp3.setFechaContratacion(LocalDate.of(2023, 1, 10));
            emp3.setActivo(true);
            emp3.setRol("Auxiliar de Farmacia");
            emp3.setHuellaDactilar(null);
            emp3.setHorario(horarioCompleto);
            emp3.setTurnoProgramado(null);
            emp3.setTelefono("3151122334");
            empleadoRepository.save(emp3);

            Empleado_Model emp4 = new Empleado_Model();
            emp4.setNombre("María Camila Torres");
            emp4.setIdentificacion("1005566778");
            emp4.setFechaContratacion(LocalDate.of(2020, 11, 5));
            emp4.setActivo(false);
            emp4.setRol("Auxiliar de Farmacia");
            emp4.setHuellaDactilar(null);
            emp4.setHorario(horarioManana);
            emp4.setTurnoProgramado(null);
            emp4.setTelefono("3005566778");
            empleadoRepository.save(emp4);

            // ── 3. TURNOS PROGRAMADOS ─────────────────────────────────────
            TurnoProgramado_Model turno1 = new TurnoProgramado_Model();
            turno1.setFecha(LocalDate.now().plusDays(1));
            turno1.setHoraInicio(LocalTime.of(7, 0));
            turno1.setHoraFin(LocalTime.of(12, 0));
            turno1.setEmpleado(emp1);
            turnoProgramadoRepository.save(turno1);

            TurnoProgramado_Model turno2 = new TurnoProgramado_Model();
            turno2.setFecha(LocalDate.now().plusDays(1));
            turno2.setHoraInicio(LocalTime.of(13, 0));
            turno2.setHoraFin(LocalTime.of(18, 0));
            turno2.setEmpleado(emp2);
            turnoProgramadoRepository.save(turno2);

            TurnoProgramado_Model turno3 = new TurnoProgramado_Model();
            turno3.setFecha(LocalDate.now().plusDays(2));
            turno3.setHoraInicio(LocalTime.of(8, 0));
            turno3.setHoraFin(LocalTime.of(17, 0));
            turno3.setEmpleado(emp3);
            turnoProgramadoRepository.save(turno3);

            // ── 4. USUARIOS ───────────────────────────────────────────────
            // Admin principal
            Usuario_Model admin = new Usuario_Model();
            admin.setNombreCompleto("Administrador Farmacenter");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setCorreoElectronico("admin@farmacenter.com");
            admin.setRol("ADMIN");
            admin.setTelefono("3000000001");
            usuarioRepository.save(admin);

            // Usuario normal
            Usuario_Model user1 = new Usuario_Model();
            user1.setNombreCompleto("Carlos Andrés Pérez");
            user1.setUsername("caperez");
            user1.setPassword(passwordEncoder.encode("User1234"));
            user1.setCorreoElectronico("carlos.perez@farmacenter.com");
            user1.setRol("usuario");
            user1.setTelefono("3101234567");
            usuarioRepository.save(user1);

            Usuario_Model user2 = new Usuario_Model();
            user2.setNombreCompleto("Laura Sofía Martínez");
            user2.setUsername("lsmartinez");
            user2.setPassword(passwordEncoder.encode("User1234"));
            user2.setCorreoElectronico("laura.martinez@farmacenter.com");
            user2.setRol("usuario");
            user2.setTelefono("3209876543");
            usuarioRepository.save(user2);

            System.out.println("[DataSeeder] ✅ Seed completado exitosamente.");
            System.out.println("[DataSeeder] Credenciales de acceso:");
            System.out.println("[DataSeeder]   Admin  → usuario: admin    / contraseña: Admin123");
            System.out.println("[DataSeeder]   User1  → usuario: caperez  / contraseña: User1234");
            System.out.println("[DataSeeder]   User2  → usuario: lsmartinez / contraseña: User1234");
        } catch (Exception e) {
            System.out.println("[DataSeeder] Error al ejecutar el seed: " + e.getMessage());
            System.out.println("[DataSeeder] La aplicación continuará ejecutándose normalmente.");
            // No relanzar la excepción para que la aplicación continúe
        }
    }
}