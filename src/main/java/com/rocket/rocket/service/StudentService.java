package com.rocket.rocket.service;

import com.rocket.rocket.mapper.LoanMapper;
import com.rocket.rocket.mapper.StudentMapper;
import com.rocket.rocket.model.Student;
import com.rocket.rocket.utils.CustomResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class StudentService {

    private final StudentMapper studentMapper;
    private  final LoanMapper loanMapper;

    public StudentService(StudentMapper studentMapper, LoanMapper loanMapper) {
        this.studentMapper = studentMapper;
        this.loanMapper = loanMapper;
    }

    /**
     * Busca un estudiante por su número de matrícula
     * @param matricula Número de matrícula a buscar (no puede ser nulo o vacío)
     * @return CustomResponse con el estudiante encontrado (200),
     *         no encontrado (404), datos inválidos (400) o error (500)
     */
    public CustomResponse<Student> findByMatricula(String matricula) {
        try {
            if (matricula == null || matricula.trim().isEmpty()) {
                return new CustomResponse<>(null, 400, "La matrícula no puede ser nula o vacía", true);
            }

            Student student = studentMapper.findByMatricula(matricula);
            if (student != null) {
                return new CustomResponse<>(student, 200, "Estudiante con matrícula " + matricula + " encontrado", false);
            } else {
                return new CustomResponse<>(null, 404, "Estudiante con matrícula " + matricula + " no encontrado", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al buscar el estudiante por matrícula: " + e.getMessage(), true);
        }
    }

    /**
     * Busca estudiantes cuyos emails contengan una coincidencia parcial.
     * @param email Fragmento del email a buscar (no puede ser nulo o vacío)
     * @return CustomResponse con lista de estudiantes encontrados (200),
     *         vacía si no hay coincidencias, datos inválidos (400) o error (500)
     */
    public CustomResponse<List<Student>> findByEmailLike(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return new CustomResponse<>(null, 400, "El email no puede ser nulo o vacío", true);
            }

            List<Student> students = studentMapper.findByEmailLike(email);
            return new CustomResponse<>(students, 200, "Estudiantes encontrados con coincidencia en email", false);

        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al buscar estudiantes por email: " + e.getMessage(), true);
        }
    }
    /**
     * Obtiene la lista completa de estudiantes registrados
     * @return CustomResponse con la lista de estudiantes (200),
     *         lista vacía (200) o error (500)
     */
    public CustomResponse<List<Student>> findAll() {
        try {
            List<Student> students = studentMapper.findAll();
            if (students != null && !students.isEmpty()) {
                return new CustomResponse<>(students, 200, "Estudiantes obtenidos exitosamente", false);
            } else {
                return new CustomResponse<>(students, 200, "No hay estudiantes disponibles", false);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al obtener la lista de estudiantes: " + e.getMessage(), true);
        }
    }

    /**
     * Registra un nuevo estudiante en el sistema
     * @param student Objeto Student con los datos del nuevo estudiante
     * @return CustomResponse con el estudiante creado (201),
     *         datos inválidos (400), conflictos (409) o error (500)
     * @throws DuplicateKeyException si el email o matrícula ya existen
     * @throws DataIntegrityViolationException si hay violación de restricciones
     */
    @Transactional
    public CustomResponse<Student> save(Student student) {
        try {
            // Validar campos obligatorios
            if (student == null) {
                return new CustomResponse<>(null, 400, "El estudiante no puede ser nulo", true);
            }

            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                return new CustomResponse<>(null, 400, "El email no puede ser nulo o vacío", true);
            }

            if (student.getNombre() == null || student.getNombre().trim().isEmpty()) {
                return new CustomResponse<>(null, 400, "El nombre no puede ser nulo o vacío", true);
            }

            if (student.getApellidos() == null || student.getApellidos().trim().isEmpty()) {
                return new CustomResponse<>(null, 400, "Los apellidos no pueden ser nulos o vacíos", true);
            }

            // Verificación si el estudiante con el mismo email ya existe
            Student existingStudent = studentMapper.findByEmail(student.getEmail());
            if (existingStudent != null) {
                return new CustomResponse<>(null, 409, "El correo electrónico " + student.getEmail() + " ya está registrado", true);
            }
            // Verificación si el estudiante con la matricula ya existe
            Student existingStudentByMatricula = studentMapper.findByMatricula(student.getMatricula());
            if (existingStudentByMatricula != null) {
                return new CustomResponse<>(null, 409, "La matricula " + student.getMatricula() + " ya está registrado", true);
            }

            // Guardar el nuevo estudiante
            studentMapper.insertStudent(student);
            return new CustomResponse<>(student, 201, "Estudiante creado exitosamente con ID: " + student.getId(), false);
        } catch (DuplicateKeyException e) {
            return new CustomResponse<>(null, 409, "El correo electrónico ya está registrado", true);
        } catch (DataIntegrityViolationException e) {
            return new CustomResponse<>(null, 400, "Error de integridad de datos: " + e.getMessage(), true);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al guardar el estudiante: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza los datos de un estudiante existente
     * @param student Objeto Student con los datos actualizados
     * @return CustomResponse con el estudiante actualizado (200),
     *         no encontrado (404), conflictos (409) o error (500)
     * @throws DuplicateKeyException si el nuevo email o matrícula ya existen
     * @throws DataIntegrityViolationException si hay violación de restricciones
     */
    @Transactional
    public CustomResponse<Student> update(Student student) {
        try {
            // Validaciones básicas
            if (student == null || student.getId() == null) {
                return new CustomResponse<>(null, 400, "El estudiante y su ID no pueden ser nulos", true);
            }

            // Obtener estudiante existente
            Student existingStudent = studentMapper.findById(student.getId());
            if (existingStudent == null) {
                return new CustomResponse<>(null, 404, "Estudiante con ID " + student.getId() + " no encontrado", true);
            }

            // Validación de email duplicado (solo si cambió)
            if (!Objects.equals(existingStudent.getEmail(), student.getEmail())) {
                Student emailExists = studentMapper.findByEmail(student.getEmail());
                if (emailExists != null && !emailExists.getId().equals(student.getId())) {
                    return new CustomResponse<>(null, 409, "El correo electrónico " + student.getEmail() + " ya está siendo utilizado por otro usuario", true);
                }
            }

            // Validación de matrícula duplicada (solo si cambió)
            if (!Objects.equals(existingStudent.getMatricula(), student.getMatricula())) {
                Student matriculaExists = studentMapper.findByMatricula(student.getMatricula());
                if (matriculaExists != null && !matriculaExists.getId().equals(student.getId())) {
                    return new CustomResponse<>(null, 409, "La matrícula " + student.getMatricula() + " ya está siendo utilizada por otro estudiante", true);
                }
            }

            // Actualizar estudiante
            studentMapper.updateStudent(student);
            return new CustomResponse<>(student, 200, "Estudiante actualizado exitosamente", false);

        } catch (DuplicateKeyException e) {
            return new CustomResponse<>(null, 409, "El correo electrónico o matrícula ya está registrado", true);
        } catch (DataIntegrityViolationException e) {
            return new CustomResponse<>(null, 400, "Error de integridad de datos: " + e.getMessage(), true);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al actualizar el estudiante: " + e.getMessage(), true);
        }
    }
    /**
     * Elimina un estudiante del sistema
     * @param id ID del estudiante a eliminar (no puede ser nulo)
     * @return CustomResponse confirmando la eliminación (200),
     *         no encontrado (404), préstamos activos (400) o error (500)
     * @throws DataIntegrityViolationException si hay préstamos asociados
     */
    @Transactional
    public CustomResponse<String> delete(Long id) {
        try {
            if (id == null) {
                return new CustomResponse<>(null, 400, "El ID del estudiante no puede ser nulo", true);
            }

            Student student = studentMapper.findById(id);
            if (student == null) {
                return new CustomResponse<>(null, 404, "Estudiante con ID " + id + " no encontrado", true);
            }

            // Validar si tiene préstamos activos
            int activeLoans = loanMapper.countActiveLoansByStudentId(id);
            if (activeLoans > 0) {
                return new CustomResponse<>(null, 400, "No se puede eliminar el estudiante porque tiene préstamos activos.", true);
            }

            studentMapper.deleteStudent(id);
            return new CustomResponse<>("Estudiante eliminado exitosamente", 200, "Estudiante con ID " + id + " eliminado", false);

        } catch (DataIntegrityViolationException e) {
            return new CustomResponse<>(null, 400, "No se puede eliminar el estudiante debido a restricciones de integridad: " + e.getMessage(), true);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al eliminar el estudiante: " + e.getMessage(), true);
        }
    }
}