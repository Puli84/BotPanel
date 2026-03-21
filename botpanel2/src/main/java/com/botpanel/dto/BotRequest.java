package com.botpanel.dto;

public class BotRequest {
    private String nombre;
    private String numeroWhatsapp;
    private String mensajeBienvenida;
    private String contextoIA;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNumeroWhatsapp() { return numeroWhatsapp; }
    public void setNumeroWhatsapp(String numeroWhatsapp) { this.numeroWhatsapp = numeroWhatsapp; }

    public String getMensajeBienvenida() { return mensajeBienvenida; }
    public void setMensajeBienvenida(String mensajeBienvenida) { this.mensajeBienvenida = mensajeBienvenida; }
    public String getContextoIA() { return contextoIA; }
    public void setContextoIA(String contextoIA) { this.contextoIA = contextoIA; }
}
