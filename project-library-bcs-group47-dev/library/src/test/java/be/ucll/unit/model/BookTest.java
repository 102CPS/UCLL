package be.ucll.unit.model;

import be.ucll.model.Book;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookTest {

    @Test
    public void createsBookWithValidValues() {
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0-545-01022-1", 1925, 10);
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
        assertEquals("978-0-545-01022-1", book.getIsbn());
        assertEquals(1925, book.getPublicationYear());
        assertEquals(10, book.getAvailableCopies());
    }

    @Test
    public void throwsExceptionWhenTitleIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Book("", "Author", "978-0-545-01022-1", 2000, 5);
        });
        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenAuthorIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Book("Book Title", "", "978-0-545-01022-1", 2000, 5);
        });
        assertEquals("Author is required", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenIsbnIsInvalid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Book("Book Title", "Author", "12345", 2000, 5);
        });
        assertEquals("ISBN must be in the format 978-0-545-01022-1", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenPublicationYearIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Book("Book Title", "Author", "978-0-545-01022-1", -100, 5);
        });
        assertEquals("Publication year must be a positive integer and not in the future.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenPublicationYearIsInFuture() {
        int nextYear = LocalDate.now().getYear() + 1;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Book("Book Title", "Author", "978-0-545-01022-1", nextYear, 5);
        });
        assertEquals("Publication year must be a positive integer and not in the future.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenAvailableCopiesIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Book("Book Title", "Author", "978-0-545-01022-1", 2000, -5);
        });
        assertEquals("Available copies cannot be negative.", exception.getMessage());
    }
}
