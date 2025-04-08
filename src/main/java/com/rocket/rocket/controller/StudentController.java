package com.rocket.rocket.controller;

import com.rocket.rocket.model.Student;
import com.rocket.rocket.service.StudentService;
import com.rocket.rocket.utils.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("${API-URL}/students")
@RestController
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Obtener todos los estudiantes
    @GetMapping("/all")
    public ResponseEntity<CustomResponse<List<Student>>> getAllStudents() {
        CustomResponse<List<Student>> response = studentService.findAll();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Obtener estudiante por matrícula
    @GetMapping("/get-by-matricula/{matricula}")
    public ResponseEntity<CustomResponse<Student>> getStudentByMatricula(@PathVariable String matricula) {
        CustomResponse<Student> response = studentService.findByMatricula(matricula);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Obtener estudiante por email
    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<CustomResponse<List<Student>>> getStudentsByEmailLike(@PathVariable String email) {
        CustomResponse<List<Student>> response = studentService.findByEmailLike(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // Registrar estudiante
    @PostMapping("/register")
    public ResponseEntity<CustomResponse<Student>> registerStudent(@RequestBody Student student) {
        CustomResponse<Student> response = studentService.save(student);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Actualizar estudiante
    @PutMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Student>> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        CustomResponse<Student> response = studentService.update(student);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // Eliminar estudiante
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponse<String>> deleteStudent(@PathVariable Long id) {
        CustomResponse<String> response = studentService.delete(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}