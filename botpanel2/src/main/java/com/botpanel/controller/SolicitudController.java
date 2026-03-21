package com.botpanel.controller;

import com.botpanel.entity.Solicitud;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.EstadoSolicitud;
import com.botpanel.enums.Rol;
import com.botpanel.repository.SolicitudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudRepository solicitudRepository;

    public SolicitudController(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> listar(
            @AuthenticationPrincipal Usuario usuario) {
        if (usuario.getRol() == Rol.SUPER_ADMIN)
            return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(
            solicitudRepository.findByBotEmpresaIdOrderByCreadoEnDesc(
                usuario.getEmpresa().getId()));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Solicitud> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Solicitud s = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        s.setEstado(EstadoSolicitud.valueOf(body.get("estado")));
        return ResponseEntity.ok(solicitudRepository.save(s));
    }
}