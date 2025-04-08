package com.rocket.rocket.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private Long id;
    private Long usuarioId;    // ID del usuario que hace el préstamo
    private Long libroId;      // ID del libro prestado
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private String estado;     // "ACTIVO", "DEVUELTO", "VENCIDO"

    // Objetos relacionados (para joins)
    private Student usuario;   // Relación con el usuario que hace el préstamo
    private Book libro;        // Relación con el libro prestado

    // Método para verificar si un préstamo está vencido
    public boolean isVencido() {
        if (fechaDevolucion == null) {
            return false; // Sin fecha de devolución definida
        }
        return LocalDate.now().isAfter(fechaDevolucion) && !"DEVUELTO".equals(estado);
    }

    // Método para calcular días restantes
    public long diasRestantes() {
        if (fechaDevolucion == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaDevolucion);
    }
}