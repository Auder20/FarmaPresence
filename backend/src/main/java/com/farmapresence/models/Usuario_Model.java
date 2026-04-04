package com.farmapresence.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario_Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// esto es para que el id se encrementre
    private long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String correoElectronico;
    private String rol; //ADMIN o usuario
    private String token;
    private String telefono;
    
    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;

    // Constructor vacío
    public Usuario_Model() {
    }

    public Usuario_Model(String correoElectronico, String nombreCompleto, String password, String rol, String token, String username, String telefono) {
        this.correoElectronico = correoElectronico;
        this.nombreCompleto = nombreCompleto;
        this.password = password;
        this.rol = rol;
        this.token = token;
        this.username = username;
        this.telefono = telefono;
    }

    // Getters y setters manuales para asegurar compatibilidad
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getTokenExpiracion() {
        return tokenExpiracion;
    }

    public void setTokenExpiracion(LocalDateTime tokenExpiracion) {
        this.tokenExpiracion = tokenExpiracion;
    }
}
