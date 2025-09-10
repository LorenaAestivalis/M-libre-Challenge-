package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List; // Usamos el modelo aquí para simplificar

import com.example.demo.model.ProductoVendido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private Long id;
    private LocalDateTime fechaVenta;
    private List<ProductoVendido> productosVendidos;
    private Integer total;
}
