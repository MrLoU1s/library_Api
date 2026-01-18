package com.muiyuro.library.library_management_api.repositories;

import com.muiyuro.library.library_management_api.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository< Author, Long> {

    //Find Author by ID is already sorted by JpaRepository

    //Find Author by name
    List<Author> findAuthorByNameContainingIgnoreCase(String name);




}
