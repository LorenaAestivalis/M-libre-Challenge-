package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductoRequestDTO {
   @NotBlank(message = "Error:[nombre] Dato Mandatorio")
   @Size(max = 100, message = "Error:[nombre] Dato Invalido") // <100 caracteres
   @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Error:[Nombre] Dato Invalido")
   private String nombre;

   @NotNull(message = "Error:[precio] Dato Mandotorio")
   @Min(value = 1, message = "Error:[precio] Dato Invalido")
   private Integer precio;

   private String urlImagen;

   @Size(max = 250, message = "Error:[descripción] Dato Invalido")
   private String description;
   @Min(value = 0, message = "Error:[Operacion] No Valida")
   private Integer stock;

   // Getters y Setters
   public String getNombre() {
      return nombre;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public Integer getPrecio() {
      return precio;
   }

   public void setPrecio(Integer precio) {
      this.precio = precio;
   }

   public String getUrlImagen() {
      return urlImagen;
   }

   public void setUrlImagen(String urlImagen) {
      this.urlImagen = urlImagen;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Integer getStock() {
      return stock;
   }

   public void setStock(Integer stock) {
      this.stock = stock;
   }
}