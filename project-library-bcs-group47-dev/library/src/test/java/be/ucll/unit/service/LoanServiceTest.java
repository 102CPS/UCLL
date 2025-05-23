package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanService = new LoanService(loanRepository, userRepository);
    }

    // Test subclass for abstract Publication class
    private static class TestPublication extends Publication {
        public TestPublication(String title, int publicationYear, int availableCopies) {
            super(title, publicationYear, availableCopies);
        }
    }

    @Test
    public void testGetLoansByUser_withActiveLoans() {
        // Arrange
        User user = new User("John Doe", "password123", "john.doe@example.com", 30);
        Publication publication = new TestPublication("Test Book", 2021, 5);
        Loan loan1 = new Loan(user, List.of(publication), LocalDate.now().minusDays(5));
        loan1.returnPublication(); // Loan is returned

        Loan loan2 = new Loan(user, List.of(publication), LocalDate.now().minusDays(10)); // Active loan

        when(userRepository.userExists(user.getEmail())).thenReturn(true);
        when(loanRepository.findLoansByUser(user.getEmail(), true)).thenReturn(List.of(loan2));

        // Act
        List<Loan> activeLoans = loanService.getLoansByUser(user.getEmail(), true);

        // Assert
        assertEquals(1, activeLoans.size());
        assertEquals(loan2, activeLoans.get(0));
        verify(loanRepository, times(1)).findLoansByUser(user.getEmail(), true);
    }

    @Test
    public void testGetLoansByUser_withNoActiveLoans() {
        // Arrange
        User user = new User("Jane Smith", "password456", "jane.smith@example.com", 25);
        Publication publication = new TestPublication("Another Test Book", 2020, 3);
        Loan loan1 = new Loan(user, List.of(publication), LocalDate.now().minusDays(15));
        loan1.returnPublication(); // Loan is returned

        when(userRepository.userExists(user.getEmail())).thenReturn(true);
        when(loanRepository.findLoansByUser(user.getEmail(), true)).thenReturn(List.of());

        // Act
        List<Loan> activeLoans = loanService.getLoansByUser(user.getEmail(), true);

        // Assert
        assertTrue(activeLoans.isEmpty());
        verify(loanRepository, times(1)).findLoansByUser(user.getEmail(), true);
    }

    @Test
    public void testGetLoansByUser_userDoesNotExist() {
        // Arrange
        String email = "nonexistent.user@example.com";

        when(userRepository.userExists(email)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            loanService.getLoansByUser(email, true);
        });
    }

    @Test
    public void testGetLoansByUser_withInactiveLoans() {
        // Arrange
        User user = new User("John Doe", "password123", "john.doe@example.com", 30);
        Publication publication = new TestPublication("Inactive Book", 2019, 2);
        Loan loan = new Loan(user, List.of(publication), LocalDate.now().minusDays(20));
        loan.returnPublication(); // Loan is returned

        when(userRepository.userExists(user.getEmail())).thenReturn(true);
        when(loanRepository.findLoansByUser(user.getEmail(), false)).thenReturn(List.of(loan));

        // Act
        List<Loan> inactiveLoans = loanService.getLoansByUser(user.getEmail(), false);

        // Assert
        assertEquals(1, inactiveLoans.size());
        assertEquals(loan, inactiveLoans.get(0));
        verify(loanRepository, times(1)).findLoansByUser(user.getEmail(), false);
    }

    // Story 16 - Test when a user doesn't exist
    @Test
    public void testDeleteUserLoans_UserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.userExists(email)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.deleteUserLoans(email);
        });
        assertEquals("User does not exist.", exception.getMessage());
        verify(loanRepository, never()).deleteByUserEmail(anyString());
    }

    // Story 16 - Test when a user has active loans
    @Test
    public void testDeleteUserLoans_UserHasActiveLoans() {
        // Arrange
        String email = "user@example.com";
        User user = new User("Test User", "password123", email, 30);
        Publication publication = new TestPublication("Test Book", 2021, 5);

        // Create active loan (end date in the future)
        Loan activeLoan = new Loan(user, List.of(publication), LocalDate.now().minusDays(10));
        List<Loan> loans = new ArrayList<>();
        loans.add(activeLoan);

        when(userRepository.userExists(email)).thenReturn(true);
        when(loanRepository.findLoansByUser(email, false)).thenReturn(loans);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.deleteUserLoans(email);
        });
        assertEquals("User has active loans.", exception.getMessage());
        verify(loanRepository, never()).deleteByUserEmail(anyString());
    }

    // Story 16 - Test when a user has no loans
    @Test
    public void testDeleteUserLoans_UserHasNoLoans() {
        // Arrange
        String email = "user.noloan@example.com";
        when(userRepository.userExists(email)).thenReturn(true);
        when(loanRepository.findLoansByUser(email, false)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.deleteUserLoans(email);
        });
        assertEquals("User has no loans.", exception.getMessage());
        verify(loanRepository, never()).deleteByUserEmail(anyString());
    }

    // Story 16 - Test successful deletion
    @Test
    public void testDeleteUserLoans_SuccessfulDeletion() {
        // Arrange
        String email = "user.expired@example.com";
        User user = new User("Test User", "password123", email, 30);
        Publication publication = new TestPublication("Old Book", 2019, 2);

        // Create expired loan (end date in the past)
        LocalDate pastDate = LocalDate.now().minusDays(30);
        Loan expiredLoan = new Loan(user, List.of(publication), pastDate.minusDays(30));
        // Manipulating end date to ensure it's in the past for testing
        expiredLoan.returnPublication();

        List<Loan> expiredLoans = new ArrayList<>();
        expiredLoans.add(expiredLoan);

        when(userRepository.userExists(email)).thenReturn(true);
        when(loanRepository.findLoansByUser(email, false)).thenReturn(expiredLoans);
        doNothing().when(loanRepository).deleteByUserEmail(email);

        // Act
        String result = loanService.deleteUserLoans(email);

        // Assert
        assertEquals("Loans of user successfully deleted.", result);
        verify(loanRepository, times(1)).deleteByUserEmail(email);
    }
}