package com.muiyuro.library.library_management_api.controllers;

import com.muiyuro.library.library_management_api.TestContainersConfiguration;
import com.muiyuro.library.library_management_api.TestSecurityConfig;
import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.repositories.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestContainersConfiguration.class, TestSecurityConfig.class})
@AutoConfigureWebTestClient(timeout = "10000")
class AuthorControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;
    private AuthorDTO authorDTO;

    @BeforeEach
    void setUp(){
        //Clean up database
        authorRepository.deleteAll();

        //Create Author (Transient)
        testAuthor = Author.builder()
                .name("Test Author")
                .bio("Test Bio")
                .build();

        // Create DTO (Transient)
        authorDTO = AuthorDTO.builder()
                .name("Test Author")
                .bio("Test Bio")
                .build();
    }

    @Test
    void testCreateAuthor_Success() {
        // We don't save the author first. We let the API create it.
        
        webTestClient.post()
                .uri("/api/authors")
                .bodyValue(authorDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo(authorDTO.getName())
                .jsonPath("$.bio").isEqualTo(authorDTO.getBio())
                .jsonPath("$.id").isNotEmpty(); // Verify ID is generated
    }

    @Test
    void testUpdateAuthor_Success() {
        Author savedAuthor = authorRepository.save(testAuthor);

        authorDTO.setId(savedAuthor.getId());
        authorDTO.setName("Updated Author");
        authorDTO.setBio("Updated Bio");

        webTestClient.put()
                .uri("/api/authors/{authorId}", savedAuthor.getId())
                .bodyValue(authorDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedAuthor.getId())
                .jsonPath("$.name").isEqualTo("Updated Author")
                .jsonPath("$.bio").isEqualTo("Updated Bio");
    }

    @Test
    void testDeleteAuthor_Success() {
        Author savedAuthor = authorRepository.save(testAuthor);

        webTestClient.delete()
                .uri("/api/authors/{authorId}", savedAuthor.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void testGetAuthorByID_Success() {
        Author savedAuthor = authorRepository.save(testAuthor);

        webTestClient.get()
                .uri("/api/authors/{authorId}", savedAuthor.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedAuthor.getId())
                .jsonPath("$.name").isEqualTo(savedAuthor.getName())
                .jsonPath("$.bio").isEqualTo(savedAuthor.getBio());
    }

    @Test
    void testGetAllAuthors_Success() {
        Author savedAuthor = authorRepository.save(testAuthor);

        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(savedAuthor.getId())
                .jsonPath("$[0].name").isEqualTo(savedAuthor.getName())
                .jsonPath("$[0].bio").isEqualTo(savedAuthor.getBio());
    }

    @Test
    void testGetAuthorsByName_Success() {
        Author savedAuthor = authorRepository.save(testAuthor);
        
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/authors/search/name")
                        .queryParam("name", savedAuthor.getName()) // Corrected query param name
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(savedAuthor.getId())
                .jsonPath("$[0].name").isEqualTo(savedAuthor.getName())
                .jsonPath("$[0].bio").isEqualTo(savedAuthor.getBio());  
    }
}