package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ItemVentaDTO;
import com.example.demo.dto.VentaRequestDTO;
import com.example.demo.dto.VentaResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Producto;
import com.example.demo.model.ProductoVendido;
import com.example.demo.model.Venta;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService; // Ahora usará métodos síncronos

    private final ConcurrentHashMap<Long, Lock> productLocks = new ConcurrentHashMap<>();

    @Autowired
    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository,
            ProductoService productoService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService; // Asegúrate de que esta instancia ya esté con métodos síncronos
    }

    @Transactional // Marca el método como transaccional
    public VentaResponseDTO procesarVenta(VentaRequestDTO request) {
        List<ProductoVendido> productosVendidos = new ArrayList<>();
        int totalVenta = 0; // Inicializar en 0
        List<Producto> productosActualizados = new ArrayList<>();

        // Paso 1: Validar stock y recopila información de los productos
        for (ItemVentaDTO item : request.getItems()) {
            // Se mantiene el lock para prevenir condiciones de carrera en un entorno
            // multi-hilo,
            // incluso si este servicio ya no es @Async. Si tienes múltiples hilos llamando
            // a procesarVenta, los locks siguen siendo importantes.
            Lock lock = productLocks.computeIfAbsent(item.getProductoId(), k -> new ReentrantLock());
            lock.lock();
            try {
                // Usar el método síncrono del repositorio directamente o del productoService
                Producto producto = productoRepository.findById(item.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", item.getProductoId()));

                if (producto.getStock() < item.getCantidad()) {
                    throw new IllegalArgumentException(
                            "No hay suficiente stock para el producto: " + producto.getNombre());
                }

                ProductoVendido productoVendido = new ProductoVendido(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getPrecio(),
                        item.getCantidad());
                productosVendidos.add(productoVendido);
                totalVenta += item.getCantidad() * producto.getPrecio();

                // Prepara el producto para la actualización de stock
                producto.setStock(producto.getStock() - item.getCantidad());
                productosActualizados.add(producto);

            } finally {
                lock.unlock(); // Liberar el lock del producto
            }
        }

        // Paso 2: Actualizar el stock de los productos de forma síncrona
        // Ya no necesitamos CompletableFuture.allOf().get()
        for (Producto p : productosActualizados) {
            productoService.guardarProducto(p); // Llamada directa al método síncrono
        }

        // Paso 3: Crear y guardar la venta
        Venta nuevaVenta = new Venta(
                null,
                LocalDateTime.now(),
                productosVendidos,
                totalVenta); // totalVenta ya es un int

        Venta ventaGuardada = ventaRepository.saveVenta(nuevaVenta);

        return new VentaResponseDTO(
                ventaGuardada.getId(),
                ventaGuardada.getFechaVenta(),
                ventaGuardada.getProductosVendidos(),
                ventaGuardada.getTotal());
    }

    public VentaResponseDTO obtenerVentaPorId(Long id) {
        return ventaRepository.findVentaById(id)
                .map(venta -> new VentaResponseDTO(
                        venta.getId(),
                        venta.getFechaVenta(),
                        venta.getProductosVendidos(),
                        venta.getTotal()))
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
    }

    public List<VentaResponseDTO> obtenerTodasLasVentas() {
        return ventaRepository.findAllVentas().stream()
                .map(venta -> new VentaResponseDTO(
                        venta.getId(),
                        venta.getFechaVenta(),
                        venta.getProductosVendidos(),
                        venta.getTotal()))
                .collect(Collectors.toList());
    }
}