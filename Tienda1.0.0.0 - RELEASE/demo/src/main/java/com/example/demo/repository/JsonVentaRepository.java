package com.example.demo.repository;

import com.example.demo.model.Venta;
import com.example.demo.store.VentaFileStore;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JsonVentaRepository implements VentaRepository {

    private final VentaFileStore ventaFileStore;

    public JsonVentaRepository(VentaFileStore ventaFileStore) {
        this.ventaFileStore = ventaFileStore;
    }

    @Override
    public Venta saveVenta(Venta venta) {
        try {
            List<Venta> currentVentas = new ArrayList<>(ventaFileStore.getVentas());
            Venta ventaToSave = null;

            if (venta.getId() == null) {
                Long newId = ventaFileStore.getNextId();
                venta.setId(newId);
                currentVentas.add(venta);
                ventaToSave = venta;
            } else {
                // Para una venta, usualmente no se actualiza, solo se crea.
                // Si permitieras actualizar, necesitarías lógica similar a ProductoRepository.
                // Por simplicidad, asumimos que saveVenta es para nuevas ventas.
                // Si el ID existe y lo pasas, lo trataremos como una nueva venta con un nuevo
                // ID.
                Long newId = ventaFileStore.getNextId();
                venta.setId(newId);
                currentVentas.add(venta);
                ventaToSave = venta;
            }
            ventaFileStore.updateAndSaveVentas(currentVentas);
            return ventaToSave;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la venta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Venta> findAllVentas() {
        return ventaFileStore.getVentas();
    }

    @Override
    public Optional<Venta> findVentaById(Long id) {
        return ventaFileStore.getVentas().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }
}