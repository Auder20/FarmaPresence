package com.farmapresence.repository;

import com.farmapresence.models.Usuario_Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List; // ← IMPORTANTE

@Repository
public interface Usuario_Repository extends JpaRepository<Usuario_Model, Long> {

    Optional<Usuario_Model> findByUsername(String username);

    Optional<Usuario_Model> findByCorreoElectronico(String correoElectronico);

    Optional<Usuario_Model> findByToken(String token);

    List<Usuario_Model> findAllByCorreoElectronico(String correoElectronico); // ← ESTE FALTA

    List<Usuario_Model> findAllByToken(String token); // ← ESTE TAMBIÉN
}
