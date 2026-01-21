package com.muiyuro.library.library_management_api.services;

import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.dtos.BookDTO;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.entities.Book;
import com.muiyuro.library.library_management_api.repositories.AuthorRepository;
import com.muiyuro.library.library_management_api.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


//enabling mockito annotation
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private AuthorRepository authorRepository;
    
    @Spy
    private ModelMapper modelMapper;
    
    @InjectMocks
    private BookService bookService;
    
    //Argument captor to verify what was passed in save()
    @Captor
    private ArgumentCaptor<Book> bookArgumentCaptor;

    private Author testAuthor;
    private Book testBook;
    
    @BeforeEach
    void setUp() {
        // Initialize Author object
        testAuthor = new Author();
        testAuthor.setId(1L); // Manually set ID since we aren't using a DB
        testAuthor.setName("Test Author");
        testAuthor.setBio("Test Bio");

        // Initialize Book object
        testBook = new Book();
        testBook.setId(1L); // Manually set ID
        testBook.setTitle("Test Book");
        testBook.setIsbn("123456789");
        testBook.setYearPublished(2005);
        testBook.setAuthor(testAuthor);
    }



    @Test
    @DisplayName("Test createBook with an EXISTING Author")
    void testCreateBook_WithExistingAuthor(){
        //Arrange
        //Create an input DTO with ID
        BookDTO inputDto = modelMapper.map(testBook, BookDTO.class);
        inputDto.setId(null);
        //mock repository
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        //Act
        BookDTO createdBook = bookService.createBook(inputDto);

        //Assert
        assertNotNull(createdBook);
        assertEquals(testBook.getTitle(), createdBook.getTitle());
        assertEquals(testBook.getId(), createdBook.getId());

        //Verify interactions
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(bookArgumentCaptor.capture());

        //Check the entity that was actually passed to save
        Book captureBook = bookArgumentCaptor.getValue();
        assertEquals("Test Book", captureBook.getTitle());
        assertEquals("123456789", captureBook.getIsbn());
        assertEquals(2005, captureBook.getYearPublished());
        assertEquals(testAuthor, captureBook.getAuthor());

    }

    @Test
    @DisplayName("Test createBook with a NEW Author")
    void testCreateBook_WithNewAuthor(){
        //Arrange
        // 1. Create input DTO with a NEW Author (no ID)
        BookDTO inputDto = new BookDTO();
        inputDto.setTitle("New Book");
        inputDto.setIsbn("111222333");
        inputDto.setYearPublished(2024);
        
        AuthorDTO newAuthorDto = new AuthorDTO();
        newAuthorDto.setName("New Author");
        newAuthorDto.setBio("New Bio");
        inputDto.setAuthor(newAuthorDto);

        // 2. Mock saving the new author
        Author savedAuthor = new Author();
        savedAuthor.setId(2L);
        savedAuthor.setName("New Author");
        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        // 3. Mock saving the book
        Book savedBook = new Book();
        savedBook.setId(2L);
        savedBook.setTitle("New Book");
        savedBook.setAuthor(savedAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        //Act
        BookDTO result = bookService.createBook(inputDto);

        //Assert
        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        assertEquals("New Author", result.getAuthor().getName());

        // Verify that findById was NOT called for author, but save WAS called
        verify(authorRepository, never()).findById(any());
        verify(authorRepository).save(any(Author.class));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Test updateBook successfully")
    void testUpdateBook() {
        //Arrange
        // 1. Prepare the input DTO with NEW values
        BookDTO updateDto = new BookDTO();
        updateDto.setTitle("Updated Title");
        updateDto.setIsbn("987654321");
        updateDto.setYearPublished(2020);
        // We include the author because the service checks it.
        // In this case, we keep the SAME author.
        updateDto.setAuthor(modelMapper.map(testAuthor, com.muiyuro.library.library_management_api.dtos.AuthorDTO.class));

        // 2. Mock finding the EXISTING book
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // 3. Mock finding the EXISTING author (since DTO has author ID)
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));

        // 4. Mock saving the book.
        // IMPORTANT: We return the 'testBook' because the service modifies it in place!
        // Since 'testBook' is a reference object, the service updates its fields directly.
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        //

        //Act
        BookDTO result = bookService.updateBook(1L, updateDto);

        //Assert
        assertNotNull(result);

        // Verify the result matches the UPDATE (because testBook was modified in memory)
        assertEquals("Updated Title", result.getTitle());
        assertEquals("987654321", result.getIsbn());
        assertEquals(2020, result.getYearPublished());

        // Verify interactions
        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(bookArgumentCaptor.capture());

        // Check the entity that was actually passed to save
        Book capturedBook = bookArgumentCaptor.getValue();
        assertEquals("Updated Title", capturedBook.getTitle());
        assertEquals("987654321", capturedBook.getIsbn());
        assertEquals(2020, capturedBook.getYearPublished());
        assertEquals(testAuthor, capturedBook.getAuthor());
    }

    @Test
    @DisplayName("Test updateBook throws exception when book not found")
    void testUpdateBook_NotFound() {
        //Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        BookDTO updateDto = new BookDTO();

        //Act & Assert
        assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(99L, updateDto));

        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test updateBook without updating Author (Author is null in DTO)")
    void testUpdateBook_NoAuthorUpdate() {
        //Arrange
        BookDTO updateDto = new BookDTO();
        updateDto.setTitle("Updated Title");
        // Author is NULL by default here

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        //Act
        BookDTO result = bookService.updateBook(1L, updateDto);

        //Assert
        assertEquals("Updated Title", result.getTitle());
        // Ensure the author remains the ORIGINAL one
        assertEquals("Test Author", result.getAuthor().getName());

        // Verify author repo was NEVER touched
        verify(authorRepository, never()).findById(any());
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete Book successfully")
    void testDeleteBook_whenSuccessful() {
        //Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);

        //Act - calling the method
        bookService.deleteBook(1L);

        //Assert
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);

    }

    @Test
    @DisplayName("Test deleteBook throws an exception.")
    void testDeleteBook_whenFail(){
        //Arrange
        when(bookRepository.existsById(1L)).thenReturn(false);

        //Act and assert
        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(1L));

        //Assert
        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(1L);

    }



    @DisplayName("Test to get book by ID")
    @Test
    void getBookByID() {
        //Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));


        //Act
        BookDTO result = bookService.getBookByID(1L);

        //Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("123456789", result.getIsbn());
        assertEquals(2005, result.getYearPublished());
        assertEquals("Test Author", result.getAuthor().getName());

        verify(bookRepository).findById(1L);

    }

    @Test
    @DisplayName("Test getBookByID throws exception when not found")
    void getBookByID_NotFound() {
        //Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookByID(99L));

        verify(bookRepository).findById(99L);
    }

    @Test
    void getAllBooks() {
        //Arrange
        when(bookRepository.findAllWithAuthors()).thenReturn(List.of(testBook));

        //Act
        List<BookDTO> listOfBooks = bookService.getAllBooks();

        //Assert
        assertNotNull(listOfBooks);
        assertEquals(1, listOfBooks.size());
        assertEquals("Test Book", listOfBooks.get(0).getTitle());

        verify(bookRepository).findAllWithAuthors();

    }



    @Test
    @DisplayName("Test to get Book by Author ID")
    void getBookByAuthorID() {
        //Arrange
        when(bookRepository.findAllByAuthorId(1L)).thenReturn(List.of(testBook));


        //Act
        List<BookDTO> listOfBooksByAuthor = bookService.getBookByAuthorID(1L);

        //Assert
        assertThat(listOfBooksByAuthor).isNotEmpty();
        assertThat(listOfBooksByAuthor.get(0).getTitle()).isEqualTo("Test Book");
        assertThat(listOfBooksByAuthor.get(0).getAuthor().getName()).isEqualTo("Test Author");

        verify(bookRepository).findAllByAuthorId(1L);

    }

    @Test
    void getBookByTitle() {
        //Arrange
        when(bookRepository.findAllByTitleContainingIgnoreCase("test")).thenReturn(List.of(testBook));

        //Act
        List<BookDTO> listOfBookByTitle = bookService.getBookByTitle("test");

        //Assert
        assertThat(listOfBookByTitle).isNotEmpty();
        assertThat(listOfBookByTitle.get(0).getTitle()).isEqualTo("Test Book");

        verify(bookRepository).findAllByTitleContainingIgnoreCase("test");

    }

    @Test
    void getBookPublishedDuringOrAfterCertainDate() {
        //Arrange
        when(bookRepository.findAllByYearPublishedGreaterThanEqual(2004)).thenReturn(List.of(testBook));

        //Act
        List<BookDTO> bookByPublicationYear = bookService.getBookPublishedDuringOrAfterCertainDate(2004);

        //Assert
        assertThat(bookByPublicationYear).isNotEmpty();
        assertThat(bookByPublicationYear.get(0).getTitle()).isEqualTo("Test Book");

        verify(bookRepository).findAllByYearPublishedGreaterThanEqual(2004);

    }

    @Test
    @DisplayName("Testing the get or create Author if not present")
    void testGetOrCreateAuthor_whenAuthorNotFound_ThrowException(){
        //Arrange
        AuthorDTO existingAuthorDTO = new AuthorDTO();
        existingAuthorDTO.setId(100L);

        when(authorRepository.findById(100L)).thenReturn(Optional.empty());


        //Act and Assert
        assertThrows(EntityNotFoundException.class, () -> bookService.getOrCreateAuthor(existingAuthorDTO));


        verify(authorRepository).findById(100L);
        verify(authorRepository, never()).save(any());

    }

    @Test
    @DisplayName("Test getOrCreateAuthor creates new Author when ID provided")
    void testGetOrCreateAuthor_CreatesNew(){
        //Arrange
        AuthorDTO newAuthorDTO = new AuthorDTO();
        newAuthorDTO.setName("Test Author");
        newAuthorDTO.setBio("Test Bio");

        Author savedAuthor = new Author();
        savedAuthor.setId(1L);
        savedAuthor.setName("New Author");
        savedAuthor.setBio("New Bio");

        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        //Act
        Author createdAuthor = bookService.getOrCreateAuthor(newAuthorDTO);

        //Assert
        assertNotNull(createdAuthor);
        assertEquals("New Author", createdAuthor.getName());
        assertThat(1L).isEqualTo(createdAuthor.getId());

        //verify
        verify(authorRepository).save(any(Author.class));
        
    }

}