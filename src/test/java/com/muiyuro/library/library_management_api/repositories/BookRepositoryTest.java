package com.muiyuro.library.library_management_api.repositories;

import com.muiyuro.library.library_management_api.config.AuditConfig;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditConfig.class)
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Author testAuthor;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Create and persist Author
        testAuthor = new Author();
        testAuthor.setName("Test Author");
        testAuthor.setBio("Test Bio");
        testEntityManager.persist(testAuthor);

        // Create and persist Book (Common for read tests)
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setIsbn("123456789");
        testBook.setYearPublished(2005);
        testBook.setAuthor(testAuthor);
        testEntityManager.persistAndFlush(testBook);
    }

    @Test
    @DisplayName("Should save a new book and fetch it by ID")
    void testSaveBookAndFindById() {
        //Arrange - Create a NEW book different from the one in setUp to avoid ISBN conflict
        Book newBook = new Book();
        newBook.setTitle("Another Book");
        newBook.setIsbn("987654321"); // Different ISBN
        newBook.setYearPublished(2020);
        newBook.setAuthor(testAuthor);

        //Act
        Book savedBook = bookRepository.save(newBook);
        Optional<Book> found = bookRepository.findById(savedBook.getId());

        //Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Another Book");
        assertThat(found.get().getIsbn()).isEqualTo("987654321");
    }

    @Test
    @DisplayName("Should find all books with authors")
    void testFindAllWithAuthors() {
        //Act
        List<Book> books = bookRepository.findAllWithAuthors();

        //Assert
        assertThat(books).isNotEmpty();
        assertThat(books).extracting(Book::getTitle).contains("Test Book");
        assertThat(books.get(0).getAuthor()).isNotNull();
        assertThat(books.get(0).getAuthor().getName()).isEqualTo("Test Author");
    }

    @Test
    @DisplayName("Should find books by Author ID")
    void findAllByAuthorId() {
        //Act
        List<Book> books = bookRepository.findAllByAuthorId(testAuthor.getId());

        //Assert
        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor().getId()).isEqualTo(testAuthor.getId());
    }

    @Test
    @DisplayName("Should find books by title containing string (ignore case)")
    void findAllByTitleContainingIgnoreCase() {
        //Act
        List<Book> books = bookRepository.findAllByTitleContainingIgnoreCase("test");

        //Assert
        assertThat(books).isNotEmpty();
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    @DisplayName("Should find books published after or in a specific year")
    void findAllByYearPublishedGreaterThanEqual() {

        //Act
        List<Book> books = bookRepository.findAllByYearPublishedGreaterThanEqual(2000);

        //Assert
        assertThat(books).isNotEmpty();
        assertThat(books.get(0).getYearPublished()).isGreaterThanOrEqualTo(2000);
    }
}