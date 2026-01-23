package com.muiyuro.library.library_management_api.controllers;

import com.muiyuro.library.library_management_api.TestContainersConfiguration;
import com.muiyuro.library.library_management_api.TestSecurityConfig;
import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.dtos.BookDTO;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.entities.Book;
import com.muiyuro.library.library_management_api.repositories.AuthorRepository;
import com.muiyuro.library.library_management_api.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestContainersConfiguration.class, TestSecurityConfig.class})
@AutoConfigureWebTestClient(timeout = "10000")
class BookControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Book bookTest;

    private BookDTO bookDTO;

    private Author authorTest;

    private AuthorDTO authorDTO;

    @BeforeEach
    void setUp() {
        // Clean up database
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        // Create Author (Transient - NOT saved yet)
        authorTest = Author.builder()
                .name("Author Name")
                .bio("Author Bio")
                .build();

        // Create Book linked to Transient Author
        bookTest = Book.builder()
                .title("Book Title")
                .isbn("1234567890")
                .yearPublished(2023)
                .author(authorTest)
                .build();

        // Prepare DTOs (Transient)
        authorDTO = AuthorDTO.builder()
                .name("Author Name")
                .bio("Author Bio")
                .build();

        bookDTO = BookDTO.builder()
                .title("Book Title")
                .isbn("1234567890")
                .yearPublished(2023)
                .author(authorDTO)
                .build();
    }

    @Test
    void createBook() {
        // For creation, we usually want to link to an existing author or create a new one.
        // Assuming the API handles creating a new author if no ID is provided, 
        // or we can save one here to simulate linking to an existing author.
        
        // Let's save an author first to ensure we have a valid ID for the DTO
        Author savedAuthor = authorRepository.save(Author.builder()
                .name("Saved Author")
                .bio("Saved Bio")
                .build());
        
        // Update DTO to point to this saved author
        authorDTO.setId(savedAuthor.getId());
        authorDTO.setName(savedAuthor.getName());
        authorDTO.setBio(savedAuthor.getBio());
        bookDTO.setAuthor(authorDTO);

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(bookDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo(bookDTO.getTitle())
                .jsonPath("$.isbn").isEqualTo(bookDTO.getIsbn());
    }

    @Test
    void updateBookById() {
        // Saving bookTest will cascade and save authorTest because it is transient
        Book savedBook = bookRepository.save(bookTest);
        
        // Modify DTO for update
        bookDTO.setTitle("Updated Title");
        // Ensure DTO has the correct Author ID (if validation requires it)
        authorDTO.setId(savedBook.getAuthor().getId());
        bookDTO.setAuthor(authorDTO);
        
        webTestClient.put()
                .uri("/api/books/{id}", savedBook.getId())
                .bodyValue(bookDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedBook.getId())
                .jsonPath("$.title").isEqualTo("Updated Title")
                .jsonPath("$.isbn").isEqualTo(savedBook.getIsbn())
                .jsonPath("$.yearPublished").isEqualTo(savedBook.getYearPublished());
    }

    @Test
    void deleteBook() {
        Book savedBook = bookRepository.save(bookTest);
        
        webTestClient.delete()
                .uri("/api/books/{id}", savedBook.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void testGetBookByID_Success() {
        Book savedBook = bookRepository.save(bookTest);

        webTestClient.get()
                .uri("/api/books/{id}", savedBook.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedBook.getId())
                .jsonPath("$.title").isEqualTo(savedBook.getTitle())
                .jsonPath("$.isbn").isEqualTo(savedBook.getIsbn())
                .jsonPath("$.yearPublished").isEqualTo(savedBook.getYearPublished());
    }

    @Test
    void getBookById_notFound_returns404() {
        webTestClient.get()
                .uri("/api/books/9999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").value(containsString("not found"));
    }

    @Test
    void createBook_withBlankTitle_returns400() {
        bookDTO.setTitle(""); // Invalid title

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(bookDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetAllBooks(){
        Book savedBook = bookRepository.save(bookTest);
        
        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(savedBook.getId())
                .jsonPath("$[0].title").isEqualTo(savedBook.getTitle())
                .jsonPath("$[0].isbn").isEqualTo(savedBook.getIsbn());
    }

    @Test
    void testGetBookByAuthorID_Success() {
        Book savedBook = bookRepository.save(bookTest);
        
        webTestClient.get()
                .uri("/api/books/author/{authorId}", savedBook.getAuthor().getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(savedBook.getId())
                .jsonPath("$[0].title").isEqualTo(savedBook.getTitle())
                .jsonPath("$[0].isbn").isEqualTo(savedBook.getIsbn());
    }

    @Test
    void testGetBookByTitle_Success() {
        Book savedBook = bookRepository.save(bookTest);
        
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books/search/title")
                        .queryParam("title", savedBook.getTitle())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(savedBook.getId())
                .jsonPath("$[0].title").isEqualTo(savedBook.getTitle())
                .jsonPath("$[0].isbn").isEqualTo(savedBook.getIsbn());
    }

    @Test
    void testGetBooksPublishedAfter_Success() {
        Book savedBook = bookRepository.save(bookTest);
        
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books/search/year")
                        .queryParam("year", savedBook.getYearPublished())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(savedBook.getId())
                .jsonPath("$[0].title").isEqualTo(savedBook.getTitle())
                .jsonPath("$[0].isbn").isEqualTo(savedBook.getIsbn())
                .jsonPath("$[0].yearPublished").isEqualTo(savedBook.getYearPublished());
    }

    @Test
    void testGetBooksPublishedAfter_EmptyResult() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books/search/year")
                        .queryParam("year", 2025)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEmpty();
    }

}