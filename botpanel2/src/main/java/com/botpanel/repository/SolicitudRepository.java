package com.botpanel.repository;

import com.botpanel.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByBotEmpresaIdOrderByCreadoEnDesc(Long empresaId);
    List<Solicitud> findByBotEmpresaIdAndEstado(Long empresaId,
        com.botpanel.enums.EstadoSolicitud estado);
}