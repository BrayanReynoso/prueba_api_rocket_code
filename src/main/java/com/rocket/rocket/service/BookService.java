package com.rocket.rocket.service;

import com.rocket.rocket.mapper.BookMapper;
import com.rocket.rocket.model.Book;
import com.rocket.rocket.utils.CustomResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    /**
     * Busca un libro por su ID.
     * @param id ID del libro a buscar (debe ser positivo)
     * @return CustomResponse con el libro encontrado (200),
     *         no encontrado (404) o error (500)
     */
    public CustomResponse<Book> findById(Long id) {
        try {
            Book book = bookMapper.findById(id);
            if (book != null) {
                return new CustomResponse<>(book, 200, "Libro encontrado", false);
            } else {
                return new CustomResponse<>(null, 404, "Libro no encontrado", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error interno en el servidor: " + e.getMessage(), true);
        }
    }

    /**
     * Obtiene todos los libros disponibles en el sistema.
     * @return CustomResponse con la lista de libros (200),
     *         lista vacía (404) o error (500)
     */
    public CustomResponse<List<Book>> findAll() {
        try {
            List<Book> books = bookMapper.findAll();
            if (books != null && !books.isEmpty()) {
                return new CustomResponse<>(books, 200, "Libros obtenidos exitosamente", false);
            } else {
                return new CustomResponse<>(null, 404, "No hay libros disponibles", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error interno en el servidor: " + e.getMessage(), true);
        }
    }

    /**
     * Obtiene solo los libros marcados como disponibles.
     * @return CustomResponse con la lista de libros disponibles (200),
     *         lista vacía (404) o error (500)
     */
    public CustomResponse<List<Book>> findAvailable() {
        try {
            List<Book> books = bookMapper.findByAvailability(true);
            if (books != null && !books.isEmpty()) {
                return new CustomResponse<>(books, 200, "Libros disponibles obtenidos exitosamente", false);
            } else {
                return new CustomResponse<>(null, 404, "No hay libros disponibles", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error interno en el servidor: " + e.getMessage(), true);
        }
    }

    /**
     * Busca libros que coincidan con un título específico.
     * @param title Título o parte del título a buscar
     * @return CustomResponse con la lista de libros encontrados (200),
     *         no encontrados (404) o error (500)
     */
    public CustomResponse<List<Book>> findByTitle(String title) {
        try {
            List<Book> books = bookMapper.findByTitle(title);
            if (books != null && !books.isEmpty()) {
                return new CustomResponse<>(books, 200, "Libros encontrados por título", false);
            } else {
                return new CustomResponse<>(null, 404, "No se encontraron libros con ese título", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error interno en el servidor: " + e.getMessage(), true);
        }
    }

    /**
     * Busca libros por nombre de autor.
     * @param author Nombre del autor a buscar
     * @return CustomResponse con la lista de libros del autor (200),
     *         no encontrados (404) o error (500)
     */
    public CustomResponse<List<Book>> findByAuthor(String author) {
        try {
            List<Book> books = bookMapper.findByAuthor(author);
            if (books != null && !books.isEmpty()) {
                return new CustomResponse<>(books, 200, "Libros encontrados por autor", false);
            } else {
                return new CustomResponse<>(null, 404, "No se encontraron libros de ese autor", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error interno en el servidor: " + e.getMessage(), true);
        }
    }

    /**
     * Crea un nuevo registro de libro en el sistema.
     * @param book Objeto Book con los datos del nuevo libro
     * @return CustomResponse con el libro creado (201)
     *         o error al guardar (500)
     */
    public CustomResponse<Book> save(Book book) {
        try {
            // Establecer disponibilidad predeterminada si no se proporciona
            if (book.getDisponible() == null) {
                book.setDisponible(true);
            }

            bookMapper.insertBook(book);
            return new CustomResponse<>(book, 201, "Libro creado exitosamente", false);
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al guardar el libro: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza la información de un libro existente.
     * @param book Objeto Book con los datos actualizados
     * @return CustomResponse con el libro actualizado (200),
     *         no encontrado (404) o error (500)
     */

    public CustomResponse<Book> update(Book book) {
        try {
            Book existingBook = bookMapper.findById(book.getId());
            if (existingBook != null) {
                bookMapper.updateBook(book);
                Book updatedBook = bookMapper.findById(book.getId());
                return new CustomResponse<>(updatedBook, 200, "Libro actualizado exitosamente", false);
            } else {
                return new CustomResponse<>(null, 404, "Libro no encontrado para actualizar", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al actualizar el libro: " + e.getMessage(), true);
        }
    }

    /**
     * Alterna el estado de disponibilidad de un libro.
     * @param id ID del libro a modificar
     * @return CustomResponse con el libro actualizado (200),
     *         no encontrado (404) o error (500)
     */
    public CustomResponse<Book> toggleAvailability(Long id) {
        try {
            Book book = bookMapper.findById(id);
            if (book != null) {
                // Invertir el estado de disponibilidad
                book.setDisponible(!book.getDisponible());
                bookMapper.updateAvailability(id, book.getDisponible());

                // Obtener el libro actualizado
                Book updatedBook = bookMapper.findById(id);
                String message = book.getDisponible() ?
                        "Libro marcado como disponible" :
                        "Libro marcado como no disponible";

                return new CustomResponse<>(updatedBook, 200, message, false);
            } else {
                return new CustomResponse<>(null, 404, "Libro no encontrado", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al cambiar disponibilidad del libro: " + e.getMessage(), true);
        }
    }
    /**
     * Actualiza el stock de un libro.
     * @param id ID del libro a actualizar (debe ser positivo)
     * @param stock Nueva cantidad de stock (no puede ser negativo)
     * @return CustomResponse indicando el resultado de la operación
     */
    public CustomResponse<Void> updateStock(Long id, Integer stock) {
        try {
            // Validaciones de entrada
            if (id == null || id <= 0) {
                return new CustomResponse<>(null, 400, "ID de libro inválido", true);
            }
            if (stock == null || stock < 0) {
                return new CustomResponse<>(null, 400, "El stock no puede ser negativo", true);
            }

            // Verificar si el libro existe antes de actualizar
            Book existingBook = bookMapper.findById(id);
            if (existingBook == null) {
                return new CustomResponse<>(null, 404, "Libro no encontrado", true);
            }

            // Actualizar el stock
            bookMapper.addStock(id, stock);

            return new CustomResponse<>(null, 200, "Stock actualizado correctamente", false);

        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al actualizar stock: " + e.getMessage(), true);
        }
    }
    /**
     * Desactiva un libro marcándolo como no disponible
     * @param id ID del libro a desactivar
     * @return CustomResponse confirmando la desactivación (200),
     *         no encontrado (404) o error (500)
     */
    public CustomResponse<String> delete(Long id) {
        try {
            Book book = bookMapper.findById(id);
            if (book != null) {
                // En lugar de eliminar, marcar como no disponible
                bookMapper.updateAvailability(id, false);
                return new CustomResponse<>("Libro marcado como no disponible", 200, "Libro desactivado", false);
            } else {
                return new CustomResponse<>(null, 404, "Libro no encontrado", true);
            }
        } catch (Exception e) {
            return new CustomResponse<>(null, 500, "Error al desactivar el libro: " + e.getMessage(), true);
        }
    }
}