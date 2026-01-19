package com.muiyuro.library.library_management_api.services;

import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.dtos.BookDTO;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.entities.Book;
import com.muiyuro.library.library_management_api.repositories.AuthorRepository;
import com.muiyuro.library.library_management_api.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    //Create Book
    @Transactional
    public  BookDTO createBook(BookDTO bookDTO) {
        log.info("Attempting to create a new book with title: {}", bookDTO.getTitle());
        Author author = getOrCreateAuthor(bookDTO.getAuthor());

        Book newbook = modelMapper.map(bookDTO, Book.class);
        newbook.setAuthor(author);

        Book savedBook = bookRepository.save(newbook);
        log.info("Successfully created book with ID: {}", savedBook.getId());
        return modelMapper.map(savedBook, BookDTO.class);

    }

    //Get or Create Author if not present
    private Author getOrCreateAuthor(AuthorDTO authorDto) {
        //If ID is present
        if(authorDto.getId() != null && authorDto.getId() > 0 ){
            return authorRepository.findById(authorDto.getId())
                    .orElseThrow(()-> new EntityNotFoundException("Author not found with ID: " + authorDto.getId()));
        }
        //Create new
        log.info("Creating new author: {}", authorDto.getName());
        Author newAuthor =  modelMapper.map(authorDto, Author.class);
        return authorRepository.save(newAuthor);

    }

    //Update the Book details
    @Transactional
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {
        log.info("Attempting to update book with ID: {}", bookId);
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("Book not found with ID: " + bookId));

        //existing simple fields
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setYearPublished(bookDTO.getYearPublished());


        //Update Author if provided
        if(bookDTO.getAuthor() != null) {
            Author author = getOrCreateAuthor(bookDTO.getAuthor());
            existingBook.setAuthor(author);
        }

        Book updatedBook = bookRepository.save(existingBook);
        log.info("Successfully updated book with ID: {}", updatedBook.getId());
        return modelMapper.map(updatedBook, BookDTO.class);
    }

    //Delete Book by ID
    @Transactional
    public void deleteBook(Long bookId){
        log.info("Attempting to delete book with ID: {}", bookId);
        if(!bookRepository.existsById(bookId)){
            log.error("Failed to delete. Book not found with ID: {}", bookId);
            throw new EntityNotFoundException("Book not found with ID: " + bookId);
        }
        bookRepository.deleteById(bookId);
        log.info("Successfully deleted book with ID: {}", bookId);
    }

    //Get Book by ID
    public BookDTO getBookByID(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("Book not found with ID: " + bookId));
        return modelMapper.map(book, BookDTO.class);
    }

    //Get all Books and respective authors
    public List<BookDTO> getAllBooks(){
        List<Book> bookList = bookRepository. findAllWithAuthors();
        return bookList.stream()
                .map(book-> modelMapper.map(book,BookDTO.class ))
                .collect(Collectors.toList());
    }

    //Get books by a specific author
    public List<BookDTO> getBookByAuthorID(Long authorId){
        List<Book> bookList = bookRepository.findAllByAuthorId(authorId);
        return bookList.stream()
                .map(book-> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    //Get books by title
    public List<BookDTO> getBookByTitle(String title){
        List<Book> bookList = bookRepository.findAllByTitleContainingIgnoreCase(title);
        return bookList.stream()
                .map(book-> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    //Get books published after a certain date
    public List<BookDTO> getBookPublishedDuringOrAfterCertainDate(int year){
        List<Book> bookList = bookRepository.findAllByYearPublishedGreaterThanEqual(year);
        return bookList.stream()
                .map(book-> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());

    }
}
