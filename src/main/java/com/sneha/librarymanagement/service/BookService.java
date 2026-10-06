package com.sneha.librarymanagement.service;

import com.sneha.librarymanagement.dto.BookRequest;
import com.sneha.librarymanagement.entity.Book;
import com.sneha.librarymanagement.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book with this ISBN already exists");
        }
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .isbn(request.isbn())
                .category(request.category())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .coverImageUrl(request.coverImageUrl())
                .build();
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id " + id));
    }

    public Book updateBook(Long id, BookRequest request) {
        Book book = getBookById(id);
        if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbn(request.isbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another book with this ISBN already exists");
        }

        int issuedCopies = book.getTotalCopies() - book.getAvailableCopies();
        if (request.totalCopies() < issuedCopies) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Total copies cannot be less than currently issued copies (" + issuedCopies + ")");
        }

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setCategory(request.category());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies() - issuedCopies);
        book.setCoverImageUrl(request.coverImageUrl());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }
}