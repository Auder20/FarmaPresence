package com.farmapresence.services;

import com.farmapresence.models.Usuario_Model;
import com.farmapresence.repository.Usuario_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private Usuario_Repository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Intenta buscar por username primero, luego por correo
        Optional usuario = usuarioRepository.findByUsername(username);
        if (usuario.isEmpty()) {
            usuario = usuarioRepository.findByCorreoElectronico(username);
        }

        Usuario_Model user = (Usuario_Model) usuario.orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return new User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRol()))
        );
    }
}
