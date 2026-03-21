package com.botpanel.dto;

import com.botpanel.enums.Rol;

public class LoginResponse {
    private String token;
    private String nombre;
    private String email;
    private Rol rol;
    private Long empresaId;

    public LoginResponse(String token, String nombre, String email, Rol rol, Long empresaId) {
        this.token = token;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.empresaId = empresaId;
    }

    public String getToken() { return token; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
    public Long getEmpresaId() { return empresaId; }
}
