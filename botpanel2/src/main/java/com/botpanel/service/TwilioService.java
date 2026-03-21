package com.botpanel.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.List;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String from;

    // Se ejecuta una sola vez al arrancar Spring
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        System.out.println("✅ Twilio inicializado");
    }

    public void enviarMensaje(String telefono, String texto) {
        enviarConImagen(telefono, texto, null);
    }

    public void enviarConImagen(String telefono, String texto, String imagenUrl) {
        try {
            String to = telefono.startsWith("whatsapp:")
                ? telefono : "whatsapp:" + telefono;

            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                // Limpia caracteres problemáticos de la URL
                String urlLimpia = imagenUrl.trim()
                    .replace(" ", "%20")
                    .replace("[", "%5B")
                    .replace("]", "%5D");

                try {
                    Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber("whatsapp:" + from),
                        texto
                    ).setMediaUrl(List.of(new URI(urlLimpia))).create();
                } catch (Exception imgError) {
                    System.err.println("❌ Error con imagen, enviando solo texto: " + imgError.getMessage());
                    // Si falla la imagen envía solo el texto
                    Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber("whatsapp:" + from),
                        texto
                    ).create();
                }
            } else {
                Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber("whatsapp:" + from),
                    texto
                ).create();
            }

            System.out.println("✅ Mensaje enviado a: " + to);

        } catch (Exception e) {
            System.err.println("❌ Error Twilio: " + e.getMessage());
        }
    }
}