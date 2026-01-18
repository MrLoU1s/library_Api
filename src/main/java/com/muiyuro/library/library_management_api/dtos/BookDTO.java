package com.muiyuro.library.library_management_api.dtos;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private long id;

    private String title;

    private String isbn;

    private int yearPublished;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //relationship
    private AuthorDTO author;
}
