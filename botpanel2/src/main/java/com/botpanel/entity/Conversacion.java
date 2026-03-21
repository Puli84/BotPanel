package com.botpanel.entity;

import com.botpanel.enums.EstadoConversacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversacion")
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contacto;

    private String nombreContacto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConversacion estado = EstadoConversacion.ACTIVA;

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime cerradoEn;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "agente_id")
    private Usuario agente;

    @JsonIgnore
    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL)
    @OrderBy("enviadoEn ASC")
    private List<Mensaje> mensajes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public EstadoConversacion getEstado() { return estado; }
    public void setEstado(EstadoConversacion estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getCerradoEn() { return cerradoEn; }
    public void setCerradoEn(LocalDateTime cerradoEn) { this.cerradoEn = cerradoEn; }

    public Bot getBot() { return bot; }
    public void setBot(Bot bot) { this.bot = bot; }

    public Usuario getAgente() { return agente; }
    public void setAgente(Usuario agente) { this.agente = agente; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }
}