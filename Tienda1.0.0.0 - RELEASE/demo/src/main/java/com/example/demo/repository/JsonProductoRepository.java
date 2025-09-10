package com.example.demo.repository;

import java.io.IOException;
import java.util.ArrayList; // Use the internal file store
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Producto;
import com.example.demo.store.ProductoFileStore;

// JsonProductoRepository -> su responsabilidad es persistir
@Repository // Mark this as a Spring Repository
public class JsonProductoRepository implements ProductoRepository {

    private final ProductoFileStore productoFileStore;

    public JsonProductoRepository(ProductoFileStore productoFileStore) {
        this.productoFileStore = productoFileStore;
    }

    @Override
    public List<Producto> findAll() {
        return productoFileStore.getProducts(); // Delegates to file store
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoFileStore.getProducts().stream() // Delegates to file store
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public Producto save(Producto producto) throws IOException {
        List<Producto> currentProducts = new ArrayList<>(productoFileStore.getProducts()); // Get mutable copy
        Producto productToSave = null;

        if (producto.getId() == null) {
            // New product
            Long newId = productoFileStore.getNextId();
            producto.setId(newId);
            currentProducts.add(producto);
            productToSave = producto;
        } else {
            // Update existing product
            Optional<Producto> existingProduct = currentProducts.stream()
                    .filter(p -> p.getId().equals(producto.getId()))
                    .findFirst();

            if (existingProduct.isPresent()) {
                // Replace the existing product in the list
                currentProducts = currentProducts.stream()
                        .map(p -> p.getId().equals(producto.getId()) ? producto : p)
                        .collect(Collectors.toCollection(ArrayList::new));
                productToSave = producto;
            } else {
                // ID provided but not found, treat as new (or throw an error if this is not
                // desired)
                Long newId = productoFileStore.getNextId();
                producto.setId(newId);
                currentProducts.add(producto);
                productToSave = producto; // Or throw ResourceNotFoundException("Producto", "id", producto.getId())
            }
        }
        productoFileStore.updateAndSaveProducts(currentProducts); // Update file store and save
        return productToSave;
    }

    @Override
    public void deleteById(Long id) throws IOException {
        List<Producto> currentProducts = new ArrayList<>(productoFileStore.getProducts()); // Get mutable copy
        boolean removed = currentProducts.removeIf(p -> p.getId().equals(id));
        if (removed) {
            productoFileStore.updateAndSaveProducts(currentProducts); // Update file store and save
        } else {
            // Optional: throw ResourceNotFoundException if you want to indicate failure to
            // delete
            // throw new ResourceNotFoundException("Producto", "id", id);
        }
    }

    @Override
    public long count() {
        return productoFileStore.getProducts().size();
    }
}