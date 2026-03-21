package com.botpanel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "bot")
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String contextoIA;

    @Column(nullable = false)
    private String nombre;

    private String numeroWhatsapp;
    private Boolean activo = false;

    @Column(columnDefinition = "TEXT")
    private String mensajeBienvenida;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @JsonIgnore
    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL)
    private List<Conversacion> conversaciones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNumeroWhatsapp() { return numeroWhatsapp; }
    public void setNumeroWhatsapp(String numeroWhatsapp) { this.numeroWhatsapp = numeroWhatsapp; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public String getContextoIA() { return contextoIA; }
  

    public String getMensajeBienvenida() { return mensajeBienvenida; }
    public void setMensajeBienvenida(String mensajeBienvenida) { this.mensajeBienvenida = mensajeBienvenida; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public List<Conversacion> getConversaciones() { return conversaciones; }
    public void setConversaciones(List<Conversacion> conversaciones) { this.conversaciones = conversaciones; }
    public void setContextoIA(String contextoIA) { this.contextoIA = contextoIA; }
}