package com.botpanel.controller;

import com.botpanel.entity.Conversacion;
import com.botpanel.entity.Mensaje;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.EstadoConversacion;
import com.botpanel.enums.OrigenMensaje;
import com.botpanel.enums.Rol;
import com.botpanel.repository.ConversacionRepository;
import com.botpanel.repository.MensajeRepository;
import com.botpanel.service.TwilioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversaciones")
@CrossOrigin(origins = "*")
public class ConversacionController {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final TwilioService twilioService;

    public ConversacionController(ConversacionRepository conversacionRepository,
                                   MensajeRepository mensajeRepository,
                                   TwilioService twilioService) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.twilioService = twilioService;
    }

    // Lista conversaciones de la empresa
    @GetMapping
    public ResponseEntity<List<Conversacion>> listar(
            @AuthenticationPrincipal Usuario usuario) {
        if (usuario.getRol() == Rol.SUPER_ADMIN)
            return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(
            conversacionRepository.findByBotEmpresaId(usuario.getEmpresa().getId())
        );
    }

    // Mensajes de una conversación
    @GetMapping("/{id}/mensajes")
    public ResponseEntity<List<Mensaje>> mensajes(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeRepository.findByConversacionId(id));
    }

    // Responder manualmente desde el panel
    @PostMapping("/{id}/responder")
    public ResponseEntity<Mensaje> responder(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Usuario usuario) {

        Conversacion conv = conversacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        String texto = body.get("texto");

        // Envía por WhatsApp
        twilioService.enviarMensaje(conv.getContacto(), texto);

        // Guarda el mensaje en BD
        Mensaje msg = new Mensaje();
        msg.setContenido(texto);
        msg.setOrigen(OrigenMensaje.BOT);
        msg.setConversacion(conv);
        mensajeRepository.save(msg);

        return ResponseEntity.ok(msg);
    }

    // Cerrar conversación
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Conversacion> cerrar(@PathVariable Long id) {
        Conversacion conv = conversacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        conv.setEstado(EstadoConversacion.CERRADA);
        return ResponseEntity.ok(conversacionRepository.save(conv));
    }

    // Reabrir conversación
    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<Conversacion> reabrir(@PathVariable Long id) {
        Conversacion conv = conversacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        conv.setEstado(EstadoConversacion.ACTIVA);
        return ResponseEntity.ok(conversacionRepository.save(conv));
    }
}