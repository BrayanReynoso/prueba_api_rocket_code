package com.rocket.rocket.mapper;

import com.rocket.rocket.model.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMapper {
    Book findById(Long id);
    List<Book> findAll();
    void insertBook(Book libro);
    void updateBook(Book libro);

    List<Book> findByTitle(@Param("titulo") String title);
    List<Book> findByAuthor(@Param("autor") String author);
    List<Book> findByAvailability(@Param("disponible") Boolean available);
    void updateAvailability(@Param("id") Long id, @Param("disponible") Boolean available);
    void addStock(@Param("id") Long id, @Param("amount") Integer amount);
}