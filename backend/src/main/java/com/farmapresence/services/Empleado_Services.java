package com.farmapresence.services;

import com.farmapresence.models.Empleado_Model;
import com.farmapresence.repository.Empleado_Repository;
import com.farmapresence.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Empleado_Services {

    private static final Logger logger = LoggerFactory.getLogger(Empleado_Services.class);
    private final Empleado_Repository empleadoRepository;

    @Autowired
    public Empleado_Services(Empleado_Repository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado_Model> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    // Guardar o actualizar un empleado
    public Empleado_Model save(Empleado_Model empleado) {
        return empleadoRepository.save(empleado);
    }

    // Buscar por ID
    public Optional<Empleado_Model> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    // Buscar por huella dactilar
    public Optional<Empleado_Model> findByHuellaDactilar(String huellaDactilar) {
        return empleadoRepository.findByHuellaDactilar(huellaDactilar);
    }

    // Buscar por huella dactilar incluyendo el horario (requiere query personalizada en el repositorio)
    public Optional<Empleado_Model> findByHuellaDactilarWithHorario(String huellaDactilar) {
        return empleadoRepository.findByHuellaDactilarWithHorario(huellaDactilar);
    }

    // Obtener todas las huellas dactilares
    public List<String> getAllHuellas() {
        return empleadoRepository.findAll()
                .stream()
                .map(Empleado_Model::getHuellaDactilar)
                .collect(Collectors.toList());
    }

    // Guardar solo una huella dactilar (⚠️ cuidado con duplicados)
    public Empleado_Model saveHuella(Empleado_Model empleado) {
        Empleado_Model nuevoEmpleado = new Empleado_Model();
        nuevoEmpleado.setHuellaDactilar(empleado.getHuellaDactilar());
        return empleadoRepository.save(nuevoEmpleado);
    }

    // Obtener huella por ID
    public String getHuellaById(Long id) {
        Empleado_Model empleado = empleadoRepository.findById(id).orElse(null);
        return empleado != null ? empleado.getHuellaDactilar() : null;
    }

    // Obtener todos los empleados
    public List<Empleado_Model> findAll() {
        return empleadoRepository.findAll();
    }

    // Eliminar por ID
    public void deleteById(Long id) {
        empleadoRepository.deleteById(id);
    }

    // Buscar por identificación
    public Optional<Empleado_Model> findByIdentificacion(String identificacion) {
        return empleadoRepository.findByIdentificacion(identificacion);
    }

    // Eliminar por identificación
    public boolean deleteByIdentificacion(String identificacion) {
        Optional<Empleado_Model> empleado = empleadoRepository.findByIdentificacion(identificacion);
        if (empleado.isPresent()) {
            empleadoRepository.delete(empleado.get());
            return true;
        }
        return false;
    }

    // Eliminar todos y resetear auto-incremento
    @Transactional
    public void deleteAll() {
        empleadoRepository.deleteAll();
        empleadoRepository.resetAutoIncrement();
    }

    // Obtener huellas sin identificación
    public List<String> getHuellasSinIdentificacion() {
        List<Empleado_Model> empleadosSinIdentificacion = empleadoRepository.findByIdentificacionIsNullOrIdentificacion("");
        return empleadosSinIdentificacion.stream()
                .map(Empleado_Model::getHuellaDactilar)
                .collect(Collectors.toList());
    }

    // Obtener nombre por identificación
    public Optional<String> getNombreByIdentificacion(String identificacion) {
        Optional<Empleado_Model> empleadoOpt = empleadoRepository.findByIdentificacion(identificacion);
        return empleadoOpt.map(Empleado_Model::getNombre);
    }

    // Método para registrar huella dactilar
    public Response<Empleado_Model> registrarHuella(Empleado_Model empleado) {
        try {
            if (empleado == null || empleado.getHuellaDactilar() == null) {
                return new Response<>("400", "Huella dactilar es requerida", null, "HUELLA_REQUERIDA");
            }
            
            // Guardar huella en el empleado existente
            Empleado_Model empleadoExistente = findById(empleado.getId()).orElse(null);
            if (empleadoExistente == null) {
                return new Response<>("404", "Empleado no encontrado", null, "EMPLEADO_NOT_FOUND");
            }
            
            empleadoExistente.setHuellaDactilar(empleado.getHuellaDactilar());
            Empleado_Model actualizado = save(empleadoExistente);
            
            return new Response<>("200", "Huella registrada correctamente", actualizado, "HUELLA_REGISTRADA");
            
        } catch (Exception e) {
            logger.error("Error al registrar huella: {}", e.getMessage());
            return new Response<>("500", "Error al registrar huella", null, "ERROR_REGISTRO_HUELLA");
        }
    }
}
