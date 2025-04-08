package com.rocket.rocket.mapper;

import com.rocket.rocket.model.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanMapper {
    Loan findById(Long id);
    Loan findByIdWithDetails(Long id);
    List<Loan> findAll();
    List<Loan> findAllWithDetails();
    List<Loan> findByUsuarioIdWithDetails(Long usuarioId);
    int countActiveLoansByStudentId(Long studentId);
    void insertLoan(Loan prestamo);
    void updateLoan(Loan prestamo);
    void updateEstado(Long id, String estado);
    void deleteLoan(Long id);
    List<Loan> findByStudentNameWithDetails(String nombre);
}