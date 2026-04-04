package com.farmapresence;

import com.farmapresence.models.Empleado_Model;
import com.farmapresence.models.Horario_Model;
import com.farmapresence.models.TurnoProgramado_Model;
import com.farmapresence.models.Usuario_Model;
import com.farmapresence.repository.Empleado_Repository;
import com.farmapresence.repository.Horario_Repository;
import com.farmapresence.repository.TurnoProgramado_Repository;
import com.farmapresence.repository.Usuario_Repository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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
        // Esperar más tiempo para que la aplicación esté completamente lista
        try {
            Thread.sleep(10000); // 10 segundos de retraso
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Reintentar la siembra varias veces si falla por conexión
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                System.out.println("[DataSeeder] Intento " + (retryCount + 1) + " de " + maxRetries);

                System.out.println("[DataSeeder] Iniciando seed de datos de ejemplo (modo idempotente)...");

                // ── 1. HORARIOS ──────────────────────────────────────────────
                Optional<Horario_Model> horarioMananaOpt = horarioRepository.findByDescripcion("Turno Mañana");
                Horario_Model horarioManana;
                if (horarioMananaOpt.isEmpty()) {
                    horarioManana = new Horario_Model();
                    horarioManana.setDescripcion("Turno Mañana");
                    horarioManana.setHoraInicio1(LocalTime.of(7, 0));
                    horarioManana.setHoraFin1(LocalTime.of(12, 0));
                    horarioManana.setHoraInicio2(LocalTime.of(14, 0));
                    horarioManana.setHoraFin2(LocalTime.of(17, 0));
                    horarioRepository.save(horarioManana);
                    System.out.println("[DataSeeder] ✅ Horario 'Turno Mañana' creado");
                } else {
                    horarioManana = horarioMananaOpt.get();
                    System.out.println("[DataSeeder] ⏭️ Horario 'Turno Mañana' ya existe, omitiendo");
                }

                Optional<Horario_Model> horarioTardeOpt = horarioRepository.findByDescripcion("Turno Tarde");
                Horario_Model horarioTarde;
                if (horarioTardeOpt.isEmpty()) {
                    horarioTarde = new Horario_Model();
                    horarioTarde.setDescripcion("Turno Tarde");
                    horarioTarde.setHoraInicio1(LocalTime.of(13, 0));
                    horarioTarde.setHoraFin1(LocalTime.of(18, 0));
                    horarioTarde.setHoraInicio2(null);
                    horarioTarde.setHoraFin2(null);
                    horarioRepository.save(horarioTarde);
                    System.out.println("[DataSeeder] ✅ Horario 'Turno Tarde' creado");
                } else {
                    horarioTarde = horarioTardeOpt.get();
                    System.out.println("[DataSeeder] ⏭️ Horario 'Turno Tarde' ya existe, omitiendo");
                }

                Optional<Horario_Model> horarioCompletoOpt = horarioRepository.findByDescripcion("Turno Completo");
                Horario_Model horarioCompleto;
                if (horarioCompletoOpt.isEmpty()) {
                    horarioCompleto = new Horario_Model();
                    horarioCompleto.setDescripcion("Turno Completo");
                    horarioCompleto.setHoraInicio1(LocalTime.of(8, 0));
                    horarioCompleto.setHoraFin1(LocalTime.of(17, 0));
                    horarioCompleto.setHoraInicio2(null);
                    horarioCompleto.setHoraFin2(null);
                    horarioRepository.save(horarioCompleto);
                    System.out.println("[DataSeeder] ✅ Horario 'Turno Completo' creado");
                } else {
                    horarioCompleto = horarioCompletoOpt.get();
                    System.out.println("[DataSeeder] ⏭️ Horario 'Turno Completo' ya existe, omitiendo");
                }

                // ── 2. EMPLEADOS ─────────────────────────────────────────────
                Optional<Empleado_Model> emp1Opt = empleadoRepository.findByIdentificacion("1001234567");
                Empleado_Model emp1;
                if (emp1Opt.isEmpty()) {
                    emp1 = new Empleado_Model();
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
                    System.out.println("[DataSeeder] ✅ Empleado 'Carlos Andrés Pérez' creado");
                } else {
                    emp1 = emp1Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Empleado 'Carlos Andrés Pérez' ya existe, omitiendo");
                }

                Optional<Empleado_Model> emp2Opt = empleadoRepository.findByIdentificacion("1009876543");
                Empleado_Model emp2;
                if (emp2Opt.isEmpty()) {
                    emp2 = new Empleado_Model();
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
                    System.out.println("[DataSeeder] ✅ Empleado 'Laura Sofía Martínez' creado");
                } else {
                    emp2 = emp2Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Empleado 'Laura Sofía Martínez' ya existe, omitiendo");
                }

                Optional<Empleado_Model> emp3Opt = empleadoRepository.findByIdentificacion("1112233445");
                Empleado_Model emp3;
                if (emp3Opt.isEmpty()) {
                    emp3 = new Empleado_Model();
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
                    System.out.println("[DataSeeder] ✅ Empleado 'Jhon Sebastián Gómez' creado");
                } else {
                    emp3 = emp3Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Empleado 'Jhon Sebastián Gómez' ya existe, omitiendo");
                }

                Optional<Empleado_Model> emp4Opt = empleadoRepository.findByIdentificacion("1005566778");
                Empleado_Model emp4;
                if (emp4Opt.isEmpty()) {
                    emp4 = new Empleado_Model();
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
                    System.out.println("[DataSeeder] ✅ Empleado 'María Camila Torres' creado");
                } else {
                    emp4 = emp4Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Empleado 'María Camila Torres' ya existe, omitiendo");
                }

                // ── 3. TURNOS PROGRAMADOS ─────────────────────────────────────
                Optional<TurnoProgramado_Model> turno1Opt = turnoProgramadoRepository.findByEmpleadoIdAndFecha(emp1.getId(), LocalDate.now().plusDays(1));
                TurnoProgramado_Model turno1;
                if (turno1Opt.isEmpty()) {
                    turno1 = new TurnoProgramado_Model();
                    turno1.setFecha(LocalDate.now().plusDays(1));
                    turno1.setHoraInicio(LocalTime.of(7, 0));
                    turno1.setHoraFin(LocalTime.of(12, 0));
                    turno1.setEmpleado(emp1);
                    turnoProgramadoRepository.save(turno1);
                    System.out.println("[DataSeeder] ✅ Turno para 'Carlos Andrés Pérez' creado");
                } else {
                    turno1 = turno1Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Turno para 'Carlos Andrés Pérez' ya existe, omitiendo");
                }

                Optional<TurnoProgramado_Model> turno2Opt = turnoProgramadoRepository.findByEmpleadoIdAndFecha(emp2.getId(), LocalDate.now().plusDays(1));
                TurnoProgramado_Model turno2;
                if (turno2Opt.isEmpty()) {
                    turno2 = new TurnoProgramado_Model();
                    turno2.setFecha(LocalDate.now().plusDays(1));
                    turno2.setHoraInicio(LocalTime.of(13, 0));
                    turno2.setHoraFin(LocalTime.of(18, 0));
                    turno2.setEmpleado(emp2);
                    turnoProgramadoRepository.save(turno2);
                    System.out.println("[DataSeeder] ✅ Turno para 'Laura Sofía Martínez' creado");
                } else {
                    turno2 = turno2Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Turno para 'Laura Sofía Martínez' ya existe, omitiendo");
                }

                Optional<TurnoProgramado_Model> turno3Opt = turnoProgramadoRepository.findByEmpleadoIdAndFecha(emp3.getId(), LocalDate.now().plusDays(2));
                TurnoProgramado_Model turno3;
                if (turno3Opt.isEmpty()) {
                    turno3 = new TurnoProgramado_Model();
                    turno3.setFecha(LocalDate.now().plusDays(2));
                    turno3.setHoraInicio(LocalTime.of(8, 0));
                    turno3.setHoraFin(LocalTime.of(17, 0));
                    turno3.setEmpleado(emp3);
                    turnoProgramadoRepository.save(turno3);
                    System.out.println("[DataSeeder] ✅ Turno para 'Jhon Sebastián Gómez' creado");
                } else {
                    turno3 = turno3Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Turno para 'Jhon Sebastián Gómez' ya existe, omitiendo");
                }

                // ── 4. USUARIOS ───────────────────────────────────────────────
                Optional<Usuario_Model> adminOpt = usuarioRepository.findByUsername("admin");
                Usuario_Model admin;
                if (adminOpt.isEmpty()) {
                    admin = new Usuario_Model();
                    admin.setNombreCompleto("Administrador Farmacenter");
                    admin.setUsername("admin");
                    admin.setPassword(passwordEncoder.encode("Admin123"));
                    admin.setCorreoElectronico("admin@farmacenter.com");
                    admin.setRol("ADMIN");
                    admin.setTelefono("3000000001");
                    usuarioRepository.save(admin);
                    System.out.println("[DataSeeder] ✅ Usuario 'admin' creado");
                } else {
                    admin = adminOpt.get();
                    System.out.println("[DataSeeder] ⏭️ Usuario 'admin' ya existe, omitiendo");
                }

                Optional<Usuario_Model> user1Opt = usuarioRepository.findByUsername("caperez");
                Usuario_Model user1;
                if (user1Opt.isEmpty()) {
                    user1 = new Usuario_Model();
                    user1.setNombreCompleto("Carlos Andrés Pérez");
                    user1.setUsername("caperez");
                    user1.setPassword(passwordEncoder.encode("User1234"));
                    user1.setCorreoElectronico("carlos.perez@farmacenter.com");
                    user1.setRol("usuario");
                    user1.setTelefono("3101234567");
                    usuarioRepository.save(user1);
                    System.out.println("[DataSeeder] ✅ Usuario 'caperez' creado");
                } else {
                    user1 = user1Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Usuario 'caperez' ya existe, omitiendo");
                }

                Optional<Usuario_Model> user2Opt = usuarioRepository.findByUsername("lsmartinez");
                Usuario_Model user2;
                if (user2Opt.isEmpty()) {
                    user2 = new Usuario_Model();
                    user2.setNombreCompleto("Laura Sofía Martínez");
                    user2.setUsername("lsmartinez");
                    user2.setPassword(passwordEncoder.encode("User1234"));
                    user2.setCorreoElectronico("laura.martinez@farmacenter.com");
                    user2.setRol("usuario");
                    user2.setTelefono("3209876543");
                    usuarioRepository.save(user2);
                    System.out.println("[DataSeeder] ✅ Usuario 'lsmartinez' creado");
                } else {
                    user2 = user2Opt.get();
                    System.out.println("[DataSeeder] ⏭️ Usuario 'lsmartinez' ya existe, omitiendo");
                }

                System.out.println("[DataSeeder] ✅ Seed completado exitosamente (modo idempotente).");
                System.out.println("[DataSeeder] Credenciales de acceso:");
                System.out.println("[DataSeeder]   Admin  → usuario: admin    / contraseña: Admin123");
                System.out.println("[DataSeeder]   User1  → usuario: caperez  / contraseña: User1234");
                System.out.println("[DataSeeder]   User2  → usuario: lsmartinez / contraseña: User1234");
                return; // Salir exitosamente
            } catch (Exception e) {
                retryCount++;
                System.out.println("[DataSeeder] Error en intento " + retryCount + ": " + e.getMessage());

                if (retryCount >= maxRetries) {
                    System.out.println("[DataSeeder] ❌ Máximo de reintentos alcanzado. La aplicación continuará sin datos iniciales.");
                    System.out.println("[DataSeeder] Error final: " + e.getMessage());
                    // No relanzar la excepción para que la aplicación continúe
                    return;
                } else {
                    System.out.println("[DataSeeder] Reintentando en 5 segundos...");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }
}
