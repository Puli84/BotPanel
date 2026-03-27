package com.botpanel.controller;

import com.botpanel.entity.Archivo;
import com.botpanel.entity.Conversacion;
import com.botpanel.entity.Mensaje;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.EstadoConversacion;
import com.botpanel.enums.OrigenMensaje;
import com.botpanel.enums.Rol;
import com.botpanel.entity.Bot;
import com.botpanel.repository.ArchivoRepository;
import com.botpanel.repository.BotRepository;
import com.botpanel.repository.ConversacionRepository;
import com.botpanel.repository.MensajeRepository;
import com.botpanel.service.TwilioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversaciones")
@CrossOrigin(origins = "*")
public class ConversacionController {

    @Value("${app.base-url:https://botpanel-production.up.railway.app}")
    private String baseUrl;

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final TwilioService twilioService;
    private final ArchivoRepository archivoRepository;
    private final BotRepository botRepository;

    public ConversacionController(ConversacionRepository conversacionRepository,
                                   MensajeRepository mensajeRepository,
                                   TwilioService twilioService,
                                   ArchivoRepository archivoRepository,
                                   BotRepository botRepository) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.twilioService = twilioService;
        this.archivoRepository = archivoRepository;
        this.botRepository = botRepository;
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
        msg.setOrigen(OrigenMensaje.AGENTE);
        msg.setConversacion(conv);
        mensajeRepository.save(msg);

        return ResponseEntity.ok(msg);
    }

    // Crear conversación nueva con un número cualquiera
    @PostMapping("/nueva")
    public ResponseEntity<?> nuevaConversacion(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Usuario usuario) {

        String telefono = body.get("telefono");
        String mensaje  = body.get("mensaje");
        String botIdStr = body.get("botId");

        if (telefono == null || telefono.isBlank() || mensaje == null || mensaje.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Teléfono y mensaje son obligatorios"));

        // Normaliza el número (quita espacios y asegura formato +)
        telefono = telefono.trim().replaceAll("\\s+", "");
        if (!telefono.startsWith("+")) telefono = "+" + telefono;

        Bot bot;
        if (botIdStr != null && !botIdStr.isBlank()) {
            bot = botRepository.findById(Long.parseLong(botIdStr))
                    .orElseThrow(() -> new RuntimeException("Bot no encontrado"));
        } else {
            List<Bot> bots = botRepository.findByEmpresaIdAndActivoTrue(usuario.getEmpresa().getId());
            if (bots.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("error", "No hay bots activos en tu empresa"));
            bot = bots.get(0);
        }

        final String telefonoFinal = telefono;
        final Bot botFinal = bot;

        // Reutiliza conversación activa existente o crea una nueva
        Conversacion conv = conversacionRepository
                .findByBotEmpresaId(usuario.getEmpresa().getId())
                .stream()
                .filter(c -> c.getContacto().equals(telefonoFinal)
                          && c.getBot().getId().equals(botFinal.getId())
                          && c.getEstado() == com.botpanel.enums.EstadoConversacion.ACTIVA)
                .findFirst()
                .orElseGet(() -> {
                    Conversacion nueva = new Conversacion();
                    nueva.setContacto(telefonoFinal);
                    nueva.setBot(botFinal);
                    return conversacionRepository.save(nueva);
                });

        // Envía por WhatsApp
        twilioService.enviarMensaje(conv.getContacto(), mensaje);

        // Guarda el mensaje en BD
        Mensaje msg = new Mensaje();
        msg.setContenido(mensaje);
        msg.setOrigen(OrigenMensaje.AGENTE);
        msg.setConversacion(conv);
        mensajeRepository.save(msg);

        return ResponseEntity.ok(conv);
    }

    // Enviar archivo (foto/PDF) desde el panel del agente
    @PostMapping("/{id}/responder-archivo")
    public ResponseEntity<?> responderArchivo(
            @PathVariable Long id,
            @RequestParam(value = "texto", required = false, defaultValue = "") String texto,
            @RequestParam("archivo") MultipartFile archivo) {

        try {
            Conversacion conv = conversacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

            // Guarda el archivo en BD
            String original = archivo.getOriginalFilename();
            String extension = original != null && original.contains(".")
                    ? original.substring(original.lastIndexOf(".")) : "";
            String archivoId = UUID.randomUUID().toString() + extension;

            Archivo entity = new Archivo();
            entity.setId(archivoId);
            entity.setNombre(original);
            entity.setContentType(archivo.getContentType());
            entity.setDatos(archivo.getBytes());
            archivoRepository.save(entity);

            String archivoUrl = baseUrl + "/api/archivos/" + archivoId;

            // Envía por WhatsApp con el archivo
            twilioService.enviarConImagen(conv.getContacto(), texto, archivoUrl);

            // Guarda el mensaje en BD
            String contenidoMensaje = texto.isBlank()
                    ? "[Archivo: " + original + "]"
                    : texto + " [Archivo: " + original + "]";

            Mensaje msg = new Mensaje();
            msg.setContenido(contenidoMensaje);
            msg.setOrigen(OrigenMensaje.AGENTE);
            msg.setConversacion(conv);
            mensajeRepository.save(msg);

            return ResponseEntity.ok(msg);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al enviar archivo: " + e.getMessage()));
        }
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