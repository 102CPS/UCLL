package be.ucll.unit.model;

import be.ucll.model.Magazine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MagazineTest {

    @Test
    public void createsMagazineWithValidValues() {
        Magazine magazine = new Magazine("Title", "jhon", "random", 2025, 10);
        assertEquals("Title", magazine.getTitle());
        assertEquals("jhon", magazine.getEditor());
        assertEquals("random", magazine.getIssn());
        assertEquals(2025, magazine.getPublicationYear());
        assertEquals(10, magazine.getAvailableCopies());
    }

    @Test
    public void throwsExceptionWhenEditorIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Magazine("Magazine Title", "", "random", 2025, 10);
        });
        assertEquals("Editor is required.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenIssnIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Magazine("Magazine Title", "John", "", 2025, 10);
        });
        assertEquals("ISSN is required.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenAvailableCopiesIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Magazine("Magazine Title", "John", "random", 2025, -5);
        });
        assertEquals("Available copies cannot be negative.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenPublicationYearIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Magazine("Magazine Title", "John", "random", -100, 10);
        });
        assertEquals("Publication year must be a positive integer and not in the future.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenPublicationYearIsInFuture() {
        int nextYear = 2026;  // Assuming current year is 2025
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Magazine("Magazine Title", "John", "random", nextYear, 10);
        });
        assertEquals("Publication year must be a positive integer and not in the future.", exception.getMessage());
    }
}
