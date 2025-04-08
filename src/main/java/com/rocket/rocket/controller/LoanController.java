package com.rocket.rocket.controller;

import com.rocket.rocket.model.Loan;
import com.rocket.rocket.service.LoanService;
import com.rocket.rocket.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${API-URL}/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/all")
    public ResponseEntity<CustomResponse<List<Loan>>> getAllLoans() {
        CustomResponse<List<Loan>> response = loanService.getAllLoans();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Loan>> getLoanById(@PathVariable Long id) {
        CustomResponse<Loan> response = loanService.getLoanById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @GetMapping("/usuario/{nombre}")
    public ResponseEntity<CustomResponse<List<Loan>>> getLoansByNombreUsuario(@PathVariable String nombre) {
        CustomResponse<List<Loan>> response = loanService.getLoansByStudentName(nombre);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<CustomResponse<Loan>> createLoan(@RequestBody Loan loan) {
        CustomResponse<Loan> response = loanService.crearLoan(loan);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<Loan>> updateLoan(
            @PathVariable Long id,
            @RequestBody Loan loan) {
        loan.setId(id); // Asegurar que el ID del path coincide con el del body
        CustomResponse<Loan> response = loanService.actualizarLoan(loan);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PatchMapping("/{id}/devolver")
    public ResponseEntity<CustomResponse<Loan>> returnBook(@PathVariable Long id) {
        CustomResponse<Loan> response = loanService.registrarDevolucion(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CustomResponse<Loan>> cancelLoan(@PathVariable Long id) {
        CustomResponse<Loan> response = loanService.cancelarPrestamo(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Void>> deleteLoan(@PathVariable Long id) {
        CustomResponse<Void> response = loanService.eliminarLoan(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}