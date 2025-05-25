package be.ucll.unit.service;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private LoanService loanService;

    // Test subclass for abstract Publication
    private static class TestPublication extends Publication {
        public TestPublication(String title, int publicationYear, int availableCopies) {
            super(title, publicationYear, availableCopies);
        }
    }

    @Test
    void testRegisterLoan_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> loanService.registerLoan(email, LocalDate.now(), List.of(1L)));
    }

    @Test
    void testDeleteUserLoans_Success() {
        String email = "user@example.com";
        User user = new User("John Doe", "password", email, 30);
        Publication pub = new TestPublication("Test Book", 2020, 5);
        Loan loan = new Loan(user, List.of(pub), LocalDate.now().minusDays(40));
        loan.returnPublication();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));

        String result = loanService.deleteUserLoans(email);

        assertEquals("Loans of user successfully deleted.", result);
        verify(loanRepository).deleteAll(List.of(loan));
    }

    @Test
    void testDeleteUserLoans_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> loanService.deleteUserLoans(email));
        assertEquals("User not found", ex.getMessage());
        verifyNoInteractions(loanRepository);
    }

    @Test
    void testDeleteUserLoans_UserHasActiveLoans() {
        String email = "active@example.com";
        User user = new User("Active User", "password", email, 25);
        Publication pub = new TestPublication("Active Book", 2021, 3);
        Loan activeLoan = new Loan(user, List.of(pub), LocalDate.now().minusDays(10));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(List.of(activeLoan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> loanService.deleteUserLoans(email));
        assertEquals("User has active loans.", ex.getMessage());
        verify(loanRepository, never()).deleteAll(any());
    }

    @Test
    void testGetLoansByUser_Success() {
        String email = "user@example.com";
        User user = new User("John Doe", "password", email, 30);
        Publication pub = new TestPublication("Book", 2020, 2);
        Loan loan = new Loan(user, List.of(pub), LocalDate.now());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));

        List<Loan> loans = loanService.getLoansByUser(email, false);
        assertEquals(1, loans.size());
        assertEquals(loan, loans.get(0));
    }

    @Test
    void testGetLoansByUser_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> loanService.getLoansByUser(email, false));
    }
}
