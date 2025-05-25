package be.ucll.unit.service;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.ActiveLoansException;
import be.ucll.service.UserNotFoundException;
import be.ucll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        loanRepository = mock(LoanRepository.class);
        userService = new UserService(userRepository, loanRepository);
    }

    // Existing previous test methods...
    // (I’m leaving a placeholder for your other tests; they’re not shown in the snippet you provided)

    // Story 17 - Delete User Test Cases
    @Test
    void testDeleteUser_Success() {
        // Arrange
        String email = "john.doe@example.com";
        User user = new User("John Doe", "password123", email, 30);

        // Mock finding the user
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Mock finding no active loans
        when(loanRepository.findLoansByUser(email, true)).thenReturn(List.of());

        // Act
        String result = userService.deleteUser(email);

        // Assert
        assertEquals("User successfully deleted", result);

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(loanRepository).findLoansByUser(email, true);
        verify(loanRepository).deleteByUserEmail(email);
        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        // Mock user not found
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(email)
        );

        assertEquals("User does not exist.", exception.getMessage());

        // Verify no further interactions
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUser_UserWithActiveLoans() {
        // Arrange
        String email = "active.loans@example.com";
        User user = new User("Active Loans User", "password123", email, 25);

        // Mock finding the user
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Create a mock active loan
        Publication publication = mock(Publication.class);
        User loanUser = new User("John Doe", "password123", "john.doe@example.com", 30);
        Loan activeLoan = new Loan(loanUser, List.of(publication), LocalDate.now().minusDays(10));

        // Mock finding active loans
        when(loanRepository.findLoansByUser(email, true)).thenReturn(List.of(activeLoan));

        // Act & Assert
        ActiveLoansException exception = assertThrows(
                ActiveLoansException.class,
                () -> userService.deleteUser(email)
        );

        assertEquals("User has active loans.", exception.getMessage());

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(loanRepository).findLoansByUser(email, true);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUser_UserWithOnlyInactiveLoans() {
        // Arrange
        String email = "inactive.loans@example.com";
        User user = new User("Inactive Loans User", "password123", email, 40);

        // Mock finding the user
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Create a mock inactive loan (returned or past due)
        Publication publication = mock(Publication.class);
        Loan inactiveLoan = new Loan(user, List.of(publication), LocalDate.now().minusDays(30));
        inactiveLoan.returnPublication(); // Simulate returning the publication to make it inactive

        // Mock finding no active loans but some inactive ones
        when(loanRepository.findLoansByUser(email, true)).thenReturn(Collections.emptyList());
        when(loanRepository.findLoansByUser(email, false)).thenReturn(List.of(inactiveLoan));

        // Act
        String result = userService.deleteUser(email);

        // Assert
        assertEquals("User successfully deleted", result);

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(loanRepository).findLoansByUser(email, true);
        verify(loanRepository).deleteByUserEmail(email); // Inactive loans deleted
        verify(userRepository).deleteByEmail(email); // User deleted
    }
}