package com.rocket.rocket.service;

import com.rocket.rocket.model.Book;
import com.rocket.rocket.model.Loan;
import com.rocket.rocket.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Envía un correo electrónico al estudiante con la información de su préstamo
     * @param student Estudiante destinatario
     * @param loan Préstamo realizado
     * @param book Libro prestado
     */
    public void sendLoanConfirmationEmail(Student student, Loan loan, Book book) {
        try {
            // Preparar el contexto para la plantilla
            final Context ctx = new Context(new Locale("es", "ES"));
            ctx.setVariable("student", student);
            ctx.setVariable("loan", loan);
            ctx.setVariable("book", book);
            ctx.setVariable("fechaPrestamo", loan.getFechaPrestamo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            ctx.setVariable("fechaDevolucion", loan.getFechaDevolucion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Procesar la plantilla
            final String htmlContent = templateEngine.process("emails/loan-confirmation", ctx);

            // Crear y enviar el mensaje
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("biblioteca@tuinstitucion.edu");
            helper.setTo(student.getEmail());
            helper.setSubject("Confirmación de Préstamo de Libro");
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            // Loguear el error, pero no detener el proceso
            System.err.println("Error al enviar correo electrónico: " + e.getMessage());
        }
    }

}