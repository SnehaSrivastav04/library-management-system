package com.sneha.librarymanagement.repository;

import com.sneha.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}