package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Publication year must be a positive integer and not in the future.")
    @Min(value = 1, message = "Publication year must be a positive integer and not in the future.")
    private Integer publicationYear;

    @NotNull(message = "Available copies cannot be negative.")
    @Min(value = 0, message = "Available copies cannot be negative.")
    private Integer availableCopies;

    protected Publication() {}

    public Publication(String title, int publicationYear, int availableCopies) {
        setTitle(title);
        setPublicationYear(publicationYear);
        setAvailableCopies(availableCopies);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public int getPublicationYear() { return publicationYear; }
    public int getAvailableCopies() { return availableCopies; }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        this.title = title;
    }

    public void setPublicationYear(int publicationYear) {
        if (publicationYear < 0 || publicationYear > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Publication year must be a positive integer and not in the future.");
        }
        this.publicationYear = publicationYear;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative.");
        }
        this.availableCopies = availableCopies;
    }

    public void lendPublication() {
        if (availableCopies <= 0) {
            throw new IllegalArgumentException("No available copies left for publication.");
        }
        availableCopies--;
    }

    public void returnPublication() {
        this.availableCopies++;
    }
}
