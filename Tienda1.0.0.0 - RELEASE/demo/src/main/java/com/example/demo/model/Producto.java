package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
  private Long id;
  private String nombre;
  private Integer precio;
  private String urlImagen;
  private String description;
  private int stock;

  public Producto(Long id, Integer precio) {
    this.id = id;
    this.precio = precio;

  }
}
