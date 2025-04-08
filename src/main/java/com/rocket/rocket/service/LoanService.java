package com.rocket.rocket.service;

import com.rocket.rocket.mapper.BookMapper;
import com.rocket.rocket.mapper.LoanMapper;
import com.rocket.rocket.mapper.StudentMapper;
import com.rocket.rocket.model.Book;
import com.rocket.rocket.model.Loan;
import com.rocket.rocket.model.Student;
import com.rocket.rocket.utils.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final LoanMapper loanMapper;
    private final BookMapper bookMapper;
    private final StudentMapper studentMapper;
    private final EmailService emailService;
    private static final int MAX_LOANS_PER_USER = 3;

    @Autowired
    public LoanService(LoanMapper loanMapper, BookMapper bookMapper,
                       StudentMapper studentMapper, EmailService emailService) {
        this.loanMapper = loanMapper;
        this.bookMapper = bookMapper;
        this.studentMapper = studentMapper;
        this.emailService = emailService;
    }

    /**
     * Obtiene un préstamo específico por su ID con todos sus detalles
     * @param id ID del préstamo a buscar
     * @return CustomResponse con el préstamo encontrado (200),
     *         no encontrado (404) o error del servidor (500)
     */
    public CustomResponse<Loan> getLoanById(Long id) {
        try {
            Loan loan = loanMapper.findByIdWithDetails(id);
            if (loan != null) {
                return new CustomResponse<>(loan, 200, "Préstamo encontrado exitosamente", false);
            } else {
                return new CustomResponse<>(null, 404, "No se encontró el préstamo con ID: " + id, true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al obtener el préstamo: " + e.getMessage(), true);
        }
    }

    /**
     * Obtiene todos los préstamos registrados en el sistema con detalles completos
     * @return CustomResponse con la lista de préstamos (200),
     *         lista vacía (200) o error del servidor (500)
     */

    public CustomResponse<List<Loan>> getAllLoans() {
        try {
            List<Loan> loans = loanMapper.findAllWithDetails();
            if (loans != null && !loans.isEmpty()) {
                return new CustomResponse<>(loans, 200,
                        "Se encontraron " + loans.size() + " préstamos", false);
            } else {
                return new CustomResponse<>(new ArrayList<>(), 200,
                        "No hay préstamos registrados en el sistema", false);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al obtener los préstamos: " + e.getMessage(), true);
        }
    }

    /**
     * Busca préstamos por nombre del estudiante
     * @param nombre Nombre (parcial o completo) del estudiante
     * @return Lista de préstamos o mensaje de error
     */
    public CustomResponse<List<Loan>> getLoansByStudentName(String nombre) {
        try {
            List<Loan> loans = loanMapper.findByStudentNameWithDetails(nombre);
            if (loans != null && !loans.isEmpty()) {
                return new CustomResponse<>(loans, 200,
                        "Se encontraron " + loans.size() + " préstamos para el nombre: " + nombre, false);
            } else {
                return new CustomResponse<>(new ArrayList<>(), 200,
                        "No se encontraron préstamos para el nombre: " + nombre, false);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al buscar préstamos por nombre: " + e.getMessage(), true);
        }
    }

    /**
     * Crea un nuevo registro de préstamo en el sistema
     * @param loan Objeto Loan con los datos del préstamo
     * @return CustomResponse con el préstamo creado (201),
     *         validaciones fallidas (400), no encontrado (404),
     *         conflictos (400) o error del servidor (500)
     * @throws Exception Si ocurre algún error durante el proceso
     */
    @Transactional
    public CustomResponse<Loan> crearLoan(Loan loan) {
        try {
            // Validar que el usuario existe
            Student student = studentMapper.findById(loan.getUsuarioId());
            if (student == null) {
                return new CustomResponse<>(null, 404,
                        "No se encontró al estudiante con ID: " + loan.getUsuarioId(), true);
            }

            // Validar que el libro existe
            Book book = bookMapper.findById(loan.getLibroId());
            if (book == null) {
                return new CustomResponse<>(null, 404,
                        "No se encontró el libro con ID: " + loan.getLibroId(), true);
            }

            // Verificar disponibilidad del libro
            if (book.getDisponible() != null && !book.getDisponible()) {
                return new CustomResponse<>(null, 400,
                        "El libro '" + book.getTitulo() + "' no está disponible para préstamo", true);
            }

            // Verificar si hay stock disponible
            if (book.getStock() != null && book.getStock() <= 0) {
                return new CustomResponse<>(null, 400,
                        "No hay ejemplares disponibles del libro '" + book.getTitulo() + "'", true);
            }

            // Obtener préstamos activos del usuario
            List<Loan> activeUserLoans = loanMapper.findByUsuarioIdWithDetails(loan.getUsuarioId());

            // Verificar que el usuario no tenga más de 3 préstamos activos
            long activeLoansCount = activeUserLoans.stream()
                    .filter(l -> "ACTIVO".equals(l.getEstado()))
                    .count();

            if (activeLoansCount >= MAX_LOANS_PER_USER) {
                return new CustomResponse<>(null, 400,
                        "El estudiante ya tiene " + MAX_LOANS_PER_USER + " préstamos activos. " +
                                "No puede solicitar más libros hasta devolver alguno.", true);
            }

            // Verificar si el usuario ya tiene un préstamo activo del mismo libro
            boolean hasSameBookActive = activeUserLoans.stream()
                    .anyMatch(l -> l.getLibroId().equals(loan.getLibroId()) &&
                            "ACTIVO".equals(l.getEstado()));

            if (hasSameBookActive) {
                return new CustomResponse<>(null, 400,
                        "El estudiante ya tiene un préstamo activo de este libro", true);
            }

            // Configurar fechas si no están establecidas
            if (loan.getFechaPrestamo() == null) {
                loan.setFechaPrestamo(LocalDate.now());
            }

            if (loan.getFechaDevolucion() == null) {
                loan.setFechaDevolucion(LocalDate.now().plusDays(15));
            }

            // Establecer estado inicial
            loan.setEstado("ACTIVO");

            // Guardar el préstamo
            loanMapper.insertLoan(loan);

            // Actualizar stock del libro
            book.setStock(book.getStock() - 1);
            if (book.getStock() <= 0) {
                book.setDisponible(false);
            }
            bookMapper.updateBook(book);

            // Obtener el préstamo con todos los detalles
            Loan savedLoan = loanMapper.findByIdWithDetails(loan.getId());

            // Enviar correo electrónico de confirmación
            try {
                emailService.sendLoanConfirmationEmail(student, savedLoan, book);
                return new CustomResponse<>(savedLoan, 201,
                        "Préstamo registrado exitosamente. Se ha enviado un correo de confirmación a " +
                                student.getEmail(), false);
            } catch (Exception e) {
                return new CustomResponse<>(savedLoan, 201,
                        "Préstamo registrado exitosamente. Error al enviar correo de confirmación: " +
                                e.getMessage(), false);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al crear el préstamo: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza la información de un préstamo existente
     * @param loan Objeto Loan con los datos actualizados
     * @return CustomResponse con el préstamo actualizado (200),
     *         no encontrado (404) o error del servidor (500)
     */
    @Transactional
    public CustomResponse<Loan> actualizarLoan(Loan loan) {
        try {
            // Verificar que el préstamo existe
            Loan existingLoan = loanMapper.findById(loan.getId());
            if (existingLoan == null) {
                return new CustomResponse<>(null, 404,
                        "No se encontró el préstamo con ID: " + loan.getId(), true);
            }

            // Actualizar el préstamo
            loanMapper.updateLoan(loan);

            // Obtener el préstamo actualizado con detalles
            Loan updatedLoan = loanMapper.findByIdWithDetails(loan.getId());

            return new CustomResponse<>(updatedLoan, 200,
                    "Préstamo actualizado exitosamente", false);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al actualizar el préstamo: " + e.getMessage(), true);
        }
    }

    /**
     * Registra la devolución de un libro prestado
     * @param id ID del préstamo a marcar como devuelto
     * @return CustomResponse con el préstamo actualizado (200),
     *         no encontrado (404), estado inválido (400) o error (500)
     */
    @Transactional
    public CustomResponse<Loan> registrarDevolucion(Long id) {
        try {
            // Verificar que el préstamo existe
            Loan loan = loanMapper.findByIdWithDetails(id);
            if (loan == null) {
                return new CustomResponse<>(null, 404,
                        "No se encontró el préstamo con ID: " + id, true);
            }

            // Verificar que el préstamo está activo
            if (!"ACTIVO".equals(loan.getEstado())) {
                return new CustomResponse<>(null, 400,
                        "El préstamo ya ha sido " +
                                ("DEVUELTO".equals(loan.getEstado()) ? "devuelto" : "cancelado"), true);
            }

            // Actualizar estado del préstamo
            loan.setEstado("DEVUELTO");
            loanMapper.updateEstado(id, "DEVUELTO");

            // Obtener y actualizar el libro
            Book book = bookMapper.findById(loan.getLibroId());
            if (book != null) {
                book.setStock(book.getStock() + 1);
                book.setDisponible(true);
                bookMapper.updateBook(book);
            }

            // Obtener el préstamo actualizado con detalles
            Loan updatedLoan = loanMapper.findByIdWithDetails(id);

            return new CustomResponse<>(updatedLoan, 200,
                    "Devolución registrada exitosamente. El libro '" +
                            (loan.getLibro() != null ? loan.getLibro().getTitulo() : "seleccionado") +
                            "' ha sido devuelto al inventario", false);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al registrar la devolución: " + e.getMessage(), true);
        }
    }

    /**
     * Cancela un préstamo activo
     * @param id ID del préstamo a cancelar
     * @return CustomResponse con el préstamo actualizado (200),
     *         no encontrado (404), estado inválido (400) o error (500)
     */
    @Transactional
    public CustomResponse<Loan> cancelarPrestamo(Long id) {
        try {
            // Verificar que el préstamo existe
            Loan loan = loanMapper.findByIdWithDetails(id);
            if (loan == null) {
                return new CustomResponse<>(null, 404,
                        "No se encontró el préstamo con ID: " + id, true);
            }

            // Verificar que el préstamo está activo
            if (!"ACTIVO".equals(loan.getEstado())) {
                return new CustomResponse<>(null, 400,
                        "El préstamo ya ha sido " +
                                ("DEVUELTO".equals(loan.getEstado()) ? "devuelto" : "cancelado"), true);
            }

            // Actualizar estado del préstamo
            loan.setEstado("CANCELADO");
            loanMapper.updateEstado(id, "CANCELADO");

            // Obtener y actualizar el libro
            Book book = bookMapper.findById(loan.getLibroId());
            if (book != null) {
                book.setStock(book.getStock() + 1);
                book.setDisponible(true);
                bookMapper.updateBook(book);
            }

            // Obtener el préstamo actualizado con detalles
            Loan updatedLoan = loanMapper.findByIdWithDetails(id);

            return new CustomResponse<>(updatedLoan, 200,
                    "Préstamo cancelado exitosamente. El libro '" +
                            (loan.getLibro() != null ? loan.getLibro().getTitulo() : "seleccionado") +
                            "' ha sido devuelto al inventario", false);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al cancelar el préstamo: " + e.getMessage(), true);
        }
    }

    /**
     * Elimina permanentemente un registro de préstamo (solo si está devuelto o cancelado)
     * @param id ID del préstamo a eliminar
     * @return CustomResponse con confirmación (200),
     *         no encontrado (404), préstamo activo (400) o error (500)
     */
    @Transactional
    public CustomResponse<Void> eliminarLoan(Long id) {
        try {
            // Verificar que el préstamo existe
            Loan loan = loanMapper.findById(id);
            if (loan == null) {
                return new CustomResponse<>(null, 404,
                        "No se encontró el préstamo con ID: " + id, true);
            }

            // Verificar que el préstamo no está activo
            if ("ACTIVO".equals(loan.getEstado())) {
                return new CustomResponse<>(null, 400,
                        "No se puede eliminar un préstamo activo. Debe registrar la devolución o cancelarlo primero", true);
            }

            // Eliminar el préstamo
            loanMapper.deleteLoan(id);

            return new CustomResponse<>(null, 200,
                    "Registro de préstamo eliminado exitosamente", false);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500,
                    "Error al eliminar el préstamo: " + e.getMessage(), true);
        }
    }
}