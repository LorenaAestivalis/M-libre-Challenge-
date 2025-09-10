package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductoCambioPrecioRequestDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Producto;
import com.example.demo.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    public Producto guardarProducto(Producto producto) {
        try {
            return productoRepository.save(producto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save product: " + e.getMessage(), e);
        }
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public void eliminarProducto(Long id) {
        try {
            productoRepository.deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar le producto: " + e.getMessage(), e);
        }
    }

    public Producto cambiarPrecioProducto(ProductoCambioPrecioRequestDTO request) {
        Integer nuevoPrecio = request.getNuevoPrecio();

        if (nuevoPrecio <= 0) { // Asumo que el precio es un Integer, no un Double
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }

        try {
            Producto producto = productoRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getId()));

            producto.setPrecio(nuevoPrecio);
            return productoRepository.save(producto);
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar el precio: " + e.getMessage(), e);
        }
    }
}