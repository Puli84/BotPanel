package com.botpanel.entity;

import com.botpanel.enums.OrigenMensaje;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigenMensaje origen;

    @Column(nullable = false)
    private LocalDateTime enviadoEn = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public OrigenMensaje getOrigen() { return origen; }
    public void setOrigen(OrigenMensaje origen) { this.origen = origen; }

    public LocalDateTime getEnviadoEn() { return enviadoEn; }
    public void setEnviadoEn(LocalDateTime enviadoEn) { this.enviadoEn = enviadoEn; }

    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion conversacion) { this.conversacion = conversacion; }
}
