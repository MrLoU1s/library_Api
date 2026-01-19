package com.muiyuro.library.library_management_api.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {
    private Long id;

    private String name;

    private String bio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    //Ensure no circular dependency, hence remove:
    //private List<BookDTO> books;
}
