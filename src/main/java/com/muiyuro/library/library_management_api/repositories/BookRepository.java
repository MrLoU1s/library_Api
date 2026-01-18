package com.muiyuro.library.library_management_api.repositories;

import com.muiyuro.library.library_management_api.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Efficiently fetch books and their authors in one query
    @Query("SELECT b FROM Book b JOIN FETCH b.author")
    List<Book> findAllWithAuthors();

    //Retrieve a single book by ID



    //Find all books by a specific author
    List<Book> findAllByAuthorId(Long authorId);

    //Find books by title through a flexible search
    List<Book> findAllByTitleContainingIgnoreCase(String title);

    //Find books published after a certain date
    List<Book> findAllByYearPublishedGreaterThanEqual(int year);


}

