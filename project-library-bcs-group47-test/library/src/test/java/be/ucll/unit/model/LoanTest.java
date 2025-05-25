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
        // Updated: expect initial availableCopies since Loan constructor no longer reduces it
        assertEquals(2, book.getAvailableCopies());
        assertEquals(3, magazine.getAvailableCopies());
        assertEquals(LocalDate.now().plusDays(30), loan.getEndDate()); // Your Loan sets endDate +30 days
    }


    @Test
    public void testInvalidLoans() {
        User user = new User("Alice", "password123", "alice@mail.com", 25);
        Book availableBook = new Book("Available Book", "Author", "978-0-545-01022-2", 2021, 2);
        Book unavailableBook = new Book("Unavailable Book", "Author", "978-0-545-01022-3", 2022, 0);

        // User is required
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(null, Arrays.asList(availableBook), LocalDate.now()));
        assertEquals("User is required.", ex1.getMessage());

        // List is required (null)
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(user, null, LocalDate.now()));
        assertEquals("List is required.", ex2.getMessage());

        // List is required (empty)
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(user, Collections.emptyList(), LocalDate.now()));
        assertEquals("List is required.", ex3.getMessage());

        // Start date is required
        IllegalArgumentException ex4 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(user, Arrays.asList(availableBook), null));
        assertEquals("Start date is required.", ex4.getMessage());

        // Start date in the future
        IllegalArgumentException ex5 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(user, Arrays.asList(availableBook), LocalDate.now().plusDays(1)));
        assertEquals("Start date cannot be in the future.", ex5.getMessage());

        // No copies available - this might fail if Loan doesn't check for this
        IllegalArgumentException ex6 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(user, Arrays.asList(unavailableBook), LocalDate.now()));
        assertEquals("Unable to lend publication. No copies available for Unavailable Book.", ex6.getMessage());

        // No copies available for at least one publication
        IllegalArgumentException ex7 = assertThrows(IllegalArgumentException.class,
                () -> new Loan(user, Arrays.asList(availableBook, unavailableBook), LocalDate.now()));
        assertEquals("Unable to lend publication. No copies available for Unavailable Book.", ex7.getMessage());
    }

}
