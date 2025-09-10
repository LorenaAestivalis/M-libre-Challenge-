package com.example.demo.mapper;


import com.example.demo.dto.ProductoCambioPrecioRequestDTO;
import com.example.demo.dto.ProductoRequestDTO;
import com.example.demo.dto.ProductoResponseDTO;
import com.example.demo.model.Producto;
public class ProductoMapper {
   public static Producto toEntity(ProductoRequestDTO dto) {
       return new Producto(
               null, // id se genera en otro lado
               dto.getNombre(),
               dto.getPrecio(),
               dto.getUrlImagen(),
               dto.getDescription(),
               dto.getStock()
       );
   }
   public static ProductoResponseDTO toResponseDTO(Producto producto) {
       return new ProductoResponseDTO(
               producto.getId(),
               producto.getNombre(),
               producto.getPrecio(),
               producto.getUrlImagen(),
               producto.getDescription(),
               producto.getStock()
       );
   }


    public static Producto toEntity2(ProductoCambioPrecioRequestDTO dto) {
       return new Producto(
               dto.getId(),
               dto.getNuevoPrecio()
       );
   }
}