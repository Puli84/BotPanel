package com.botpanel.service;

import com.botpanel.dto.LoginRequest;
import com.botpanel.dto.LoginResponse;
import com.botpanel.dto.RegisterRequest;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.Rol;
import com.botpanel.repository.UsuarioRepository;
import com.botpanel.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    public LoginResponse login(LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtUtil.generateToken(usuario);

        return new LoginResponse(
            token,
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null
        );
    }

    public String registrarSuperAdmin(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(Rol.SUPER_ADMIN);

        usuarioRepository.save(usuario);
        return "Super Admin creado correctamente";
    }
}
