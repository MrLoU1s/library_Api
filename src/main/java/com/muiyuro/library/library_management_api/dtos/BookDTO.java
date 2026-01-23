package com.muiyuro.library.library_management_api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Size(max=20, message = "ISBN must be at most 20 characters")
    @Pattern(regexp = "^[0-9]{10,13}(-[0-9X])?$", message = "Invalid ISBN format")
    private String isbn;

    @Min(value = 1000, message = "Publication year must be after 999")
    @Max(value = 2027, message = "Publication year cannot be in the future")
    private int yearPublished;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //relationship
    @Valid
    private AuthorDTO author;
}
