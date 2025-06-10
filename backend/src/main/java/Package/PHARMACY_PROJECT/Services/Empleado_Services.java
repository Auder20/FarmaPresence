package Package.PHARMACY_PROJECT.Services;

import Package.PHARMACY_PROJECT.Models.Empleado_Model;
import Package.PHARMACY_PROJECT.Repository.Empleado_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Empleado_Services {

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
    public Optional<Empleado_Model> getEmpleadoById(Long id) {
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
}
