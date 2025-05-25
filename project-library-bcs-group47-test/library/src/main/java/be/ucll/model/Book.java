package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Book extends Publication {

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "\\d{3}-\\d-\\d{3}-\\d{5}-\\d", message = "ISBN must be in the format 978-0-545-01022-1")
    private String isbn;

    public Book() { super(); }

    public Book(String title, String author, String isbn, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setAuthor(author);
        setIsbn(isbn);
    }

    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author is required");
        }
        this.author = author;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        if (isbn.length() != 17 || isbn.charAt(3) != '-' || isbn.charAt(5) != '-' ||
                isbn.charAt(9) != '-' || isbn.charAt(15) != '-') {
            throw new IllegalArgumentException("ISBN must be in the format 978-0-545-01022-1");
        }
        this.isbn = isbn;
    }
}
