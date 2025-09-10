package com.example.demo.dto;

public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private Integer precio;
    private String urlImagen;
    private String description;
    private Integer stock;
    private String mensaje; // Nuevo campo para el mensaje de respuesta

    public ProductoResponseDTO(Long id, String nombre, Integer precio, String urlImagen, String description,
            Integer stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.description = description;
        this.stock = stock;
    }

    public ProductoResponseDTO(Long id, String nombre, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.mensaje = mensaje; // Asigna el mensaje
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getPrecio() {
        return precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStock() {
        return stock;
    }

    public String getMensaje() {
        return mensaje;
    } // Getter para el nuevo campo
}