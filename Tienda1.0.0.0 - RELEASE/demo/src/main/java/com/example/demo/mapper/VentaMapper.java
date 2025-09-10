package com.example.demo.mapper;

import com.example.demo.dto.VentaResponseDTO;
import com.example.demo.model.Venta;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {

    public VentaResponseDTO toDto(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setProductosVendidos(venta.getProductosVendidos());
        dto.setTotal(venta.getTotal());
        return dto;
    }
}