package be.ucll.unit.model;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.Magazine;
import be.ucll.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnPublicationsTest {

    @Test
    public void testReturningPublications() {
        User user = new User("Alice", "password123", "alice@mail.com", 25);
        Book book = new Book("Test Book", "Author", "978-0-545-01022-1", 2020, 2);
        Magazine magazine = new Magazine("Test Magazine", "Editor", "1234-5678-9101", 2022, 3);

        Loan loan = new Loan(user, Arrays.asList(book, magazine), LocalDate.now());
        loan.returnPublication();

        assertEquals(2, book.getAvailableCopies()); // Incremented by 1
        assertEquals(3, magazine.getAvailableCopies()); // Incremented by 1
    }

    @Test
    public void testReturningPublicationsWithNoLoans() {
        User user = new User("Alice", "password123", "alice@mail.com", 25);
        Book book = new Book("Test Book", "Author", "978-0-545-01022-1", 2020, 2);

        assertEquals(2, book.getAvailableCopies()); // No loan, count should remain the same
    }
}
