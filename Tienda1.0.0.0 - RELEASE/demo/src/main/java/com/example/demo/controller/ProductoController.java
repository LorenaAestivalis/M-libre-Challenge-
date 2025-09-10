package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ProductoCambioPrecioRequestDTO;
import com.example.demo.dto.ProductoRequestDTO;
import com.example.demo.dto.ProductoResponseDTO;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.Producto; // Importar Producto
import com.example.demo.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.Validator; // Mantener si tienes un Validator inyectado, aunque no se use en el ejemplo

@RestController
@RequestMapping("/productos") // Base path for all endpoints in this controller
@Tag(name = "productos", description = "Web Service - operaciones con el producto")
@Validated // Enables method-level validation if you were to use it
public class ProductoController {

    private final ProductoService productoService;
    // private final Validator validator; // Used for explicit validation, not
    // needed if using @Valid

    public ProductoController(ProductoService productoService, Validator validator) { // Mantener Validator si lo
                                                                                      // inyectas
        this.productoService = productoService;
        // this.validator = validator;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna una lista de todos los productos disponibles.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    public List<ProductoResponseDTO> listarProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos(); // Llamada sincrónica
        return productos.stream()
                .map(ProductoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/consultar/{id}")
    @Operation(summary = "Busqueda de producto por ID", description = "Retorna la informacion de un producto especifico identificado por su ID.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ProductoResponseDTO obtenerProducto(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id); // Llamada sincrónica
        return ProductoMapper.toResponseDTO(producto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear-producto")
    @Operation(summary = "Crear producto", description = "Crear un nuevo producto. Envie los datos mandatorios.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente con ID y nombre")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO request) {

        Producto savedProducto = productoService.guardarProducto(ProductoMapper.toEntity(request)); // Llamada
                                                                                                    // sincrónica

        // Create the custom response DTO with the message
        ProductoResponseDTO response = new ProductoResponseDTO(
                savedProducto.getId(),
                savedProducto.getNombre(),
                "Producto creado exitosamente.");
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Return 201 Created
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-producto/{id}")
    @Operation(summary = "Eliminar producto por ID", description = "Elimina un producto existente identificado por su ID.")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente (No Content)")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado para eliminar")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor al eliminar el producto")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", required = true) @PathVariable Long id) {
        productoService.eliminarProducto(id); // Llamada sincrónica (asumimos que devuelve void o un booleano, y la
                                              // lógica de respuesta se maneja aquí)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/cambiar-precio")
    @Operation(summary = "Cambiar precio del producto por ID", description = "Cambia el precio de un producto existente identificado por su ID.")
    @ApiResponse(responseCode = "200", description = "Precio actualizado exitosamente en el producto")
    @ApiResponse(responseCode = "400", description = "Producto no encontrado o datos inválidos")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar el precio del producto")
    public ResponseEntity<ProductoResponseDTO> cambiarPrecio(
            @Valid @RequestBody ProductoCambioPrecioRequestDTO request) {

        Producto producto = productoService.cambiarPrecioProducto(request); // Llamada sincrónica

        // Crear respuesta con mensaje específico
        ProductoResponseDTO response = new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                "Exitosamente precio actualizado.");
        return ResponseEntity.ok(response);
    }
}