package com.softwarelogistic.modelo;

/**
 * Estados disponibles para un pedido.
 */
public enum EstadoPedido {

    // Pedido creado pero todavía no procesado
    PENDIENTE,

    // Pedido en proceso de preparación
    PROCESANDO,

    // Pedido preparado para despacho
    PREPARADO,

    // Pedido enviado al cliente
    ENVIADO,

    // Pedido recibido por el cliente
    ENTREGADO,

    // Pedido cancelado
    CANCELADO
}