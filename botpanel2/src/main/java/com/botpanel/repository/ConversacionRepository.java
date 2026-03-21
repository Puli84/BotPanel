package com.botpanel.repository;

import com.botpanel.entity.Conversacion;
import com.botpanel.enums.EstadoConversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
    List<Conversacion> findByBotEmpresaId(Long empresaId);
    List<Conversacion> findByBotEmpresaIdAndEstado(Long empresaId, EstadoConversacion estado);
    List<Conversacion> findByAgenteId(Long agenteId);
    long countByBotEmpresaIdAndEstado(Long empresaId, EstadoConversacion estado);
}
