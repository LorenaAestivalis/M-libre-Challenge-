package com.example.demo.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VentaRequestDTO {

    @NotEmpty(message = "Error:[Operacion] No Valida")
    @Valid
    private List<ItemVentaDTO> items;
}
