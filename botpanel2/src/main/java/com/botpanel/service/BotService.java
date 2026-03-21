package com.botpanel.service;

import com.botpanel.dto.BotRequest;
import com.botpanel.dto.BotResponse;
import com.botpanel.entity.Bot;
import com.botpanel.entity.Usuario;
import com.botpanel.enums.Rol;
import com.botpanel.repository.BotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotService {

    private final BotRepository botRepository;

    public BotService(BotRepository botRepository) {
        this.botRepository = botRepository;
    }

    public List<BotResponse> listar(Usuario usuarioActual) {
        List<Bot> bots;

        if (usuarioActual.getRol() == Rol.SUPER_ADMIN) {
            bots = botRepository.findAll();
        } else {
            bots = botRepository.findByEmpresaId(usuarioActual.getEmpresa().getId());
        }

        return bots.stream().map(this::toResponse).toList();
    }

    public BotResponse crear(BotRequest request, Usuario usuarioActual) {
        if (usuarioActual.getRol() == Rol.SUPER_ADMIN) {
            throw new RuntimeException("El Super Admin debe crear bots desde el endpoint de empresa");
        }

        Bot bot = new Bot();
        bot.setNombre(request.getNombre());
        bot.setNumeroWhatsapp(request.getNumeroWhatsapp());
        bot.setMensajeBienvenida(request.getMensajeBienvenida());
        bot.setEmpresa(usuarioActual.getEmpresa());

        return toResponse(botRepository.save(bot));
    }

    public BotResponse toggleActivo(Long botId, Usuario usuarioActual) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot no encontrado"));

        if (usuarioActual.getRol() != Rol.SUPER_ADMIN &&
            !bot.getEmpresa().getId().equals(usuarioActual.getEmpresa().getId())) {
            throw new RuntimeException("No tienes permiso sobre este bot");
        }

        bot.setActivo(!bot.getActivo());
        return toResponse(botRepository.save(bot));
    }

    public void eliminar(Long botId, Usuario usuarioActual) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot no encontrado"));

        if (usuarioActual.getRol() != Rol.SUPER_ADMIN &&
            !bot.getEmpresa().getId().equals(usuarioActual.getEmpresa().getId())) {
            throw new RuntimeException("No tienes permiso sobre este bot");
        }

        botRepository.delete(bot);
    }

    private BotResponse toResponse(Bot bot) {
        BotResponse r = new BotResponse();
        r.setId(bot.getId());
        r.setNombre(bot.getNombre());
        r.setNumeroWhatsapp(bot.getNumeroWhatsapp());
        r.setActivo(bot.getActivo());
        r.setMensajeBienvenida(bot.getMensajeBienvenida());
        r.setEmpresaId(bot.getEmpresa().getId());
        r.setEmpresaNombre(bot.getEmpresa().getNombre());
        r.setTotalConversaciones(
            bot.getConversaciones() != null ? bot.getConversaciones().size() : 0
        );
        return r;
    }
    public BotResponse actualizar(Long id, BotRequest request, Usuario usuarioActual) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bot no encontrado"));

        if (usuarioActual.getRol() != Rol.SUPER_ADMIN &&
            !bot.getEmpresa().getId().equals(usuarioActual.getEmpresa().getId())) {
            throw new RuntimeException("No tienes permiso sobre este bot");
        }

        bot.setNombre(request.getNombre());
        bot.setNumeroWhatsapp(request.getNumeroWhatsapp());
        bot.setMensajeBienvenida(request.getMensajeBienvenida());
        bot.setContextoIA(request.getContextoIA());

        return toResponse(botRepository.save(bot));
    }
}
