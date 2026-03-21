package com.botpanel.dto;

public class BotResponse {
    private Long id;
    private String nombre;
    private String numeroWhatsapp;
    private Boolean activo;
    private String mensajeBienvenida;
    private Long empresaId;
    private String empresaNombre;
    private long totalConversaciones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNumeroWhatsapp() { return numeroWhatsapp; }
    public void setNumeroWhatsapp(String numeroWhatsapp) { this.numeroWhatsapp = numeroWhatsapp; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getMensajeBienvenida() { return mensajeBienvenida; }
    public void setMensajeBienvenida(String mensajeBienvenida) { this.mensajeBienvenida = mensajeBienvenida; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }

    public long getTotalConversaciones() { return totalConversaciones; }
    public void setTotalConversaciones(long totalConversaciones) { this.totalConversaciones = totalConversaciones; }
}
