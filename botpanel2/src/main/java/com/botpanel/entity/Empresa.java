package com.botpanel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    private String tokenWhatsapp;
    private String numeroWhatsapp;

    @Column(nullable = false)
    private LocalDate creadoEn = LocalDate.now();

    private Boolean activa = true;

    @JsonIgnore
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Bot> bots;

    @JsonIgnore
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Usuario> usuarios;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTokenWhatsapp() { return tokenWhatsapp; }
    public void setTokenWhatsapp(String tokenWhatsapp) { this.tokenWhatsapp = tokenWhatsapp; }

    public String getNumeroWhatsapp() { return numeroWhatsapp; }
    public void setNumeroWhatsapp(String numeroWhatsapp) { this.numeroWhatsapp = numeroWhatsapp; }

    public LocalDate getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDate creadoEn) { this.creadoEn = creadoEn; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public List<Bot> getBots() { return bots; }
    public void setBots(List<Bot> bots) { this.bots = bots; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }
}