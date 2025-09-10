package com.example.demo.repository;

import com.example.demo.model.Producto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// This is a custom repository interface, not extending JpaRepository
public interface ProductoRepository {
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto) throws IOException; // Can create or update
    void deleteById(Long id) throws IOException;
    long count(); // Added for completeness, if needed
    // You could add more specific query methods here if needed, e.g.,
    // Optional<Producto> findByNombre(String nombre);
}