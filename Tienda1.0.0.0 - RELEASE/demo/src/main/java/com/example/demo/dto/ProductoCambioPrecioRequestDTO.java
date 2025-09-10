package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductoCambioPrecioRequestDTO {

    @NotNull(message = "Error:[Id] Dato Mandatorio")
    private Long id;

    @NotNull(message = "Error:[precio] Dato Mandatorio")
    @Positive(message = "Error:[nuevoPrecio] Dato invalido")
    private Integer nuevoPrecio;

    // obligatorio para Jackson
    public ProductoCambioPrecioRequestDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter y Setter para el String que recibimos de la solicitud

    // Getter para el Integer real que usarás en tu lógica de negocio
    public Integer getNuevoPrecio() {
        return this.nuevoPrecio;
    }

    public void setNuevoPrecio(Integer nuevoPrecio) {
        this.nuevoPrecio = nuevoPrecio;

    }
}