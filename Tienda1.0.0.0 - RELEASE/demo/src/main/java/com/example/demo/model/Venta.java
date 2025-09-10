package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    private Long id;
    private LocalDateTime fechaVenta;
    private List<ProductoVendido> productosVendidos; // Una lista de productos y sus cantidades en esta venta
    private Integer total;

}
