package com.muiyuro.library.library_management_api.services;

import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.entities.Book;
import com.muiyuro.library.library_management_api.repositories.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    
    @Mock 
    private AuthorRepository authorRepository;
    
    @Spy
    private ModelMapper modelMapper;
    
    @InjectMocks
    private AuthorService authorService;
    
    private Author testAuthor;
    private Book testBook;
    
    
    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Author's Book");
        testBook.setIsbn("123456789");
        testBook.setYearPublished(2005);
        
        
        testAuthor = new Author();
        testAuthor.setId(1L); 
        testAuthor.setName("Cool Author");
        testAuthor.setBio("Cool Bio");
        
        testBook.setAuthor(testAuthor);
        testAuthor.setBooks(new ArrayList<>(List.of(testBook)));

    }


    @Test
    void getAllAuthors() {
        //assign
        when(authorRepository.findAll()).thenReturn(List.of(testAuthor));

        //Act
        List<AuthorDTO> authorDTO = authorService.getAllAuthors();

        //assert
        assertThat(authorDTO).isNotEmpty();
        assertThat(authorDTO.get(0).getName()).isEqualTo("Cool Author");
        assertThat(authorDTO.get(0).getBio()).isEqualTo("Cool Bio");

        //verify
        verify(authorRepository).findAll();
    }

    @Test
    void createAuthor() {
        //assign
        AuthorDTO newAuthor = new AuthorDTO();
        newAuthor.setName("New Author");
        newAuthor.setBio("New Bio");

        Author savedAuthor = new Author();
        savedAuthor.setId(2L);
        savedAuthor.setName("New Author");
        savedAuthor.setBio("New Bio");

        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);
        
        //act 
        AuthorDTO createdAuthor = authorService.createAuthor(newAuthor);

        //assert
        assertNotNull(createdAuthor);
        assertThat(createdAuthor.getName()).isEqualTo("New Author");
        assertThat(createdAuthor.getBio()).isEqualTo("New Bio");
        assertThat(createdAuthor.getId()).isEqualTo(2L);

        //verify
        verify(authorRepository).save(any(Author.class));

    }

    @Test
    void getAuthorByName() {
        //Assign
        when(authorRepository.findAuthorByNameContainingIgnoreCase("Cool")).thenReturn(List.of(testAuthor));

        //Act
        List<AuthorDTO> authorDTO = authorService.getAuthorByName("Cool");

        //Assert
        assertThat(authorDTO).isNotEmpty();
        assertThat(authorDTO.get(0).getName()).isEqualTo("Cool Author");

        //verify
        verify(authorRepository).findAuthorByNameContainingIgnoreCase("Cool");


    }

    @Test
    void getAuthorByID() {
        //Assign
        when(authorRepository.findById(1L)).thenReturn(java.util.Optional.of(testAuthor));

        //Act
        AuthorDTO authorDTO = authorService.getAuthorByID(1L);

        //Assert
        assertNotNull(authorDTO);
        assertThat(authorDTO.getName()).isEqualTo("Cool Author");

        //verify
        verify(authorRepository).findById(1L);

    }

    @Test
    void updateAuthorDetails() {
        //Assign
        AuthorDTO update = new AuthorDTO();
        update.setName("Cool Bro");
        update.setBio("Cool Bro");

        when(authorRepository.findById(1L)).thenReturn(java.util.Optional.of(testAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);

        //Act
        AuthorDTO updatedAuthor = authorService.updateAuthorDetails(1L, update);

        //Assert
        assertNotNull(updatedAuthor);
        assertThat(updatedAuthor.getName()).isEqualTo("Cool Bro");
        assertThat(updatedAuthor.getBio()).isEqualTo("Cool Bro");

        //verify
        verify(authorRepository).findById(1L);

    }

    @Test
    void deleteAuthor() {
        //Assign
        when(authorRepository.existsById(1L)).thenReturn(true);

        //Act
        authorService.deleteAuthor(1L);

        //Assert
        verify(authorRepository).existsById(1L);
        verify(authorRepository).deleteById(1L);


    }
}