// src/main/java/com/example/demo/store/ProductoFileStore.java
package com.example.demo.store; // Changed package to 'store' to differentiate

import com.example.demo.model.Producto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Internal component responsible for low-level JSON file loading, saving,
 * and managing the in-memory list of Producto objects, ensuring thread-safety.
 * This class is intended to be used by the JsonProductoRepository.
 */
@Component
public class ProductoFileStore {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Value("${app.data.productos-json-path:classpath:productos.json}")
    private String productosJsonPath; // Path for initial loading

    @Value("${app.data.productos-save-path:./productos_data.json}")
    private String productosSavePath; // Path for saving persistent changes

    private List<Producto> currentProductos = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(0L);

    public ProductoFileStore(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        loadData();
        idCounter.set(currentProductos.stream()
                .mapToLong(Producto::getId)
                .max()
                .orElse(0L) + 1);
        System.out.println("ProductoFileStore initialized. Next available ID: " + idCounter.get());
    }

    private void loadData() throws IOException {
        readWriteLock.writeLock().lock();
        try {
            File saveFile = new File(productosSavePath);
            List<Producto> loadedData;

            if (saveFile.exists() && saveFile.canRead()) {
                System.out.println("Loading products from persistence file: " + productosSavePath);
                try (InputStream is = Files.newInputStream(saveFile.toPath())) {
                    loadedData = objectMapper.readValue(is, new TypeReference<List<Producto>>() {
                    });
                }
            } else {
                System.out.println("Persistence file not found. Loading from classpath: " + productosJsonPath);
                Resource resource = resourceLoader.getResource(productosJsonPath);
                if (!resource.exists()) {
                    System.err.println("Initial classpath JSON resource not found. Starting with an empty list.");
                    loadedData = new ArrayList<>();
                } else {
                    try (InputStream is = resource.getInputStream()) {
                        loadedData = objectMapper.readValue(is, new TypeReference<List<Producto>>() {
                        });
                    }
                    // Immediately save the initial load to the writable path
                    saveData(loadedData);
                }
            }
            this.currentProductos = loadedData; // Update the in-memory list
            System.out.println("Loaded " + this.currentProductos.size() + " products into memory.");
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void saveData(List<Producto> dataToSave) throws IOException {
        // This is called from loadData or from JsonProductoRepository with a locked
        // list
        File tempFile = File.createTempFile("productos", ".json");
        objectMapper.writeValue(tempFile, dataToSave);
        Files.move(tempFile.toPath(), new File(productosSavePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Products successfully saved to: " + productosSavePath);
    }

    // --- Methods for JsonProductoRepository to use ---

    public List<Producto> getProducts() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(currentProductos); // Return a defensive copy
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public synchronized void updateAndSaveProducts(List<Producto> updatedProducts) throws IOException {
        readWriteLock.writeLock().lock(); // Acquire write lock for modifications
        try {
            this.currentProductos = new ArrayList<>(updatedProducts); // Replace with updated list
            saveData(this.currentProductos); // Save the new state
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public long getNextId() {
        return idCounter.getAndIncrement();
    }
}