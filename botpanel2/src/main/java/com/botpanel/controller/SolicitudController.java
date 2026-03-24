package com.botpanel.controller;

import com.botpanel.entity.Solicitud;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.EstadoSolicitud;
import com.botpanel.enums.Rol;
import com.botpanel.repository.SolicitudRepository;
import com.botpanel.service.TwilioService;
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
    private final TwilioService twilioService;

    public SolicitudController(SolicitudRepository solicitudRepository, TwilioService twilioService) {
        this.solicitudRepository = solicitudRepository;
        this.twilioService = twilioService;
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

        EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(body.get("estado"));
        s.setEstado(nuevoEstado);
        solicitudRepository.save(s);

        if (s.getTelefono() != null && !s.getTelefono().isBlank()) {
            String mensajePersonalizado = body.get("mensaje");
            String mensaje = null;
            if (mensajePersonalizado != null && !mensajePersonalizado.isBlank()) {
                mensaje = mensajePersonalizado;
            } else if (nuevoEstado == EstadoSolicitud.CONFIRMADA) {
                mensaje = "Hola " + s.getNombre() + ", tu cita ha sido *confirmada*. ¡Te esperamos!";
            } else if (nuevoEstado == EstadoSolicitud.CANCELADA) {
                mensaje = "Hola " + s.getNombre() + ", lamentablemente tu cita ha sido *cancelada*. Contacta con nosotros para más información.";
            }
            if (mensaje != null) {
                twilioService.enviarMensaje(s.getTelefono(), mensaje);
            }
        }

        return ResponseEntity.ok(s);
    }
}