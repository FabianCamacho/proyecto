package com.tienda.service;

import com.tienda.domain.Rol;
import com.tienda.domain.Usuario;
import com.tienda.domain.UsuarioRol;
import com.tienda.domain.UsuarioRolId;
import com.tienda.repository.RolRepository;
import com.tienda.repository.UsuarioRepository;
import com.tienda.repository.UsuarioRolRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // =========================================================
    // GUARDAR USUARIO NORMAL
    // =========================================================

    @Transactional
    public void save(Usuario usuario) {

        // Encriptar la contraseña
        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        // Guardar el usuario
        usuario = usuarioRepository.save(usuario);

        // Los usuarios que se registran normalmente reciben USER
        asignarRol(usuario, "USER");
    }

    // =========================================================
    // ASIGNAR ROL
    // =========================================================

    @Transactional
    public void asignarRol(Usuario usuario, String nombreRol) {

        Rol rol = rolRepository.findByRol(nombreRol);

        if (rol == null) {
            throw new IllegalArgumentException(
                    "El rol " + nombreRol + " no existe."
            );
        }

        UsuarioRolId id = new UsuarioRolId();

        id.setIdUsuario(usuario.getIdUsuario());
        id.setIdRol(rol.getIdRol());

        UsuarioRol usuarioRol = new UsuarioRol();

        usuarioRol.setId(id);
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        usuarioRolRepository.save(usuarioRol);
    }

    // =========================================================
    // CREAR USUARIO CON UN ROL ESPECÍFICO
    // =========================================================

    @Transactional
    public void saveConRol(Usuario usuario, String nombreRol) {

        // Encriptar la contraseña
        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        // Guardar usuario
        usuario = usuarioRepository.save(usuario);

        // Asignar el rol indicado
        asignarRol(usuario, nombreRol);
    }
}