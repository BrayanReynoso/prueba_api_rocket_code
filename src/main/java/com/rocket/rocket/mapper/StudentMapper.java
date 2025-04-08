package com.rocket.rocket.mapper;

import com.rocket.rocket.model.Student;

import java.util.List;

public interface StudentMapper {
    Student findById(Long id);
    Student findByEmail(String email);
    Student findByMatricula(String matricula);
    List<Student> findAll();
    void insertStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(Long id);
    List<Student> findByEmailLike(String email);
}