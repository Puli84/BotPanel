package com.botpanel.entity;

import com.botpanel.enums.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // JSON flexible con datos extra según el negocio
    @Column(columnDefinition = "TEXT")
    private String datos;

    // Tipo libre: "reserva", "cita", "pedido", etc.
    private String tipo;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    private LocalDateTime creadoEn = LocalDateTime.now();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getDatos() { return datos; }
    public void setDatos(String datos) { this.datos = datos; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public Bot getBot() { return bot; }
    public void setBot(Bot bot) { this.bot = bot; }
}