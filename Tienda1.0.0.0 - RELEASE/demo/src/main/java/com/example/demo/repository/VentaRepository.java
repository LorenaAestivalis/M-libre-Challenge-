package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Venta;

public interface VentaRepository {
    Venta saveVenta(Venta venta);
    List<Venta> findAllVentas();
    Optional<Venta> findVentaById(Long id);
}