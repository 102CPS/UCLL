package be.ucll.unit.model;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.Magazine;
import be.ucll.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoanTest {

    @Test
    public void testSuccessfulLoan() {
        User user = new User("Alice", "password123", "alice@mail.com", 25);
        Book book = new Book("Test Book", "Author", "978-0-545-01022-1", 2020, 2);
        Magazine magazine = new Magazine("Test Magazine", "Editor", "1234-5678-9101", 2022, 3);

        Loan loan = new Loan(user, Arrays.asList(book, magazine), LocalDate.now());

        assertEquals(user, loan.getUser());
        assertEquals(1, book.getAvailableCopies()); // Decreased by 1
        assertEquals(2, magazine.getAvailableCopies()); // Decreased by 1
        assertEquals(LocalDate.now().plusDays(21), loan.getEndDate());
    }

    @Test
    public void testInvalidLoans() {
        User user = new User("Alice", "password123", "alice@mail.com", 25);
        Book availableBook = new Book("Available Book", "Author", "978-0-545-01022-2", 2021, 2);
        Book unavailableBook = new Book("Unavailable Book", "Author", "978-0-545-01022-3", 2022, 0);

        assertThrows(IllegalArgumentException.class, () -> new Loan(null, Arrays.asList(availableBook), LocalDate.now()), "User is required.");
        assertThrows(IllegalArgumentException.class, () -> new Loan(user, null, LocalDate.now()), "List is required.");
        assertThrows(IllegalArgumentException.class, () -> new Loan(user, Collections.emptyList(), LocalDate.now()), "List is required.");
        assertThrows(IllegalArgumentException.class, () -> new Loan(user, Arrays.asList(availableBook), null), "Start date is required.");
        assertThrows(IllegalArgumentException.class, () -> new Loan(user, Arrays.asList(availableBook), LocalDate.now().plusDays(1)), "Start date cannot be in the future.");
        assertThrows(IllegalArgumentException.class, () -> new Loan(user, Arrays.asList(unavailableBook), LocalDate.now()), "Unable to lend publication. No copies available for Unavailable Book.");
        assertThrows(IllegalArgumentException.class, () -> new Loan(user, Arrays.asList(availableBook, unavailableBook), LocalDate.now()), "Unable to lend publication. No copies available for Unavailable Book.");
    }
}
