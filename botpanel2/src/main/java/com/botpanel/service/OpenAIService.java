package com.botpanel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    // Método básico sin historial
    public String responder(String contexto, String mensajeUsuario) {
        return responderConHistorial(contexto, mensajeUsuario, new ArrayList<>());
    }

    // Método con historial — para conversaciones con memoria
    public String responderConHistorial(String contexto, String mensajeUsuario,
                                         List<Map<String, String>> historial) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<Map<String, String>> mensajes = new ArrayList<>();

        mensajes.add(Map.of(
            "role", "system",
            "content", contexto != null && !contexto.isEmpty()
                ? contexto
                : "Eres un asistente virtual útil y amable."
        ));

        int inicio = Math.max(0, historial.size() - 10);
        mensajes.addAll(historial.subList(inicio, historial.size()));
        mensajes.add(Map.of("role", "user", "content", mensajeUsuario));

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", mensajes);
        body.put("max_tokens", 300);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            System.err.println("Error OpenAI: " + e.getMessage());
            return "Lo siento, en este momento no puedo responder. Inténtalo más tarde.";
        }
    }

    // Método que detecta solicitudes (reservas, citas, pedidos...)
    public Map<String, Object> responderYExtraerSolicitud(String contexto,
            String mensajeUsuario, List<Map<String, String>> historial) {

        String instruccionExtra = """

        IMPORTANTE: Cuando el cliente haya proporcionado toda la información necesaria
        para completar su solicitud (cita, reserva, pedido, etc.), incluye al FINAL
        de tu respuesta, en una línea separada, este JSON exacto:
        ##SOLICITUD##{"tipo":"...","nombre":"...","telefono":"...","notas":"...","datos":{...}}##

        Donde:
        - tipo: el tipo de solicitud ("reserva", "cita", "pedido", etc.)
        - nombre: nombre del cliente
        - telefono: si lo ha dado, sino de donde venga el mensaje
        - notas: observaciones importantes
        - datos: objeto JSON con cualquier dato extra relevante

        Solo incluye el JSON cuando tengas nombre y los datos clave del negocio.
        Si falta información importante, sigue preguntando.
        """;

        String contextoCompleto = (contexto != null ? contexto : "") + instruccionExtra;
        String respuestaCompleta = responderConHistorial(
            contextoCompleto, mensajeUsuario, historial);

        Map<String, Object> resultado = new HashMap<>();

        if (respuestaCompleta.contains("##SOLICITUD##")) {
            String[] partes = respuestaCompleta.split("##SOLICITUD##");
            resultado.put("mensaje", partes[0].trim());
            try {
                String jsonStr = partes[1].replace("##", "").trim();
                resultado.put("solicitudJson", jsonStr);
                resultado.put("tieneSolicitud", true);
            } catch (Exception e) {
                resultado.put("tieneSolicitud", false);
                resultado.put("mensaje", respuestaCompleta);
            }
        } else {
            resultado.put("mensaje", respuestaCompleta);
            resultado.put("tieneSolicitud", false);
        }

        return resultado;
    }
}