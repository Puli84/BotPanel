package com.botpanel.controller;

import com.botpanel.entity.Empresa;
import com.botpanel.service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Empresa>> listar() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Empresa> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Empresa> crear(@RequestBody CrearEmpresaRequest req) {
        return ResponseEntity.ok(
            empresaService.crear(
                req.getNombre(),
                req.getEmail(),
                req.getAdminEmail(),
                req.getAdminPassword(),
                req.getAdminNombre()
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // DTO interno
    static class CrearEmpresaRequest {
        private String nombre;
        private String email;
        private String adminNombre;
        private String adminEmail;
        private String adminPassword;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAdminNombre() { return adminNombre; }
        public void setAdminNombre(String adminNombre) { this.adminNombre = adminNombre; }

        public String getAdminEmail() { return adminEmail; }
        public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

        public String getAdminPassword() { return adminPassword; }
        public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
    }
}
