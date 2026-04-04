package com.farmapresence;

import com.farmapresence.controllers.Empleado_Controller;
import com.farmapresence.models.Empleado_Model;
import com.farmapresence.services.Empleado_Services;
import com.farmapresence.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmpleadoControllerTest {

    @Mock
    private Empleado_Services empleadoServices;

    @InjectMocks
    private Empleado_Controller empleadoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetEmpleadoByIdentificacion_Found() {
        Empleado_Model empleado = new Empleado_Model();
        empleado.setIdentificacion("123");
        when(empleadoServices.findByIdentificacion("123")).thenReturn(Optional.of(empleado));

        ResponseEntity<Response<Empleado_Model>> response = empleadoController.getEmpleadoByIdentificacion("123");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().getCode());
        assertEquals("123", response.getBody().getData().getIdentificacion());
    }

    @Test
    public void testGetEmpleadoByIdentificacion_NotFound() {
        when(empleadoServices.findByIdentificacion("999")).thenReturn(Optional.empty());

        ResponseEntity<Response<Empleado_Model>> response = empleadoController.getEmpleadoByIdentificacion("999");

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("404", response.getBody().getCode());
    }

    @Test
    public void testUpdateEmpleado_Success() {
        Empleado_Model existingEmpleado = new Empleado_Model();
        existingEmpleado.setHuellaDactilar("huella1");
        existingEmpleado.setIdentificacion("123");

        Empleado_Model updateData = new Empleado_Model();
        updateData.setNombre("Juan");
        updateData.setIdentificacion("123");
        updateData.setActivo(true);

        when(empleadoServices.findByHuellaDactilar("huella1")).thenReturn(Optional.of(existingEmpleado));
        when(empleadoServices.findByIdentificacion("123")).thenReturn(Optional.empty());
        when(empleadoServices.save(any(Empleado_Model.class))).thenReturn(existingEmpleado);

        ResponseEntity<Response<Empleado_Model>> response = empleadoController.updateEmpleado("huella1", updateData);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().getCode());
    }

    @Test
    public void testUpdateEmpleado_DuplicateIdentificacion() {
        Empleado_Model updateData = new Empleado_Model();
        updateData.setIdentificacion("123");

        when(empleadoServices.findByIdentificacion("123")).thenReturn(Optional.of(new Empleado_Model()));

        ResponseEntity<Response<Empleado_Model>> response = empleadoController.updateEmpleado("huella1", updateData);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().getCode());
    }
}
