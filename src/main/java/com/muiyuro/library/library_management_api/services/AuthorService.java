package com.muiyuro.library.library_management_api.services;

import com.muiyuro.library.library_management_api.dtos.AuthorDTO;
import com.muiyuro.library.library_management_api.dtos.BookDTO;
import com.muiyuro.library.library_management_api.entities.Author;
import com.muiyuro.library.library_management_api.repositories.AuthorRepository;
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
public class AuthorService {

    private AuthorRepository authorRepository;
    private ModelMapper modelMapper;

    //Retrieve Existing authors
    public List<AuthorDTO> getAllAuthors(){
        List<Author> authorList = authorRepository.findAll();
        return authorList.stream()
                .map(author -> modelMapper.map(author, AuthorDTO.class))
                .collect(Collectors.toList());
    }

    //Create Author
    @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO){
        log.info("Attempting to create new author: {}", authorDTO.getName());
        Author newAuthor = modelMapper.map(authorDTO, Author.class);
        Author savedAuthor = authorRepository.save(newAuthor);
        log.info("Successfully created author with ID: {}", savedAuthor.getId());
        return modelMapper.map(savedAuthor, AuthorDTO.class);
    }

    //Retrieve Author By Name
    public List<AuthorDTO> getAuthorByName(String name ){
        List<Author> authorList = authorRepository.findAuthorByNameContainingIgnoreCase(name);
        return authorList   .stream()
                .map(author-> modelMapper.map(author, AuthorDTO.class))
                .collect(Collectors.toList());
    }
    
    //Retrieve Author By ID
    public AuthorDTO getAuthorByID(Long authorId){
        Author author = authorRepository.findById(authorId)
                .orElseThrow(()-> new EntityNotFoundException("Author not found with ID: " + authorId));
        return modelMapper.map(author, AuthorDTO.class);

    }
    
    //Update Author
    @Transactional
    public AuthorDTO updateAuthorDetails(AuthorDTO authorDTO, Long authorId){
        log.info("Attempting to update author with ID: {}", authorId);
        Author existingAuthor = authorRepository.findById(authorId)
                .orElseThrow(()-> new EntityNotFoundException("Author not found with ID: " + authorId));
        
        existingAuthor.setName(authorDTO.getName());
        existingAuthor.setBio(authorDTO.getBio());
        
        Author updatedAuthor = authorRepository.save(existingAuthor);
        log.info("Successfully updated author with ID: {}", updatedAuthor.getId());
        return modelMapper.map(updatedAuthor, AuthorDTO.class);

    }
    
    //Delete Author By ID
    @Transactional
    public void deleteAuthor(Long authorId){
        log.info("Attempting to delete author with ID: {}", authorId);
        if(!authorRepository.existsById(authorId)) {
            log.error("Failed to delete. Author not found with ID: {}", authorId);
            throw new EntityNotFoundException("Author not found with ID: " + authorId);
        }
        authorRepository.deleteById(authorId);
        log.info("Successfully deleted author with ID: {}", authorId);
    }


}
