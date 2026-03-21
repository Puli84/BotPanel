package com.botpanel.controller;

import com.botpanel.dto.BotRequest;
import com.botpanel.dto.BotResponse;
import com.botpanel.entity.Usuario;
import com.botpanel.service.BotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
@CrossOrigin(origins = "*")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @GetMapping
    public ResponseEntity<List<BotResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(botService.listar(usuario));
    }

    @PostMapping
    public ResponseEntity<BotResponse> crear(@RequestBody BotRequest request,
                                              @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(botService.crear(request, usuario));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<BotResponse> toggle(@PathVariable Long id,
                                               @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(botService.toggleActivo(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @AuthenticationPrincipal Usuario usuario) {
        botService.eliminar(id, usuario);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<BotResponse> actualizar(
            @PathVariable Long id,
            @RequestBody BotRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(botService.actualizar(id, request, usuario));
    }
}
