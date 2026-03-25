package com.botpanel.repository;

import com.botpanel.entity.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivoRepository extends JpaRepository<Archivo, String> {
}
