package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus; // Importar HttpStatus para ResponseEntity
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.VentaRequestDTO;
import com.example.demo.dto.VentaResponseDTO;
import com.example.demo.service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ventas")
@Tag(name = "ventas", description = "Web Service - operaciones con la venta")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/procesar")
    @Operation(summary = "Procesar una nueva venta", description = "Procesa una lista de items de venta, actualiza el stock de productos y registra la venta.")
    @ApiResponse(responseCode = "201", description = "Venta procesada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<VentaResponseDTO> procesarVenta(
            @Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO response = ventaService.procesarVenta(request); // Llamada sincrónica
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Devolver 201 Created
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/consultar/{id}")
    @Operation(summary = "Obtener detalles de una venta por ID", description = "Retorna la información de una venta específica identificada por su ID.")
    @ApiResponse(responseCode = "200", description = "Venta encontrada")
    @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    public VentaResponseDTO obtenerVenta(
            @Parameter(description = "ID de la venta", required = true) @PathVariable Long id) {
        return ventaService.obtenerVentaPorId(id); // Llamada sincrónica
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Listar todas las ventas", description = "Retorna una lista de todas las ventas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente")
    public List<VentaResponseDTO> listarTodasLasVentas() {
        return ventaService.obtenerTodasLasVentas(); // Llamada sincrónica
    }
}