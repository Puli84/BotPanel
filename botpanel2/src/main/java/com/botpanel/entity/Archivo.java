package com.botpanel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "archivo")
public class Archivo {

    @Id
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String contentType;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] datos;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getDatos() { return datos; }
    public void setDatos(byte[] datos) { this.datos = datos; }
}
