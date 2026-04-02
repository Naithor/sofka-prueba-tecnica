package com.naithor.sofkapruebatecnica.clientes.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El género es obligatorio")
    private String genero;
    
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "La edad mínima es 18 años")
    private Integer edad;
    
    @NotBlank(message = "La identificación es obligatoria")
    @Size(min = 5, max = 50, message = "La identificación debe tener entre 5 y 50 caracteres")
    private String identificacion;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 255, message = "La dirección debe tener entre 5 y 255 caracteres")
    private String direccion;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{7,20}$", message = "El teléfono debe contener entre 7 y 20 dígitos")
    private String telefono;
    
    @NotBlank(message = "El clienteId es obligatorio")
    @Size(min = 5, max = 50, message = "El clienteId debe tener entre 5 y 50 caracteres")
    private String clienteId;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 255, message = "La contraseña debe tener entre 4 y 255 caracteres")
    private String contrasena;
    
    private Boolean estado = true;
}
