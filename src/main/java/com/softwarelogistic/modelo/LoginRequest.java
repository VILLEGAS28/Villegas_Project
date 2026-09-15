package com.softwarelogistic.dto;

/**
 * DTO utilizado para recibir las credenciales
 * del usuario durante el inicio de sesión.
 */
public class LoginRequest {

    // ---------------------------------------------------------
    // CORREO
    // ---------------------------------------------------------

    private String correo;

    // ---------------------------------------------------------
    // CONTRASEÑA
    // ---------------------------------------------------------

    private String password;

    // ---------------------------------------------------------
    // GETTER CORREO
    // ---------------------------------------------------------

    public String getCorreo() {
        return correo;
    }

    // ---------------------------------------------------------
    // SETTER CORREO
    // ---------------------------------------------------------

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // ---------------------------------------------------------
    // GETTER CONTRASEÑA
    // ---------------------------------------------------------

    public String getPassword() {
        return password;
    }

    // ---------------------------------------------------------
    // SETTER CONTRASEÑA
    // ---------------------------------------------------------

    public void setPassword(String password) {
        this.password = password;
    }
}