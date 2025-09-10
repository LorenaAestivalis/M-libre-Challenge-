package com.example.demo.store;

import com.example.demo.model.Venta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Importar para LocalDateTime
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class VentaFileStore {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Value("${app.data.ventas-json-path:classpath:ventas.json}")
    private String ventasJsonPath; // Path for initial loading

    @Value("${app.data.ventas-save-path:./ventas_data.json}")
    private String ventasSavePath; // Path for saving persistent changes

    private List<Venta> currentVentas = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(0L);

    public VentaFileStore(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        // Clonar el ObjectMapper y añadir el módulo de JavaTime para LocalDateTime
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT);
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        loadData();
        idCounter.set(currentVentas.stream()
                .mapToLong(Venta::getId)
                .max()
                .orElse(0L) + 1);
        System.out.println("VentaFileStore initialized. Next available ID: " + idCounter.get());
    }

    private void loadData() throws IOException {
        readWriteLock.writeLock().lock();
        try {
            File saveFile = new File(ventasSavePath);
            List<Venta> loadedData;

            if (saveFile.exists() && saveFile.canRead()) {
                System.out.println("Loading sales from persistence file: " + ventasSavePath);
                try (InputStream is = Files.newInputStream(saveFile.toPath())) {
                    loadedData = objectMapper.readValue(is, new TypeReference<List<Venta>>() {
                    });
                }
            } else {
                System.out.println("Persistence file not found. Loading from classpath: " + ventasJsonPath);
                Resource resource = resourceLoader.getResource(ventasJsonPath);
                if (!resource.exists()) {
                    System.err.println(
                            "Initial classpath JSON resource for sales not found. Starting with an empty list.");
                    loadedData = new ArrayList<>();
                } else {
                    try (InputStream is = resource.getInputStream()) {
                        loadedData = objectMapper.readValue(is, new TypeReference<List<Venta>>() {
                        });
                    }
                    // Immediately save the initial load to the writable path
                    saveData(loadedData);
                }
            }
            this.currentVentas = loadedData; // Update the in-memory list
            System.out.println("Loaded " + this.currentVentas.size() + " sales into memory.");
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void saveData(List<Venta> dataToSave) throws IOException {
        File tempFile = File.createTempFile("ventas", ".json");
        objectMapper.writeValue(tempFile, dataToSave);
        Files.move(tempFile.toPath(), new File(ventasSavePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Sales successfully saved to: " + ventasSavePath);
    }

    public List<Venta> getVentas() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(currentVentas); // Return a defensive copy
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public synchronized void updateAndSaveVentas(List<Venta> updatedVentas) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            this.currentVentas = new ArrayList<>(updatedVentas);
            saveData(this.currentVentas);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public long getNextId() {
        return idCounter.getAndIncrement();
    }
}