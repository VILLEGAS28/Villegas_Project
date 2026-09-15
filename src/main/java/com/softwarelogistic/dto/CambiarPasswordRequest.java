package com.softwarelogistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ====================================================================
 * DTO: SOLICITUD DE CAMBIO DE CONTRASEÑA
 * ====================================================================
 * Objeto de transferencia que transporta los datos necesarios
 * para actualizar la clave de un usuario de forma segura.
 */
public class CambiarPasswordRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer idUsuario;

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaPassword;

    // Constructores
    public CambiarPasswordRequest() {
    }

    public CambiarPasswordRequest(Integer idUsuario, String passwordActual, String nuevaPassword) {
        this.idUsuario = idUsuario;
        this.passwordActual = passwordActual;
        this.nuevaPassword = nuevaPassword;
    }

    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}