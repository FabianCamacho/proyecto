package com.tienda.service;

import com.tienda.domain.Usuario;
import com.tienda.domain.UsuarioRol;
import com.tienda.repository.UsuarioRepository;
import com.tienda.repository.UsuarioRolRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository) {

        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            throw new UsernameNotFoundException(
                    "El usuario no existe"
            );
        }

        List<UsuarioRol> roles =
                usuarioRolRepository.findByUsuario(usuario);

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (UsuarioRol usuarioRol : roles) {

            String nombreRol = usuarioRol.getRol().getRol();

            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + nombreRol)
            );
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(authorities)
                .disabled(!usuario.isActivo())
                .build();
    }
}