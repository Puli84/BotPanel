package com.botpanel.repository;

import com.botpanel.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByConversacionId(Long conversacionId);

    @Query("SELECT DATE(m.enviadoEn), COUNT(m) FROM Mensaje m WHERE m.conversacion.bot.empresa.id = :empresaId AND m.enviadoEn >= :desde GROUP BY DATE(m.enviadoEn) ORDER BY DATE(m.enviadoEn)")
    List<Object[]> countByDia(@Param("empresaId") Long empresaId, @Param("desde") LocalDateTime desde);

    @Query("SELECT HOUR(m.enviadoEn), COUNT(m) FROM Mensaje m WHERE m.conversacion.bot.empresa.id = :empresaId GROUP BY HOUR(m.enviadoEn) ORDER BY HOUR(m.enviadoEn)")
    List<Object[]> countByHora(@Param("empresaId") Long empresaId);
}