package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemVentaDTO {

    @NotNull(message = "Error:[Id] Dato mandatorio")
    @Positive(message = "Error:[Id] Dato invalido]")
    private Long productoId;

    @NotNull(message = "Error:[cantidad] Dato Mandatorio")
    @Min(value = 1, message = "Error:[cantidad] Dato invalido") // cantidad debe >0
    private Integer cantidad;
}
