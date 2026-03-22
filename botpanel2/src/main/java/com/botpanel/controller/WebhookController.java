package com.botpanel.controller;

import com.botpanel.entity.Bot;
import com.botpanel.entity.Conversacion;
import com.botpanel.entity.Mensaje;
import com.botpanel.entity.Solicitud;
import com.botpanel.enums.EstadoConversacion;
import com.botpanel.enums.OrigenMensaje;
import com.botpanel.repository.BotRepository;
import com.botpanel.repository.ConversacionRepository;
import com.botpanel.repository.MensajeRepository;
import com.botpanel.repository.SolicitudRepository;
import com.botpanel.service.OpenAIService;
import com.botpanel.service.TwilioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.twiml.MessagingResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final OpenAIService openAIService;
    private final BotRepository botRepository;
    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final SolicitudRepository solicitudRepository;
    private final TwilioService twilioService;

    public WebhookController(OpenAIService openAIService,
                             BotRepository botRepository,
                             ConversacionRepository conversacionRepository,
                             MensajeRepository mensajeRepository,
                             SolicitudRepository solicitudRepository,
                             TwilioService twilioService) {
        this.openAIService = openAIService;
        this.botRepository = botRepository;
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.solicitudRepository = solicitudRepository;
        this.twilioService = twilioService;
    }

    // ── Webhook Twilio ─────────────────────────────────────────────
    @PostMapping(
        value = "/whatsapp",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_XML_VALUE
    )
    public String recibirMensajeTwilio(
            @RequestParam("From") String from,
            @RequestParam("Body") String body,
            @RequestParam(value = "To", required = false) String to) {

        System.out.println("Twilio — de: " + from + " → " + body);

        Bot bot = encontrarBot(to);
        String contacto = from.replace("whatsapp:", "");

        // Si el bot está inactivo, guardamos el mensaje pero no respondemos
        if (bot == null) {
            System.out.println("Bot inactivo o no encontrado — mensaje recibido pero sin respuesta");
            return new MessagingResponse.Builder().build().toXml();
        }

        List<Map<String, String>> historial = obtenerHistorial(
            contacto, bot != null ? bot.getEmpresa().getId() : null);

        Map<String, Object> resultado = openAIService.responderYExtraerSolicitud(
            bot != null ? bot.getContextoIA() : null, body, historial);

        String respuestaCompleta = (String) resultado.get("mensaje");
        System.out.println("Respuesta OpenAI: " + respuestaCompleta);

        // ── Detecta ##IMAGEN## ──────────────────────────────────────
        String imagenUrl = null;
        String respuestaLimpia = respuestaCompleta;

        if (respuestaCompleta != null && respuestaCompleta.contains("##IMAGEN##")) {
            String[] partes = respuestaCompleta.split("##IMAGEN##");
            respuestaLimpia = partes[0].trim();
            if (partes.length > 1)
                imagenUrl = partes[1].replace("##", "").trim();
        }

        // ── Detecta ##ARCHIVO## ─────────────────────────────────────
        String archivoUrl = null;
        if (respuestaLimpia != null && respuestaLimpia.contains("##ARCHIVO##")) {
            String[] partes = respuestaLimpia.split("##ARCHIVO##");
            respuestaLimpia = partes[0].trim();
            if (partes.length > 1)
                archivoUrl = partes[1].replace("##", "").trim();
        }

        // Guarda solicitud si la hay
        if (Boolean.TRUE.equals(resultado.get("tieneSolicitud")) && bot != null) {
            guardarSolicitud((String) resultado.get("solicitudJson"), contacto, bot);
        }

        // Guarda mensaje en BD
        if (bot != null) guardarMensaje(contacto, body, respuestaLimpia, bot);

        // Envía con archivo, imagen o solo texto
        if (archivoUrl != null) {
            System.out.println("Enviando archivo: " + archivoUrl);
            twilioService.enviarConImagen(contacto, respuestaLimpia, archivoUrl);
        } else if (imagenUrl != null) {
            System.out.println("Enviando imagen: " + imagenUrl);
            twilioService.enviarConImagen(contacto, respuestaLimpia, imagenUrl);
        } else {
            twilioService.enviarConImagen(contacto, respuestaLimpia, null);
        }

        return new MessagingResponse.Builder().build().toXml();
    }

    // ── Webhook Baileys ────────────────────────────────────────────
    @PostMapping("/whatsapp-baileys")
    public Map<String, String> recibirMensajeBaileys(@RequestBody Map<String, String> payload) {
        String from = payload.get("from");
        String body = payload.get("body");

        Bot bot = botRepository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getActivo()))
                .findFirst().orElse(null);

        List<Map<String, String>> historial = obtenerHistorial(
            from, bot != null ? bot.getEmpresa().getId() : null);

        Map<String, Object> resultado = openAIService.responderYExtraerSolicitud(
            bot != null ? bot.getContextoIA() : null, body, historial);

        String respuestaCompleta = (String) resultado.get("mensaje");

        // Limpia tags de imagen y archivo
        String respuestaLimpia = respuestaCompleta;
        if (respuestaCompleta != null && respuestaCompleta.contains("##IMAGEN##")) {
            respuestaLimpia = respuestaCompleta.split("##IMAGEN##")[0].trim();
        }
        if (respuestaLimpia != null && respuestaLimpia.contains("##ARCHIVO##")) {
            respuestaLimpia = respuestaLimpia.split("##ARCHIVO##")[0].trim();
        }

        if (Boolean.TRUE.equals(resultado.get("tieneSolicitud")) && bot != null) {
            guardarSolicitud((String) resultado.get("solicitudJson"), from, bot);
        }

        if (bot != null) guardarMensaje(from, body, respuestaLimpia, bot);

        return Map.of("respuesta", respuestaLimpia != null ? respuestaLimpia : "");
    }

    // ── Busca el bot por número ────────────────────────────────────
    private Bot encontrarBot(String numeroDestino) {
        if (numeroDestino != null) {
            String numero = numeroDestino.replace("whatsapp:", "");
            return botRepository.findAll().stream()
                    .filter(b -> numero.equals(b.getNumeroWhatsapp())
                            && Boolean.TRUE.equals(b.getActivo()))
                    .findFirst().orElse(null);
        }
        return botRepository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getActivo()))
                .findFirst().orElse(null);
    }

    // ── Historial de conversación ──────────────────────────────────
    private List<Map<String, String>> obtenerHistorial(String contacto, Long empresaId) {
        if (empresaId == null) return new ArrayList<>();
        return conversacionRepository.findByBotEmpresaId(empresaId)
                .stream()
                .filter(c -> contacto.equals(c.getContacto())
                        && c.getEstado() == EstadoConversacion.ACTIVA)
                .findFirst()
                .map(conv -> mensajeRepository.findByConversacionId(conv.getId())
                        .stream()
                        .map(m -> Map.of(
                            "role", m.getOrigen() == OrigenMensaje.CLIENTE
                                ? "user" : "assistant",
                            "content", m.getContenido()
                        ))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    // ── Guarda solicitud ───────────────────────────────────────────
    private void guardarSolicitud(String jsonStr, String telefono, Bot bot) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> datos = mapper.readValue(jsonStr, Map.class);

            Solicitud s = new Solicitud();
            s.setNombre((String) datos.getOrDefault("nombre", ""));
            s.setTelefono(telefono);
            s.setNotas((String) datos.getOrDefault("notas", ""));
            s.setTipo((String) datos.getOrDefault("tipo", "solicitud"));
            s.setBot(bot);

            Object datosExtra = datos.get("datos");
            if (datosExtra != null)
                s.setDatos(mapper.writeValueAsString(datosExtra));

            solicitudRepository.save(s);
            System.out.println("✅ Solicitud guardada: " + s.getNombre() + " — " + s.getTipo());

        } catch (Exception e) {
            System.err.println("❌ Error guardando solicitud: " + e.getMessage());
        }
    }

    // ── Guarda mensajes en BD ──────────────────────────────────────
    private void guardarMensaje(String contacto, String textoCliente,
                                 String textoBot, Bot bot) {
        Optional<Conversacion> convExistente = conversacionRepository
                .findByBotEmpresaId(bot.getEmpresa().getId())
                .stream()
                .filter(c -> contacto.equals(c.getContacto())
                        && c.getEstado() == EstadoConversacion.ACTIVA)
                .findFirst();

        Conversacion conv = convExistente.orElseGet(() -> {
            Conversacion nueva = new Conversacion();
            nueva.setContacto(contacto);
            nueva.setBot(bot);
            nueva.setEstado(EstadoConversacion.ACTIVA);
            return conversacionRepository.save(nueva);
        });

        Mensaje msgCliente = new Mensaje();
        msgCliente.setContenido(textoCliente);
        msgCliente.setOrigen(OrigenMensaje.CLIENTE);
        msgCliente.setConversacion(conv);
        mensajeRepository.save(msgCliente);

        Mensaje msgBot = new Mensaje();
        msgBot.setContenido(textoBot != null ? textoBot : "");
        msgBot.setOrigen(OrigenMensaje.BOT);
        msgBot.setConversacion(conv);
        mensajeRepository.save(msgBot);
    }
}
