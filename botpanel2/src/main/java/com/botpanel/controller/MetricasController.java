package com.botpanel.controller;

import com.botpanel.entity.Usuario;
import com.botpanel.enums.Rol;
import com.botpanel.repository.ConversacionRepository;
import com.botpanel.repository.MensajeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/metricas")
@CrossOrigin(origins = "*")
public class MetricasController {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;

    public MetricasController(ConversacionRepository conversacionRepository,
                               MensajeRepository mensajeRepository) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetricas(
            @AuthenticationPrincipal Usuario usuario) {

        Long empresaId = usuario.getRol() == Rol.SUPER_ADMIN
                ? null
                : usuario.getEmpresa().getId();

        Map<String, Object> metricas = new HashMap<>();

        if (empresaId == null) {
            metricas.put("totalContactos", 0);
            metricas.put("totalConversaciones", 0);
            metricas.put("contactos", List.of());
            return ResponseEntity.ok(metricas);
        }

      
        var conversaciones = conversacionRepository.findByBotEmpresaId(empresaId);

        
        List<String> contactos = conversaciones.stream()
                .map(c -> c.getContacto())
                .distinct()
                .toList();

        metricas.put("totalContactos", contactos.size());
        metricas.put("totalConversaciones", conversaciones.size());
        metricas.put("contactos", contactos);

        
        List<Object[]> porDia = mensajeRepository.countByDia(
                empresaId, LocalDateTime.now().minusDays(7));
        metricas.put("mensajesPorDia", porDia);

      
        List<Object[]> porHora = mensajeRepository.countByHora(empresaId);
        metricas.put("mensajesPorHora", porHora);

        return ResponseEntity.ok(metricas);
    }
}