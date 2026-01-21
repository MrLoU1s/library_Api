package com.muiyuro.library.library_management_api.controllers;

import com.muiyuro.library.library_management_api.dtos.BookDTO;
import com.muiyuro.library.library_management_api.services.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    //endpoint for creating book
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@Valid  @RequestBody BookDTO book) {
        return bookService.createBook(book);
    }
    
    //endpoint for updating book
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBookById(@PathVariable Long id, @Valid  @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(id, bookDTO);
    }
    
    //endpoint for deleting book
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Changed to 204 No Content
    public void deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
    }
    
    //endpoint for get book by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO getBookByID(@PathVariable Long id){
        return bookService.getBookByID(id);
    }
    
    //endpoint for get all books
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookDTO> getAllBooks(){
        return bookService.getAllBooks();
    }
    
    //endpoint for get books by author ID
    @GetMapping("/author/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BookDTO> getBookByAuthorID(@PathVariable Long authorId){
        return bookService.getBookByAuthorID(authorId);
    }
    
    //endpoint for get books by title
    @GetMapping("/search/title")
    @ResponseStatus(HttpStatus.OK)
    public List<BookDTO> getBookByTitle(@RequestParam String title){
        return bookService.getBookByTitle( title);
    }
    
    //endpoint for get books published after a certain date
    @GetMapping("/search/year")
    @ResponseStatus(HttpStatus.OK)
    public List<BookDTO> getBooksPublishedAfter(@RequestParam int year){
        return bookService.getBookPublishedDuringOrAfterCertainDate(year);
    }
}
