package com.muiyuro.library.library_management_api.repositories;

import com.muiyuro.library.library_management_api.config.AuditConfig;
import com.muiyuro.library.library_management_api.entities.Author;
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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditConfig.class)
class AuthorRepositoryTest {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    private Author author;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setName("Arthur Morgan");
        author.setBio("You are a good man.");
        testEntityManager.persistAndFlush(author);
        
    }
    

    @Test
    @DisplayName("Should find author by name ignoring case and partial match")
    void findAuthorByNameContainingIgnoreCase() {
        //Act - Search for partial name in lowercase to test "Containing" and "IgnoreCase"
        List<Author> authors = authorRepository.findAuthorByNameContainingIgnoreCase("arthur");
        
        //Assert
        assertThat(authors).isNotEmpty();
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("Arthur Morgan");
    }

    @Test
    @DisplayName("Should find author by ID")
    void findById() {
        //Act
        Optional<Author> foundAuthor = authorRepository.findById(author.getId());

        //Assert
        assertThat(foundAuthor).isPresent();
        assertThat(foundAuthor.get().getName()).isEqualTo("Arthur Morgan");

    }
}