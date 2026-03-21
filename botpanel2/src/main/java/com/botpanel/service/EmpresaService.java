package com.botpanel.service;

import com.botpanel.entity.Empresa;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.Rol;
import com.botpanel.repository.EmpresaRepository;
import com.botpanel.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaService(EmpresaRepository empresaRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Empresa obtener(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    @Transactional
    public Empresa crear(String nombre, String email,
                         String adminEmail, String adminPassword, String adminNombre) {
        if (empresaRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe una empresa con ese email");
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setEmail(email);
        empresa = empresaRepository.save(empresa);

        Usuario admin = new Usuario();
        admin.setNombre(adminNombre);
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRol(Rol.ADMIN_EMPRESA);
        admin.setEmpresa(empresa);
        usuarioRepository.save(admin);

        return empresa;
    }

    public void eliminar(Long id) {
        empresaRepository.deleteById(id);
    }
}
