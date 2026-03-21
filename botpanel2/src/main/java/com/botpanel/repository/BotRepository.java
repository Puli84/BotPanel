package com.botpanel.repository;

import com.botpanel.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BotRepository extends JpaRepository<Bot, Long> {
    List<Bot> findByEmpresaId(Long empresaId);
    List<Bot> findByEmpresaIdAndActivoTrue(Long empresaId);
}
