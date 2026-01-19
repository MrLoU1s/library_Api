package com.muiyuro.library.library_management_api.controllers;

import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.services.AuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {
    
    private final AuthorService authorService;
    
    //endpoint for creating author
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO createAuthor( @Valid @RequestBody AuthorDTO authorDTO){
        return authorService.createAuthor(authorDTO);
    }
    
    //endpoint for updating Author details
    @PutMapping("/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDTO updateAuthor( @Valid @RequestBody AuthorDTO authorDTO, @PathVariable Long authorId){
        // Fixed argument order: DTO first, then ID
        return authorService.updateAuthorDetails( authorId, authorDTO);
    }
    
    //endpoint for deleting Author
    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Changed to 204 No Content
    public void deleteAuthor(@PathVariable Long authorId){
        authorService.deleteAuthor(authorId);
    }
    
    //endpoint for getting Author by ID
    @GetMapping("/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDTO getAuthorByID(@PathVariable Long authorId){
        return authorService.getAuthorByID(authorId);
    }
    
    //endpoint for getting all Authors
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorDTO> getAllAuthors(){
        return authorService.getAllAuthors();
    }
    
    //endpoint for getting Author by name
    @GetMapping("/search/name")
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorDTO> getAuthorsByName(@RequestParam String name){
        return authorService.getAuthorByName(name);
    }
}
